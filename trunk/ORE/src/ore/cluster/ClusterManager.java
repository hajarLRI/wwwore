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
	
	private static ClusterManager instance;
	private Peer self;
	private List<Peer> peers = new LinkedList<Peer>();
	Map<String, Set<RemoteSubscriber>> subscribers = new HashMap<String, Set<RemoteSubscriber>>();
	Map<String, Set<Subscription>> local = new HashMap<String, Set<Subscription>>();
	Map<Subscription, String> inverted = new HashMap<Subscription, String>();
	
	public void clear() {
		subscribers.clear();
		local.clear();
		inverted.clear();
	}
	
	private ClusterManager(String selfIP, String[] peerIP) throws Exception  {
		self = new Peer(selfIP);
		self.start();
		for(String ip : peerIP) {
			Peer p = new Peer(ip);
			peers.add(p);
			p.connect();
		}
	}
	
	public void receive(String room, String msg) {
		Set<Subscription> s = local.get(room);
		if(s != null) {
			for(Subscription sub : s) {
				try {
					sub.print(msg.toCharArray());
				} catch (BrokenCometException e) {
					//TODO Jetty specific problem
					//e.printStackTrace();
				}
			}
		}
	}
	
	public void delete(Subscription sub) {
		String room = inverted.get(sub);
		Set<Subscription> s = local.get(room);
		if(s == null) {
			throw new IllegalStateException();
		}
		if(s.size() == 1) {
			for(Peer p : peers) {
				//TODO Handle deletes
				p.subscriptionNotice(room, "", false);
			}
		}
		s.remove(sub);
	}
	
	public void delete(String room, Subscription sub) {
		Set<Subscription> s = local.get(room);
		if(s == null) {
			throw new IllegalStateException();
		}
		if(s.size() == 1) {
			for(Peer p : peers) {
				//Handle deletes
				p.subscriptionNotice(room, "", false);
			}
		}
		s.remove(sub);
	}
   
	public void subscribe(String userID, final Subscription subscription, String className, Serializable identifier, String propertyName, EventType type) {
		Set<Subscription> s = local.get(identifier.toString());
		if(s == null) {
			s = new HashSet<Subscription>();
			local.put(identifier.toString(), s);
		}
		if(s.size() == 0) {
			for(Peer p : peers) {
				p.subscriptionNotice(identifier.toString(), userID, true);
			}
		}
		s.add(subscription);
		inverted.put(subscription, identifier.toString());
	}

	public void publish(char[] data, Event event) {
		String key = Metadata.getPrimaryKeyValue(event.getEntity()).toString();
		key += "$" + event.getEntity().getClass().getName();
		Set<RemoteSubscriber> ps = subscribers.get(key);
		if(ps != null) {
			Set<Peer> peers = new HashSet<Peer>();
			for(RemoteSubscriber p : ps) {
				peers.add(p.getHost());
			}
			for(Peer p : peers) {
				p.send(key, "", new String(data));
			}
		}
	}
	
	public static void init(String ip, String[] peerIP) throws Exception {
		if(ip == null) {
			instance = new NullClusterManager();
		} else {
			instance = new ClusterManager(ip, peerIP);
		}
	}
	
	public static ClusterManager getInstance() {
		if(instance == null) {
			throw new IllegalStateException();
		}
		return instance;
	}
	
	private ClusterManager() {}
	
	
	private static class NullClusterManager extends ClusterManager {

		private NullClusterManager() {
			super();
		}
		
		@Override
		public void receive(String room, String msg) {
			
		}

		@Override
		public void delete(Subscription sub) {
			
		}

		@Override
		public void delete(String room, Subscription sub) {
			
		}

		@Override
		public void subscribe(String userID, Subscription subscription, String className,
				Serializable identifier, String propertyName, EventType type) {
			
		}

		@Override
		public void publish(char[] data, Event event) {
		
		}
		
	}
}
