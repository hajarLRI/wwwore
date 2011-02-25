package ore.subscriber;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.jms.JMSException;

import ore.api.CollectionChangeListener;
import ore.api.Event;
import ore.api.PropertyChangeListener;
import ore.cluster.ClusterManager;
import ore.event.EventManager;
import ore.exception.BrokenCometException;
import ore.hibernate.Metadata;
import ore.util.LogMan;

import org.eclipse.jetty.continuation.Continuation;

/**
 * Maintains the Comet connection to a particular client and provides buffering
 * of events for the client. 
 */
public class Subscriber {
	private Continuation c;
	private String id;
	private RepartitionSubscription rs = new RepartitionSubscription(this);
	private ConcurrentLinkedQueue<String> buffer = new ConcurrentLinkedQueue<String>();
	private boolean isRepartitioning = false;
	
	public boolean isRepartitioning() {
		return isRepartitioning;
	}
	
	public synchronized void redirect(String ipAddress, String port) throws BrokenCometException {
		isRepartitioning = true;
		rs.repartition(ipAddress, port);
	}
	
	private List<Subscription> subs = new LinkedList<Subscription>();
	
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
	
	boolean isSuspended() {
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
				//pw.close();
				if(isRepartitioning()) {
					SubscriberManager.getInstance().remove(this);
				}
				getContinuation().complete();
			} 
		} catch(Exception e) {
			throw new BrokenCometException(this, e);
		}

	}
	
	private synchronized void pickup() throws Exception {	
			if(buffer.size() > 0) {
				LogMan.info("Subscriber " + id + " pickup data");
				PrintWriter pw = c.getServletResponse().getWriter();
				flushData(pw);
				if(isRepartitioning) {
					SubscriberManager.getInstance().remove(this);
				}
				//pw.close();
				c = null;
			} else {
				LogMan.info("Subscriber " + id + " empty pickup data");
				c.suspend(c.getServletResponse());
			}
	}
	
	public void pickup(Continuation c) throws Exception {
		this.connect(c);
		this.pickup();
	}

	public void addPropertyChangeListener(String userID, String className, String key, String property, PropertyChangeListener listener) throws JMSException {
		PropertyChangeSubscription sx = new PropertyChangeSubscription(listener, this, className, key, property);
		subs.add(sx);
		EventManager.getInstance().addPropertyChangeSubscription(sx);
		ClusterManager.getInstance().subscribe(userID, sx, className, key, property, Event.EventType.PropertyChanged);
	}
	
	public void addCollectionChangeListener(String userID, String className, String key, String property, CollectionChangeListener listener) throws JMSException {
		CollectionChangeSubscription sx = new CollectionChangeSubscription(listener, this, className, key, property);
		subs.add(sx);
		EventManager.getInstance().addCollectionChangeSubscription(sx);
		ClusterManager.getInstance().subscribe(userID, sx, className, key, property, Event.EventType.CollectionChanged);
	}

}
