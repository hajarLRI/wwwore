package ore.cluster;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import ore.util.LogMan;

public class SubscriptionListener implements MessageListener {
	
	public void onMessage(Message msg) {
		
		TextMessage textMessage = (TextMessage) msg;
		//LogMan.info("!!!!!"+textMessage.toString());
		try {
			String operation = textMessage.getStringProperty("operation");
			if(operation.equals("msg")) {
				System.out.print("");
			}
			int user = textMessage.getIntProperty("user");
			String message = textMessage.getText();
			String key = textMessage.getStringProperty("key");
			Key k = Key.parse(key);
			String from = textMessage.getStringProperty("from");
			LogMan.info("onMessage(" + key + ";" + message + ")");
			if(operation.equals("join")) {
				LogMan.info("Remote peer joins room: " + key);
				//HyperEdge<Integer, Integer> hyperEdge = ORE.getGraph().getEdge(Integer.parseInt(content));
				//ORE.getGraph().putNodeOnEdge(user, hyperEdge);
				
				RemoteSubscriber rs = new RemoteSubscriber(user+"", ClusterManager.getInstance().getPeer(from));
				LogMan.info("Remote subscriber is: " + rs.getHost().getIP());
				ClusterManager.getInstance().addSubscriber(k, rs);
			} else if(operation.equals("msg")) {
				LogMan.info("Received message: " + key + "," + message);
				ClusterManager.getInstance().receive(k, message);
			} else {
				RemoteSubscriber rs = new RemoteSubscriber(user+"", ClusterManager.getInstance().getPeer(from));
				ClusterManager.getInstance().removeSubscriber(k, rs);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
}
