package ore.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.json.JSONArray;

public class JMSMachine extends Machine {

	private static ActiveMQConnectionFactory connectionFactory;
	private static Connection connection;
	private static Map<String, BlockingQueue<JSONArray>> buffer = new HashMap<String, BlockingQueue<JSONArray>>(); 
	
	public JMSMachine(String ip, String port, String jmsPort) {
		super(ip, port, jmsPort);
		connectionFactory = new ActiveMQConnectionFactory("vm:(broker:(tcp://"+Config.clientJMS+"))");
		try {
			connection = connectionFactory.createConnection();
			connection.start();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public JSONArray receiveMessages(String sessionID, String redirectOK) {
		BlockingQueue<JSONArray> buf = buffer.get(sessionID);
		if(buf == null) {
			buf = new LinkedBlockingQueue(); 
			buffer.put(sessionID, buf);
			try {
				createConsumer(connection, sessionID, buf);
			} catch (JMSException e) {
				e.printStackTrace();
			}
		} 
		try {
			return buf.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Queue failure: This should not happen");
	}
	
	private static void createConsumer(Connection connection, String sessionID, BlockingQueue<JSONArray> buf) throws JMSException {
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		createConsumer(session, sessionID, buf);
	}
	
	private static void createConsumer(Session session, String sessionID, final BlockingQueue<JSONArray> buf) throws JMSException {
		Topic subscriptionChannel = session.createTopic("topic" + sessionID);
		MessageConsumer consumer = session.createConsumer(subscriptionChannel, null, true);
		
		consumer.setMessageListener(
				new MessageListener() {
					@Override
					public void onMessage(Message arg0) { 
						try {
							TextMessage msg = (TextMessage) arg0;
							JSONArray arr = new JSONArray(msg.getText());
							buf.add(arr);
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
		);
	}

}
