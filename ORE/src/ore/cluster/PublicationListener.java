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
			String[] msgs = textMessage.getText().split("!!!!");
			LogMan.info("Received message: " + msgs[0] + "," + msgs[1]);
			ClusterManager.getInstance().receive(msgs[0], msgs[1]);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
}
