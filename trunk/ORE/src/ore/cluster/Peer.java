package ore.cluster;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import ore.subscriber.Subscriber;
import ore.util.CountingMap;
import ore.util.LogMan;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Peer {
	private String ip;
	private ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private CountingMap interest = new CountingMap(true);
	
	public void inc(Subscriber s) {
		interest.inc(s);
	}
	
	public void dec(Subscriber s) {
		interest.dec(s);
	}
	
	public Subscriber max() {
		return interest.max();
	}
	
	public String getIP() {
		return ip;
	}
	
	//Flyweight pattern
	private static Map<String, Peer> instances = new HashMap<String, Peer>();
	private Peer(String ip) {
		LogMan.info("Peer("+ip+")");
		this.ip = ip;
	}
	
	public static Peer create(String ip) {
		Peer p = instances.get(ip);
		if(p == null) {
			p = new Peer(ip);
			instances.put(ip, p);
		}
		return p;
	}
	
	/*public void send(Key k, String user, String msg) {
		LogMan.info("sent("+ip+")");
		MessageProducer producer = null;
		try {
			TextMessage message = createMessage(session, k.toString(), msg);
			message.setStringProperty("user", user);
			Topic msgChannel = session.createTopic("msg" + ip.replace('.', 'x').replace(':', 'y'));
			producer = session.createProducer(msgChannel);
			producer.send(message);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}*/
	
	public void sendMessage(Key k, String user, String operation, String from, String msg) {
		LogMan.info("sendMessage(" + k.toString() + ";" + operation + ", user=" + user + ") to " + ip);
		//int userId = Integer.parseInt(user);
		MessageProducer producer = null;
		try {
			TextMessage message = createMessage(session, operation, k.toString(), 0, from, msg);
			Topic msgChannel = session.createTopic("sub" + ip.replace('.', 'x').replace(':', 'y'));
			producer = session.createProducer(msgChannel);
			producer.send(message);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void start() throws JMSException {
		LogMan.info("start("+")");
		connectionFactory = new ActiveMQConnectionFactory("vm:(broker:(tcp://"+ip+")?broker.persistent=false)");
		connection = connectionFactory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Topic subscriptionChannel = session.createTopic("sub" + ip.replace('.', 'x').replace(':', 'y'));
		//Topic messageChannel = session.createTopic("msg" + ip.replace('.', 'x').replace(':', 'y'));
		MessageConsumer consumer = session.createConsumer(subscriptionChannel, null, true);
		consumer.setMessageListener(new SubscriptionListener());
		//MessageConsumer consumer2 = session.createConsumer(messageChannel, null, true);
		//consumer2.setMessageListener(new PublicationListener()); 
		connection.start();
	}
	
	public void connect() throws JMSException {
		LogMan.info("connect("+")");
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
					e1.printStackTrace();
				}
			}
		}
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		connection.start();
	}
	
	private TextMessage createMessage(Session session, String operation, String key, int user, String from, String msg) throws JMSException {
		TextMessage message = session.createTextMessage(msg);
		message.setStringProperty("operation", operation);
		message.setStringProperty("key", key);
		message.setIntProperty("user", user);
		message.setStringProperty("from", from);
		return message;
	}
	
}
