package ore.cluster;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;

import org.apache.activemq.ActiveMQConnectionFactory;

import ore.api.Event;
import ore.api.Event.EventType;
import ore.exception.BrokenCometException;
import ore.hibernate.Metadata;
import ore.subscriber.Subscription;

public class ClusterManager {
	
	private static ClusterManager instance = new ClusterManager();
	private Peer self;
	private List<Peer> peers = new LinkedList<Peer>();
	Map<String, Set<Peer>> subscribers = new HashMap<String, Set<Peer>>();
	Map<String, Set<Subscription>> local = new HashMap<String, Set<Subscription>>();
	
	private String selfIP = "10.220.194.144:61616";
	private String[] peerIP = {"10.194.142.224:61616"};
	
	private ActiveMQConnectionFactory connectionFactory;
	private Topic oreTopic;
	Connection connection;
	
	private ClusterManager()  {
		
		/*try {
			connectionFactory = new ActiveMQConnectionFactory("vm:broker:(tcp://"+selfIP+")");
			connection = connectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			oreTopic = session.createTopic("ORE");
			
		    session.close();
		} catch(Exception e) {
			throw new RuntimeException(e);
	}*/
		
		System.out.println("ClusterManager()");
		try {
			self = new Peer(selfIP);
			self.start();
			for(String ip : peerIP) {
				Peer p = new Peer(ip);
				peers.add(p);
				p.connect();
			}
		} catch(Exception e)  {
			e.printStackTrace();
		}
	}
	
	public void receive(String room, String msg) {
		System.out.println("receive("+room+"; "+msg+")");
		Set<Subscription> s = local.get(room);
		if(s != null) {
			for(Subscription sub : s) {
				try {
					sub.print(msg.toCharArray());
				} catch (BrokenCometException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void delete(String room, Subscription sub) {
		System.out.println("delete("+room+"; "+sub.toString()+")");
		Set<Subscription> s = local.get(room);
		if(s == null) {
			throw new IllegalStateException();
		}
		if(s.size() == 1) {
			for(Peer p : peers) {
				p.subscriptionNotice(room, false);
			}
		}
		s.remove(sub);
	}
   
	public void subscribe(final Subscription subscription, String className, Serializable identifier, String propertyName, EventType type) {
//		Session session = null;
//		try {
//			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//			String messageSelector = createMessageSelector(className, identifier, propertyName, type);
//			Topic topic = session.createTopic(identifier.toString());
//			final MessageConsumer consumer = session.createConsumer(topic, messageSelector, true);
//			consumer.setMessageListener(new MessageListener() {
//				public void onMessage(Message msg) {
//					TextMessage textMessage = (TextMessage) msg;
//					try {
//						
//						subscription.print(textMessage.getText().toCharArray());
//					} catch (BrokenCometException e) {
//						try {
//							consumer.close();
//						} catch (JMSException e1) {
//							e1.printStackTrace();
//						}
//						e.printStackTrace();
//					} catch (JMSException e) {
//						e.printStackTrace();
//					}
//				}
//			});
//		} catch(Exception e) {
//			throw new RuntimeException(e);
//		}
		
		System.out.println("subscribe(...)");
		//Set<Subscription> s = local.get(identifier.toString());
		//if(s == null) {
		//	s = new HashSet<Subscription>();
		//}
		//if(s.size() == 0) {
			for(Peer p : peers) {
				p.subscriptionNotice(identifier.toString(), true);
			}
		//}
		//s.add(subscription);
	}

	public void publish(char[] data, Event event) {
//		Session session = null;
//		MessageProducer producer = null;
//		try {
//			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//			Topic topic = session.createTopic(Metadata.getPrimaryKeyValue(event.getEntity()).toString());
//			TextMessage message = createMessage(session, data, event);
//			producer = session.createProducer(topic);
//			producer.send(message);
//			session.close();
//		} catch(Exception e) {
//			throw new RuntimeException(e);
//		}
		System.out.println("publish(...)");
		String room = Metadata.getPrimaryKeyValue(event.getEntity()).toString();
		//Set<Peer> ps = subscribers.get(room);
		//if(ps != null) {
			for(Peer p : peers) {
				p.send(room + "!!!!" + new String(data));
			}
		//}
	}
	
	public static ClusterManager getInstance() {
		System.out.println("getInstance()");
		return instance;
	}
	
	
	/*
	 public class ClusterManager {
	private static ClusterManager instance = new ClusterManager();
	private ConnectionFactory connectionFactory;
	//private Topic oreTopic;
	Connection connection;
	
	private ClusterManager() {
		try {
				InitialContext ic = new InitialContext();
				connectionFactory = (ConnectionFactory) ic.lookup("java:comp/env/jms/connectionFactory");
				connection = connectionFactory.createConnection();
				connection.start();
				//Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				//oreTopic = session.createTopic("ORE");
				
				//session.close();
			} catch(Exception e) {
				throw new RuntimeException(e);
		}
	}
   
	public void subscribe(final Subscription subscription, String className, Serializable identifier, String propertyName, EventType type) {
		
		Session session = null;
		try {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			String messageSelector = createMessageSelector(className, identifier, propertyName, type);
			Topic topic = session.createTopic(identifier.toString());
			final MessageConsumer consumer = session.createConsumer(topic, messageSelector, true);
			consumer.setMessageListener(new MessageListener() {
				public void onMessage(Message msg) {
					TextMessage textMessage = (TextMessage) msg;
					try {
						
						subscription.print(textMessage.getText().toCharArray());
					} catch (BrokenCometException e) {
						try {
							consumer.close();
						} catch (JMSException e1) {
							e1.printStackTrace();
						}
						e.printStackTrace();
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			});
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
	}

	public void publish(char[] data, Event event) {
		Session session = null;
		MessageProducer producer = null;
		try {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Topic topic = session.createTopic(Metadata.getPrimaryKeyValue(event.getEntity()).toString());
			TextMessage message = createMessage(session, data, event);
			producer = session.createProducer(topic);
			producer.send(message);
			session.close();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private TextMessage createMessage(Session session, char[] data, Event event) throws JMSException {
		TextMessage message = session.createTextMessage(new String(data));
		message.setStringProperty("className", event.getEntity().getClass().getName());
		message.setStringProperty("propertyName", event.getPropertyName());
		//TODO Handle identifier types with no toString method
		message.setStringProperty("identifier", Metadata.getPrimaryKeyValue(event.getEntity()).toString());
		message.setStringProperty("type", event.getType().name());
		return message;
	}
	
	private String createMessageSelector(String className, Serializable identifier, String propertyName, EventType type) {
		StringBuffer sb = new StringBuffer();
		sb.append("className='" + className + "'");
		sb.append(" AND ");
		//TODO Handle identifier types with no toString method
		sb.append("identifier='" + identifier.toString() + "'");
		sb.append(" AND ");
		sb.append("propertyName='" + propertyName + "'");
		sb.append(" AND ");
		sb.append("type='" + type.name() + "'");
		return sb.toString();
	}
	
	public static ClusterManager getInstance() {
		return instance;
	}
}

	 */
}
