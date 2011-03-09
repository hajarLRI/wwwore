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
		final Topic msgChannel = session.createTopic("topic");
		for(int i=0;i < 10; i++) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					while(true) {
						try {
							Thread.sleep(10);
							StringBuffer hello = new StringBuffer(); 
							for(int j=0;j < 100; j++) {
								hello.append("HELLO ");
							}
							TextMessage message = session.createTextMessage(hello.toString());
							MessageProducer producer = session.createProducer(msgChannel);
							producer.send(message);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			t.start();
		}
	}
}
