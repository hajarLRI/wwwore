package ore.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import ore.util.CPUTimer;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Receiver {
	private static ActiveMQConnectionFactory connectionFactory;
	private static Connection connection;
	private static Session session;
	public static String ip = "localhost:61617";
	
	public static void main(String[] args) throws JMSException {
		final CPUTimer timer = new CPUTimer(100);
		timer.start();
		new Thread(new Runnable() {
			public void run() {
				while(true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("Overall: " + timer.getTotal() + ", Recent: " + timer.getRecent());
				}
			}
		}).start();
		
		connectionFactory = new ActiveMQConnectionFactory("vm:(broker:(tcp://"+ip+"))");
		connection = connectionFactory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Topic subscriptionChannel = session.createTopic("topic");
		MessageConsumer consumer = session.createConsumer(subscriptionChannel, null, true);
		consumer.setMessageListener(
				new MessageListener() {
					@Override
					public void onMessage(Message arg0) { 
						try {
							TextMessage msg = (TextMessage) arg0;
							String x = msg.getText();
							x.toLowerCase();
							System.out.println(x);
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
		);
		connection.start();
	}
}
