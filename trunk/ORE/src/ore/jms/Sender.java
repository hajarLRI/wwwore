package ore.jms;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import ore.util.CPUTimer;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Sender {
	private static ActiveMQConnectionFactory connectionFactory;
	
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
		StringBuffer hello = new StringBuffer(); 
		for(int j=0;j < 10; j++) {
			hello.append("HELLO WORLD");
		}
		final String helloString = hello.toString();
		
		for(int i=0;i < 1000; i++) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						Connection connection = connectionFactory.createConnection();
						Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
						final Queue msgChannel = session.createQueue("topic");
						while(true) {
						//	Thread.sleep(10);
							TextMessage message = null;
							message = session.createTextMessage(helloString);
							MessageProducer producer = session.createProducer(msgChannel);
							producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
							producer.send(message);
							producer.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			t.start();
		}
	}
}
