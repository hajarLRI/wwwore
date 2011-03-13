package ore.subscriber;

import java.util.concurrent.ConcurrentHashMap;

import ore.cluster.ClusterManager;
import ore.cluster.Peer;
import ore.exception.NoSuchSubscriber;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Maintains a map between sessionID and {@link Subscriber}
 */
public class SubscriberManager {
	
	/**
	 * Helps generate sessionIDs in increasing order
	 */
	//TODO Error-prone way of generating sessionIDs
	private int id = 0;
	
	/**
	 * Singleton pattern instance
	 */
	private static SubscriberManager instance = new SubscriberManager();
	
	/**
	 * Singleton pattern accessor 
	 */
	public static SubscriberManager getInstance() {
		return instance;
	}
	
	/**
	 * Singleton pattern
	 */
	private SubscriberManager() {}
	
	private ConcurrentHashMap<String, Subscriber> subscribers = new ConcurrentHashMap<String, Subscriber>();
	
	/**
	 * Get the {@link Subscriber} object for a given sessionID
	 * 
	 * @param	id	The sessionID of the subscriber to retrieve
	 */
	public Subscriber get(String id) throws NoSuchSubscriber{
		Subscriber me = subscribers.get(id);
		if(me == null) {
			throw new NoSuchSubscriber(id);
		}
		return me;
	}
	
	/**
	 * Factory method to create a new {@link Subscriber} with a fresh sessionID
	 * @throws Exception 
	 */
	public String newSubscriber() throws Exception {
		//TODO This is an error-prone strategy for creating sessionIDs
		int sessionIDNum = id++;
		String sessionID = sessionIDNum + "";
		Subscriber subscriber = Subscriber.create(sessionID, null);
		subscribers.put(sessionID, subscriber);
		return sessionID;
	}
	
	public String newSubscriber(JSONObject migrateInfo, boolean swap) throws Exception {
		//TODO This is an error-prone strategy for creating sessionIDs
		if(swap) {
			String from = migrateInfo.getString("from");
			Peer p = ClusterManager.getInstance().getPeer(from);
			Subscriber s = ClusterManager.getInstance().getMaxFor(from);
			if(s != null) {
				s.redirect(p, false);
			}
		}
		int sessionIDNum = id++;
		String sessionID = sessionIDNum + "";
		JSONArray digest = migrateInfo.getJSONArray("digest");
		Subscriber subscriber = new Subscriber(sessionID, digest);
		subscribers.put(sessionID, subscriber);
		return sessionID;
	}
	
	/**
	 * Reclaim memory for a disconnected {@link Subscriber}
	 * @param s	The subscriber to remove
	 */
	public void remove(Subscriber s) {
		String id = s.getID();
		subscribers.remove(id);
	}
	
 }
