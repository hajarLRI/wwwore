package ore.cluster;

import java.io.Serializable;
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
import javax.naming.InitialContext;

import org.apache.activemq.ActiveMQConnectionFactory;

import ore.api.Event;
import ore.api.Event.EventType;
import ore.exception.BrokenCometException;
import ore.hibernate.Metadata;

public class Peer {
	private String ip;
	private ActiveMQConnectionFactory connectionFactory;
	
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
			TextMessage message = createMessage(session, msg);
			Topic subscriptionChannel = session.createTopic("hello");
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
			TextMessage message = createMessage(session, prefix + room);
			Topic subscriptionChannel = session.createTopic("hello");
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
		/*subscriptionChannel = session.createTopic("sub" + ip.replace('.', 'x').replace(':', 'y'));
		messageChannel = session.createTopic("msg" + ip.replace('.', 'x').replace(':', 'y'));*/
		Topic subscriptionChannel = session.createTopic("hello");
		Topic messageChannel = session.createTopic("hello");
		final MessageConsumer consumer = session.createConsumer(subscriptionChannel, createMessageSelector(), true);
		consumer.setMessageListener(new MessageListener() {
			public void onMessage(Message msg) {
				System.out.println("onMessage("+msg+")");
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
		
		final MessageConsumer consumer2 = session.createConsumer(messageChannel, createMessageSelector(), true);
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
	}
	
	private TextMessage createMessage(Session session, String s) throws JMSException {
		TextMessage message = session.createTextMessage(s);
		message.setStringProperty("type", "hello");
		return message;
	}
	
	private String createMessageSelector() {
		StringBuffer sb = new StringBuffer();
		sb.append("type='hello'");
		return sb.toString();
	}
	
}
