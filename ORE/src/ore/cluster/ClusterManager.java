package ore.cluster;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;

import ore.api.Event;
import ore.api.Event.EventType;
import ore.exception.BrokenCometException;
import ore.hibernate.Metadata;
import ore.subscriber.Subscription;

public class ClusterManager {
	
	private static ClusterManager instance = new ClusterManager();
	private Peer self;
	private List<Peer> peers = new LinkedList<Peer>();
	Map<String, Set<Peer>> subscribers = new HashMap<String, Set<Peer>>();
	Map<String, Set<Subscription>> local = new HashMap<String, Set<Subscription>>();
	
	private String selfIP = "10.194.142.224:61616";
	private String[] peerIP = {"10.122.102.26:61616"};
	
	private ClusterManager()  {
		try {
			self = new Peer(selfIP);
			self.start();
			for(String ip : peerIP) {
				Peer p = new Peer(ip);
				peers.add(p);
				p.connect();
			}
		} catch(Exception e)  {
			e.printStackTrace();
		}
	}
	
	public void receive(String room, String msg) {
		Set<Subscription> s = local.get(room);
		if(s != null) {
			for(Subscription sub : s) {
				try {
					sub.print(msg.toCharArray());
				} catch (BrokenCometException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void delete(String room, Subscription sub) {
		Set<Subscription> s = local.get(room);
		if(s == null) {
			throw new IllegalStateException();
		}
		if(s.size() == 1) {
			for(Peer p : peers) {
				p.subscriptionNotice(room, false);
			}
		}
		s.remove(sub);
	}
   
	public void subscribe(final Subscription subscription, String className, Serializable identifier, String propertyName, EventType type) {
		Set<Subscription> s = local.get(identifier.toString());
		if(s == null) {
			s = new HashSet<Subscription>();
		}
		if(s.size() == 0) {
			for(Peer p : peers) {
				p.subscriptionNotice(identifier.toString(), true);
			}
		}
		s.add(subscription);
	}

	public void publish(char[] data, Event event) {
		String room = Metadata.getPrimaryKeyValue(event.getEntity()).toString();
		Set<Peer> ps = subscribers.get(room);
		if(ps != null) {
			for(Peer p : ps) {
				p.send(room + "!!!!" + new String(data));
			}
		}
	}
	
	public static ClusterManager getInstance() throws JMSException {
		return instance;
	}
}
