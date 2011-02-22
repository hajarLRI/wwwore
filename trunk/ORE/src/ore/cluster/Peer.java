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

import ore.util.LogMan;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Peer {
	private String ip;
	private ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	
	public Peer(String ip) {
		System.out.println("Peer("+ip+")");
		this.ip = ip;
	}
	
	public void send(Key k, String user, String msg) {
		System.out.println("sent("+ip+")");
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
	}
	
	public void subscriptionNotice(Key k, String user, boolean join) {
		System.out.println("subscriptionNotice("+user+";"+"join"+")");
		int userId = Integer.parseInt(user);
		MessageProducer producer = null;
		String operation = null;
		if(join) {
			operation = "join";
		} else {
			operation = "leave";
		}
		try {
			TextMessage message = createMessage(session, operation, k.toString());
			message.setIntProperty("user", userId);
			Topic msgChannel = session.createTopic("sub" + ip.replace('.', 'x').replace(':', 'y'));
			producer = session.createProducer(msgChannel);
			producer.send(message);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void start() throws JMSException {
		System.out.println("start("+")");
		connectionFactory = new ActiveMQConnectionFactory("vm:broker:(tcp://"+ip+")");
		connection = connectionFactory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Topic subscriptionChannel = session.createTopic("sub" + ip.replace('.', 'x').replace(':', 'y'));
		Topic messageChannel = session.createTopic("msg" + ip.replace('.', 'x').replace(':', 'y'));
		MessageConsumer consumer = session.createConsumer(subscriptionChannel, null, true);
		consumer.setMessageListener(new SubscriptionListener(this));
		MessageConsumer consumer2 = session.createConsumer(messageChannel, null, true);
		consumer2.setMessageListener(new PublicationListener()); 
		connection.start();
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
					e1.printStackTrace();
				}
			}
		}
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		connection.start();
	}
	
	private TextMessage createMessage(Session session, String operation, String s) throws JMSException {
		TextMessage message = session.createTextMessage(s);
		message.setStringProperty("operation", operation);
		return message;
	}
	
}
