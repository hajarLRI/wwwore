package ore.jms;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import ore.util.CPUTimer;
import ore.util.LogMan;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Sender {
	private static ActiveMQConnectionFactory connectionFactory;
	private static Connection connection;
	private static Session session;
	
	public static void main(String[] args) throws Exception {
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
		
		boolean done = false;
		while(!done) {
			try {
				connectionFactory = new ActiveMQConnectionFactory("tcp://"+Receiver.ip);
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
		Topic msgChannel = session.createTopic("topic");
		while(true) {
			Thread.sleep(1000);
			TextMessage message = session.createTextMessage("HELLO HELLO HELLO HELLO HELLO HELLO HELLO HELLO");
			MessageProducer producer = session.createProducer(msgChannel);
			producer.send(message);
		}
	}
}
