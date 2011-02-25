package ore.cluster;

import java.util.HashSet;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import ore.api.ORE;
import ore.hypergraph.HyperEdge;
import ore.util.LogMan;

public class SubscriptionListener implements MessageListener {
	
	public void onMessage(Message msg) {
		
		TextMessage textMessage = (TextMessage) msg;
		//System.out.println("!!!!!"+textMessage.toString());
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
			System.out.println("onMessage(" + key + ";" + message + ")");
			if(operation.equals("join")) {
				LogMan.info("Remote peer joins room: " + key);
				//HyperEdge<Integer, Integer> hyperEdge = ORE.getGraph().getEdge(Integer.parseInt(content));
				//ORE.getGraph().putNodeOnEdge(user, hyperEdge);
				
				RemoteSubscriber rs = new RemoteSubscriber(user+"", ClusterManager.getInstance().getPeer(from));
				System.out.println("Remote subscriber is: " + rs.getHost().getIP());
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
