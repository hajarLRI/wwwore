package ore.cluster;

import java.util.HashSet;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import ore.exception.BrokenCometException;

public class Peer {
	private String ip;
	private ActiveMQConnectionFactory connectionFactory;
	private Topic subscriptionChannel;
	private Topic messageChannel;
	
	private Connection connection;
	
	public Peer(String ip) {
		System.out.println("Peer("+ip+")");
		this.ip = ip;
	}
	
	public void send(String msg) {
		System.out.println("sent("+ip+")");
		Session session = null;
		MessageProducer producer = null;
		try {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			TextMessage message = session.createTextMessage(msg);
			producer = session.createProducer(subscriptionChannel);
			producer.send(message);
			session.close();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void subscriptionNotice(String room, boolean join) {
		System.out.println("subscriptionNotice("+room+";"+"join"+")");
		Session session = null;
		MessageProducer producer = null;
		String prefix = null;
		if(join) {
			prefix = "join,";
		} else {
			prefix = "leave,";
		}
		try {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			TextMessage message = session.createTextMessage(prefix + room);
			producer = session.createProducer(subscriptionChannel);
			producer.send(message);
			session.close();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void start() throws JMSException {
		System.out.println("start("+")");
		connectionFactory = new ActiveMQConnectionFactory("vm:broker:(tcp://"+ip+")");
		connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		subscriptionChannel = session.createTopic("sub" + ip);
		messageChannel = session.createTopic("msg" + ip);
		final MessageConsumer consumer = session.createConsumer(subscriptionChannel, null, true);
		consumer.setMessageListener(new MessageListener() {
			public void onMessage(Message msg) {
				TextMessage textMessage = (TextMessage) msg;
				try {
					String[] info = textMessage.getText().split(",");
					if(info[0].equals("join")) {
						Set<Peer> ps = ClusterManager.getInstance().subscribers.get(info[1]);
						if(ps == null) {
							ps = new HashSet<Peer>();
						}
						ps.add(Peer.this);
					} else {
						Set<Peer> ps = ClusterManager.getInstance().subscribers.get(info[1]);
						if(ps == null) {
							ps = new HashSet<Peer>();
						}
						ps.remove(Peer.this);
					}
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
		
		final MessageConsumer consumer2 = session.createConsumer(messageChannel, null, true);
		consumer2.setMessageListener(new MessageListener() {
			public void onMessage(Message msg) {
				System.out.println("onMessage("+msg+")");
				TextMessage textMessage = (TextMessage) msg;
				try {
					String[] msgs = textMessage.getText().split("!!!!");
					ClusterManager.getInstance().receive(msgs[0], msgs[1]);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		}); 
		session.close();
	}
	
	public void connect() throws JMSException {
		System.out.println("connect("+")");
		boolean done = false;
		while(!done) {
			try {
				connectionFactory = new ActiveMQConnectionFactory("tcp://"+ip);
				connection = connectionFactory.createConnection();
				done = true;
			} catch (Exception e ) {
				System.out.print("Connection is null");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		connection.start();
		Session session = null;
		try {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			subscriptionChannel = session.createTopic("sub" + ip);
			messageChannel = session.createTopic("msg" + ip);
			final MessageConsumer consumer = session.createConsumer(subscriptionChannel, null, true);
			consumer.setMessageListener(new MessageListener() {
				public void onMessage(Message msg) {
					TextMessage textMessage = (TextMessage) msg;
					try {
						String[] info = textMessage.getText().split(",");
						if(info[0].equals("join")) {
							Set<Peer> ps = ClusterManager.getInstance().subscribers.get(info[1]);
							if(ps == null) {
								ps = new HashSet<Peer>();
							}
							ps.add(Peer.this);
						} else {
							Set<Peer> ps = ClusterManager.getInstance().subscribers.get(info[1]);
							if(ps == null) {
								ps = new HashSet<Peer>();
							}
							ps.remove(Peer.this);
						}
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			});
			
			final MessageConsumer consumer2 = session.createConsumer(messageChannel, null, true);
			consumer2.setMessageListener(new MessageListener() {
				public void onMessage(Message msg) {
					System.out.println("onMessage("+msg+")");
					TextMessage textMessage = (TextMessage) msg;
					try {
						String[] msgs = textMessage.getText().split("!!!!");
						ClusterManager.getInstance().receive(msgs[0], msgs[1]);
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			});
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		session.close();
	}
	
}
