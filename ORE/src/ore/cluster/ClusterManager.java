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
	Map<Key, Set<RemoteSubscriber>> subscribers = new HashMap<Key, Set<RemoteSubscriber>>();
	Map<Key, Set<Subscription>> local = new HashMap<Key, Set<Subscription>>();
	Map<Subscription, Key> inverted = new HashMap<Subscription, Key>();
	
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
	
	public void receive(Key key, String msg) {
		Set<Subscription> s = local.get(key);
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
	
	/*public void delete(Subscription sub) {
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
	}*/
   
	public void subscribe(String userID, final Subscription subscription, String className, String id, String propertyName, EventType type) {
		Key key = new Key(className, id, propertyName);
		Set<Subscription> s = local.get(key);
		if(s == null) {
			s = new HashSet<Subscription>();
			local.put(key, s);
		}
		if(s.size() == 0) {
			for(Peer p : peers) {
				p.subscriptionNotice(key, userID, true);
			}
		}
		s.add(subscription);
		inverted.put(subscription, key);
	}

	public void publish(char[] data, String className, String id, String propertyName) {
		Key key = new Key(className, id, propertyName);
		Set<RemoteSubscriber> ps = subscribers.get(key);
		if(ps != null) {
			Set<Peer> peers = new HashSet<Peer>();
			for(RemoteSubscriber p : ps) {
				peers.add(p.getHost());
			}
			for(Peer p : peers) {
				p.send(key, "", new String(data));
			}
		} else {
			System.out.println("Null: " + id);
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
		public void receive(Key key, String msg) {
			
		}

	/*	@Override
		public void delete(Subscription sub) {
			
		}

		@Override
		public void delete(String room, Subscription sub) {
			
		}*/

		@Override
		public void subscribe(String userID, Subscription subscription, String className,
				String identifier, String propertyName, EventType type) {
			
		}

		@Override
		public void publish(char[] data, String className, String id, String propertyName) {
		
		}
		
	}
}
