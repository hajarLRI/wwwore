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

public class Peer {
	private String ip;
	private ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	
	public Peer(String ip) {
		System.out.println("Peer("+ip+")");
		this.ip = ip;
	}
	
	public void send(String msg) {
		System.out.println("sent("+ip+")");
		MessageProducer producer = null;
		try {
			TextMessage message = createMessage(session, msg);
			Topic msgChannel = session.createTopic("msg" + ip.replace('.', 'x').replace(':', 'y'));
			producer = session.createProducer(msgChannel);
			producer.send(message);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void subscriptionNotice(String room, boolean join) {
		System.out.println("subscriptionNotice("+room+";"+"join"+")");
		MessageProducer producer = null;
		String prefix = null;
		if(join) {
			prefix = "join,";
		} else {
			prefix = "leave,";
		}
		try {
			TextMessage message = createMessage(session, prefix + room);
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
		//Topic subscriptionChannel = session.createTopic("hello");
		//Topic messageChannel = session.createTopic("hello");
		final MessageConsumer consumer = session.createConsumer(subscriptionChannel, null, true);
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
							ClusterManager.getInstance().subscribers.put(info[1], ps);
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
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		connection.start();
	}
	
	private TextMessage createMessage(Session session, String s) throws JMSException {
		TextMessage message = session.createTextMessage(s);
		message.setStringProperty("type", "hello");
		message.setStringProperty("identifier", "hello");
		return message;
	}
	
	private String createMessageSelector() {
		StringBuffer sb = new StringBuffer();
		sb.append("type='hello'");
		return sb.toString();
	}
	
}
