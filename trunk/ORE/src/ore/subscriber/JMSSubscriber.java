package ore.subscriber;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import ore.exception.BrokenCometException;
import ore.servlet.ClusterStart;
import ore.util.LogMan;

import org.json.JSONArray;

public class JMSSubscriber extends Subscriber {
	Connection connection;
	
	
	public JMSSubscriber(String id, JSONArray digest) throws Exception {
		super(id, digest);
		connection = ClusterStart.connectionFactory.createConnection();
	}
	
	public synchronized void print(String data) throws BrokenCometException {
		try {
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			LogMan.info("Subscriber " + id + " got pushed");
			final Topic msgChannel = session.createTopic("topic" + id);
			TextMessage message = session.createTextMessage("[" + data + "]");
			MessageProducer producer = session.createProducer(msgChannel);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			producer.send(message);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
}
