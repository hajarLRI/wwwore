package ore.cluster;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import ore.util.LogMan;

public class PublicationListener implements MessageListener {
	
	public void onMessage(Message msg) {
		TextMessage textMessage = (TextMessage) msg;
		try {
			String content = textMessage.getText();
			String key = textMessage.getStringProperty("operation");
			LogMan.info("Received message: " + key + "," + content);
			ClusterManager.getInstance().receive(Key.parse(key), content);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
}
