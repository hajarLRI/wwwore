package ore.cluster;

import java.util.HashSet;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

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
			String content = textMessage.getText();
			if(operation.equals("join")) {
				LogMan.info("Remote peer joins room: " + content);
				Set<Peer> ps = ClusterManager.getInstance().subscribers.get(content);
				if(ps == null) {
					ps = new HashSet<Peer>();
					ClusterManager.getInstance().subscribers.put(content, ps);
				}
				ps.add(me);
			} else {
				Set<Peer> ps = ClusterManager.getInstance().subscribers.get(content);
				if(ps != null) {
					LogMan.info("Remote peer leaves room: " + content);
					ps.remove(me);
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
}
