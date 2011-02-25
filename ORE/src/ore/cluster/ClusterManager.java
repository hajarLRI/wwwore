package ore.cluster;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ore.api.Event.EventType;
import ore.exception.BrokenCometException;
import ore.subscriber.SubscriberDigest;
import ore.subscriber.Subscription;

public class ClusterManager {
	
	private static ClusterManager instance;
	private Peer self;
	private Map<String, Peer> peers = new HashMap<String, Peer>();
	private Map<Key, Set<RemoteSubscriber>> subscribers = new HashMap<Key, Set<RemoteSubscriber>>();
	private Map<Key, Set<Subscription>> local = new HashMap<Key, Set<Subscription>>();
	private String mode;
	
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
	
	public synchronized void removeSubscriber(Key k, RemoteSubscriber remote) {
		Set<RemoteSubscriber> rs = subscribers.get(k);
		RemoteSubscriber remover = null;
		if(rs != null) {
			if(mode.equals("weighted")) {
				rs.remove(remote);
			} else {
				for(RemoteSubscriber r : rs) {
					if(r.getHost() == remote.getHost()) {
						remover = r;
						break;
					}
				}
				rs.remove(remover);
			}
		}
	}
	
	public synchronized void clear() {
		subscribers.clear();
		local.clear();
	}
	
	private ClusterManager(String selfIP, String[] peerIP, String mode) throws Exception  {
		this.mode = mode;
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
			if((s.size() == 1) || mode.equals("weighted")) {
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
   
	public  void subscribe(String userID, Subscription subscription, EventType type) {
		Key key = subscription.getKey();
		Set<Subscription> s = null;
		synchronized(this) {
			s = local.get(key);
		}
		if(s == null) {
			s = new HashSet<Subscription>();
			local.put(key, s);
		}
		if((s.size() == 0) || mode.equals("weighted")) {
			for(Peer p : peers.values()) {
				p.sendMessage(key, userID, "join", self.getIP(), "");
			}
		}
		s.add(subscription);
	}

	public synchronized void publish(String user, String data, Key key) {
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
	
	public static void init(String ip, String[] peerIP, String mode) throws Exception {
		if(ip == null) {
			instance = new NullClusterManager();
		} else {
			instance = new ClusterManager(ip, peerIP, mode);
		}
	}
	
	public static ClusterManager getInstance() {
		if(instance == null) {
			throw new IllegalStateException();
		}
		return instance;
	}
	
	public Peer greedyRedirect(SubscriberDigest digest) {
		return greedyUnweighted(digest);
	}
	
	private Peer greedyUnweighted(SubscriberDigest digest) {
		Set<Key> keys = digest.getKeys();
		Map<Peer, Integer> buckets = new HashMap<Peer, Integer>();
		for(Key k : keys) {
			Set<RemoteSubscriber> rs = subscribers.get(k);
			if(rs != null) {
				for(RemoteSubscriber r : rs) {
					Integer i = buckets.get(r.getHost());
					if(i == null) {
						buckets.put(r.getHost(), 1);
					} else {
						buckets.put(r.getHost(), i+1);
					}
				}
			}
			Set<Subscription> l = local.get(k);
			if((l != null) && (l.size() > 1)) {
				Integer i = buckets.get(self);
				if(i == null) {
					buckets.put(self, 1);
				} else {
					buckets.put(self, i+1);
				}
			}
		}
		Integer maxInteger = buckets.get(self);
		int max = 0;
		if(maxInteger != null) {
			max = maxInteger;
		}
		Peer maxPeer = self;
		for(Map.Entry<Peer, Integer> entry : buckets.entrySet()) {
			int size = entry.getValue();
			if(size > max) {
				max = size;
				maxPeer = entry.getKey();
			}
		}
		if(maxPeer == self) {
			return null;
		}
		return maxPeer;
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
		public void subscribe(String userID, Subscription subscription, EventType type) {
			
		}

		@Override
		public void publish(String user, String data, Key k) {
		
		}
		
	}
}
