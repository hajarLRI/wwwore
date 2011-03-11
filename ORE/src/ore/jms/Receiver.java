package ore.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import ore.util.CPUTimer;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Receiver {
	private static ActiveMQConnectionFactory connectionFactory;
	public static String ip = "localhost:61617";
	static long startTime = -1;
	static int msgs = 0;
	
	
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
		connectionFactory.setUseDedicatedTaskRunner(false);
		Connection connection = connectionFactory.createConnection();
		for(int i=0; i < 100; i++) {
			createConsumer(connection);
		}
		connection.start();
	}
	
	private static void createConsumer(Connection connection) throws JMSException {
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		createConsumer(session);
	}
	
	private static void createConsumer(Session session) throws JMSException {
		Queue subscriptionChannel = session.createQueue("topic");
		MessageConsumer consumer = session.createConsumer(subscriptionChannel, null, true);
		
		consumer.setMessageListener(
				new MessageListener() {
					@Override
					public void onMessage(Message arg0) { 
						try {
							if(startTime == -1) {
								startTime = System.currentTimeMillis();
							}
							msgs++;
							long current = System.currentTimeMillis();
							long elapsed = (current-startTime)/1000;
							if((msgs % 50000) == 0) {
								//System.out.println(msgs[0]/(double)elapsed);
							}
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
	}
	
}
