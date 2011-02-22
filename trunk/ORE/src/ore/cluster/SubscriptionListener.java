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
	private Peer me;
	
	public SubscriptionListener(Peer me) {
		this.me = me;
	}
	
	public void onMessage(Message msg) {
		//System.out.println("onMessage("+msg+")");
		TextMessage textMessage = (TextMessage) msg;
		try {
			String operation = textMessage.getStringProperty("operation");
			int user = textMessage.getIntProperty("user");
			String content = textMessage.getText();
			if(operation.equals("join")) {
				LogMan.info("Remote peer joins room: " + content);
				//HyperEdge<Integer, Integer> hyperEdge = ORE.getGraph().getEdge(Integer.parseInt(content));
				//ORE.getGraph().putNodeOnEdge(user, hyperEdge);
				Set<RemoteSubscriber> ps = ClusterManager.getInstance().subscribers.get(content);
				if(ps == null) {
					ps = new HashSet<RemoteSubscriber>();
					ClusterManager.getInstance().subscribers.put(Key.parse(content), ps);
				}
				RemoteSubscriber rs = new RemoteSubscriber(user+"", me);
				ps.add(rs);
			} else {
				Set<RemoteSubscriber> ps = ClusterManager.getInstance().subscribers.get(content);
				if(ps != null) {
					LogMan.info("Remote peer leaves room: " + content);
					RemoteSubscriber rs = new RemoteSubscriber(user+"", me);
					ps.remove(rs);
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
}
