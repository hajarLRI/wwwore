package ore.subscriber;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.jms.JMSException;

import ore.api.CollectionChangeListener;
import ore.api.Event;
import ore.api.PropertyChangeListener;
import ore.chat.event.MessageListener;
import ore.cluster.ClusterManager;
import ore.cluster.Key;
import ore.cluster.Peer;
import ore.event.EventManager;
import ore.exception.BrokenCometException;
import ore.util.LogMan;

import org.eclipse.jetty.continuation.Continuation;
import org.json.JSONArray;

/**
 * Maintains the Comet connection to a particular client and provides buffering
 * of events for the client. 
 */
public class Subscriber {
	private Continuation c;
	private String id;
	private RepartitionSubscription rs = new RepartitionSubscription(this);
	private ConcurrentLinkedQueue<String> buffer = new ConcurrentLinkedQueue<String>();
	private static Map<String, String> map = new HashMap<String, String>();
	static {
		map.put("61616", "8080");
		map.put("61617", "8090");
	}
 	
	
	private List<Subscription> subs = new LinkedList<Subscription>();
	
	
	public Subscriber(String sessionID, JSONArray digest) throws Exception {
		this(sessionID);
		for(int i=0;i < digest.length();i++) {
			String key = digest.getString(i);
			Key k = Key.parse(key);
			addCollectionChangeListener(this.id	, k.getClassName(), k.getId(), k.getProperty(), new MessageListener());
		}
	}
	
	public void stop() throws BrokenCometException {
		for(Subscription s : subs) {
			s.remove();
		}
		synchronized(this) {
			SubscriberManager.getInstance().remove(this);
			if(isSuspended()) {
				String message = "{\"type\":'stop'}";
				this.print(message);
			}
		}
	}
	
	public void redirect(Peer p, boolean swap) throws BrokenCometException {
		String[] parts = p.getIP().split(":");
		if(parts[0].equals("localhost")) {
			parts[1] = map.get(parts[1]);
		} else {
			parts[1] = "8080";
		}
		redirect(parts[0], parts[1], swap);
	}
	
	public void redirect(String ip, String port, boolean swap) throws BrokenCometException {
		SubscriberDigest digest = new SubscriberDigest();
		for(Subscription s : subs) {
			s.remove();
			digest.addKey(s.getKey());
		}
		String msg = "{\"type\":'redirect',\"ip\":'" + ip + "',\"port\":'" + port + "',\"swap\":'" + swap + "',\"from\":'" + ClusterManager.getInstance().getSelf().getIP() + "',\"digest\":" + digest.toJSON() + "}";
		print(msg);
	}
	
	/**
	 * Get the Jetty Continuation associated with this client's Comet connection.
	 * (package-protected)
	 */
	Continuation getContinuation() {
		return c;
	}
	
	/** Make a new Subscriber with the given sessionID
	 * @param 	id 	The sessionID of the new Subscriber
	 */
	Subscriber(String id) {
		this.id = id;
		LogMan.info("New subscriber: " + id);
	}
	
	public String getID() {
		return id;
	}
	
	/* Check if the user has an established long-poll ready for messages
	 * (package-protected)
	 */
	boolean isConnected() {
		return (c != null);
	}
	
	public boolean isSuspended() {
		return (c != null) && (c.isSuspended());
	}
	
	private void connect(Continuation c) {
		this.c = c;
	}
	
	private void buffer(String data) {
		buffer.add(data);
	}
	
	private void flushData(PrintWriter pw) {
		pw.print("[");
		int i = 0;
		for(String data : buffer) {
			pw.print(data);
			if(i != (buffer.size()-1)) {
				pw.print(',');
			}
			i++;
		}
		pw.print("]");
		buffer.clear();
	}
	
	public synchronized void print(String data) throws BrokenCometException {
		try {
			buffer(data);
			if(isSuspended()) {
				LogMan.info("Subscriber " + id + " got pushed");
				PrintWriter pw = getContinuation().getServletResponse().getWriter();
				flushData(pw);
				getContinuation().complete();
			} 
		} catch(Exception e) {
			throw new BrokenCometException(this, e);
		}

	}
	
	private synchronized void pickup(String redirect) throws Exception {	
		if(!redirect.equals("no")) {
			Peer p = ClusterManager.getInstance().greedyRedirect(new SubscriberDigest(subs));
			if(p != null) {
				redirect(p, true);
			}
		}
		if(buffer.size() > 0) {
			LogMan.info("Subscriber " + id + " pickup data");
			PrintWriter pw = c.getServletResponse().getWriter();
			flushData(pw);
			c = null;
		} else {
			LogMan.info("Subscriber " + id + " empty pickup data");
			c.suspend(c.getServletResponse());
		}
	}
	
	public void pickup(Continuation c, String redirect) throws Exception {
		this.connect(c);
		this.pickup(redirect);
	}

	public void addPropertyChangeListener(String userID, String className, String key, String property, PropertyChangeListener listener) throws JMSException {
		PropertyChangeSubscription sx = new PropertyChangeSubscription(listener, this, className, key, property);
		subs.add(sx);
		EventManager.getInstance().addPropertyChangeSubscription(sx);
		ClusterManager.getInstance().subscribe(userID, sx, Event.EventType.PropertyChanged);
	}
	
	public void addCollectionChangeListener(String userID, String className, String key, String property, CollectionChangeListener listener) throws JMSException {
		CollectionChangeSubscription sx = new CollectionChangeSubscription(listener, this, className, key, property);
		subs.add(sx);
		EventManager.getInstance().addCollectionChangeSubscription(sx);
		ClusterManager.getInstance().subscribe(userID, sx, Event.EventType.CollectionChanged);
	}

}
