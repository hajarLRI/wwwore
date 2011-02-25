package ore.subscriber;

import java.util.concurrent.ConcurrentHashMap;

import ore.exception.NoSuchSubscriber;

import org.json.JSONArray;

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
	 */
	public String newSubscriber() {
		//TODO This is an error-prone strategy for creating sessionIDs
		int sessionIDNum = id++;
		String sessionID = sessionIDNum + "";
		Subscriber subscriber = new Subscriber(sessionID);
		subscribers.put(sessionID, subscriber);
		return sessionID;
	}
	
	public String newSubscriber(JSONArray digest) throws Exception {
		//TODO This is an error-prone strategy for creating sessionIDs
		int sessionIDNum = id++;
		String sessionID = sessionIDNum + "";
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
