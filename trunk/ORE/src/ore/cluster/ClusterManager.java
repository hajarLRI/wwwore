package ore.cluster;

import java.io.Serializable;

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

import ore.api.Event;
import ore.api.Event.EventType;
import ore.exception.BrokenCometException;
import ore.hibernate.Metadata;
import ore.subscriber.Subscription;

public class ClusterManager {
	private static ClusterManager instance = new ClusterManager();
	private ConnectionFactory connectionFactory;
	private Topic oreTopic;
	Connection connection;
	
	private ClusterManager() {
		try {
				InitialContext ic = new InitialContext();
				connectionFactory = (ConnectionFactory) ic.lookup("java:comp/env/jms/connectionFactory");
				connection = connectionFactory.createConnection();
				connection.start();
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				oreTopic = session.createTopic("ORE");
				session.close();
			} catch(Exception e) {
				throw new RuntimeException(e);
		}
	}
   
	public void subscribe(final Subscription subscription, String className, Serializable identifier, String propertyName, EventType type) {
		
		Session session = null;
		try {
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			String messageSelector = createMessageSelector(className, identifier, propertyName, type);
			final MessageConsumer consumer = session.createConsumer(oreTopic, messageSelector, true);
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
			TextMessage message = createMessage(session, data, event);
			producer = session.createProducer(oreTopic);
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
