package ore.cluster;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ore.api.Event.EventType;
import ore.exception.BrokenCometException;
import ore.subscriber.Subscription;

public class ClusterManager {
	
	private static ClusterManager instance;
	private Peer self;
	private Map<String, Peer> peers = new HashMap<String, Peer>();
	private Map<Key, Set<RemoteSubscriber>> subscribers = new HashMap<Key, Set<RemoteSubscriber>>();

	public Peer getPeer(String ip) {
		return peers.get(ip);
	}
	
	public synchronized void addSubscriber(Key k, RemoteSubscriber s) {
		Set<RemoteSubscriber> rs = subscribers.get(k);
		if(rs == null) {
			rs = new HashSet<RemoteSubscriber>();
			subscribers.put(k, rs);
		}
		rs.add(s);
	}
	
	public synchronized void removeSubscriber(Key k, Peer p) {
		Set<RemoteSubscriber> rs = subscribers.get(k);
		RemoteSubscriber remover = null;
		if(rs != null) {
			for(RemoteSubscriber r : rs) {
				if(r.getHost() == p) {
					remover = r;
					break;
				}
			}
			rs.remove(remover);
		}
	}
	
	
	private Map<Key, Set<Subscription>> local = new HashMap<Key, Set<Subscription>>();
	
	public synchronized void clear() {
		subscribers.clear();
		local.clear();
	}
	
	private ClusterManager(String selfIP, String[] peerIP) throws Exception  {
		self = new Peer(selfIP);
		self.start();
		for(String ip : peerIP) {
			Peer p = new Peer(ip);
			peers.put(ip, p);
			p.connect();
		}
	}
	
	public  void receive(Key key, String msg) {
		Set<Subscription> s = null;
		synchronized(this) {
			s = local.get(key);
		}
		if(s != null) {
			for(Subscription sub : s) {
				try {
					sub.print(msg);
				} catch (BrokenCometException e) {
					//TODO Jetty specific problem
					//e.printStackTrace();
				}
			}
		}
	}
	
	public void delete(Subscription sub, Key k, String user) {
		Set<Subscription> s = null;
		synchronized(local) {
			s = local.get(k);
			if(s == null) {
				throw new IllegalStateException();
			}
			if(s.size() == 1) {
				for(Peer p : peers.values()) {
					p.sendMessage(k, user, "leave", self.getIP(), "");
				}
			}
			s.remove(sub);
		}
	}
	/*
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
   
	public  void subscribe(String userID, final Subscription subscription, String className, String id, String propertyName, EventType type) {
		Key key = new Key(className, id, propertyName);
		Set<Subscription> s = null;
		synchronized(this) {
			s = local.get(key);
		}
		if(s == null) {
			s = new HashSet<Subscription>();
			local.put(key, s);
		}
		if(s.size() == 0) {
			for(Peer p : peers.values()) {
				p.sendMessage(key, userID, "join", self.getIP(), "");
			}
		}
		s.add(subscription);
	}

	public synchronized void publish(String user, String data, String className, String id, String propertyName) {
		Key key = new Key(className, id, propertyName);
		Set<RemoteSubscriber> ps = null;
		synchronized(this) {
			ps = subscribers.get(key);
		}
		if(ps != null) {
			Set<Peer> peers = new HashSet<Peer>();
			for(RemoteSubscriber p : ps) {
				peers.add(p.getHost());
			}
			for(Peer p : peers) {
				p.sendMessage(key, user, "msg", self.getIP(), data);
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
		public void publish(String user, String data, String className, String id, String propertyName) {
		
		}
		
	}
}
