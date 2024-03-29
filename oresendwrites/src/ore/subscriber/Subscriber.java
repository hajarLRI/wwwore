package ore.subscriber;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.jms.JMSException;

import ore.api.CollectionChangeListener;
import ore.api.Event;
import ore.api.PropertyChangeListener;
import ore.cluster.ClusterManager;
import ore.event.EventManager;
import ore.hibernate.Metadata;
import ore.util.LogMan;

import org.eclipse.jetty.continuation.Continuation;

/**
 * Maintains the Comet connection to a particular client and provides buffering
 * of events for the client. 
 */
public class Subscriber {
	private Continuation c;
	private ConcurrentLinkedQueue<Flushable> q = new ConcurrentLinkedQueue<Flushable>();
	private String id;
	
	private List<CollectionChangeListener> list = new LinkedList<CollectionChangeListener>();
	private List<Subscription> subs = new LinkedList<Subscription>();
	
	public void clear() {
		for(CollectionChangeListener ccl : list) {
			ccl.delete();
		}
		for(Subscription sub : subs) {
			ClusterManager.getInstance().delete(sub);
		}
	}
	
	/**
	 * Get the Jetty Continuation associated with this client's Comet connection.
	 * (package-protected)
	 */
	Continuation getContinuation() {
		return c;
	}
	
	/** Get the buffer of events waiting to be delivered to the client
	 * <br/> (package-protected
	 */
	ConcurrentLinkedQueue<Flushable> getQueue() {
		return q;
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
	
	public void queue(Flushable e) {
		q.add(e);
	}
	
	private void pickup() throws Exception {
		synchronized(this) {
			
			boolean gotData = false;

			for(Flushable listener : q) {
				gotData |= listener.flushEvents(c.getServletResponse().getWriter());
			}

			if(gotData) {
				LogMan.info("Subscriber " + id + " pickup data");
				//c.complete();
				c = null;
			} else {
				//LogMan.info("Subscriber " + id + " empty pickup data");
				c.suspend();
			}
		}
	}
	
	public void pickup(Continuation c) throws Exception {
		this.connect(c);
		this.pickup();
	}

	public void addPropertyChangeListener(Object entity, String property, PropertyChangeListener listener) throws JMSException {
		PropertyChangeSubscription sx = new PropertyChangeSubscription(listener, this);
		EventManager.getInstance().addPropertyChangeSubscription(entity, property, sx);
		ClusterManager.getInstance().subscribe(sx, entity.getClass().getName(), Metadata.getPrimaryKeyValue(entity), property, Event.EventType.PropertyChanged);
	}
	
	public void addCollectionChangeListener(Object entity, String property, CollectionChangeListener listener) throws JMSException {
		list.add(listener);
		CollectionChangeSubscription sx = new CollectionChangeSubscription(entity, listener, this);
		subs.add(sx);
		EventManager.getInstance().addCollectionChangeSubscription(entity, property, sx);
		ClusterManager.getInstance().subscribe(sx, entity.getClass().getName(), Metadata.getPrimaryKeyValue(entity), property, Event.EventType.CollectionChanged);
	}
	
}
