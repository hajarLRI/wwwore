package ore.subscriber;

import ore.api.CollectionChangeListener;
import ore.api.Event;
import ore.cluster.ClusterManager;
import ore.cluster.Key;
import ore.event.EventManager;
import ore.exception.BrokenCometException;

/**
 * This class is a wrapper around a CollectionChangeListener that makes the
 * listener specific to a given Subscriber. The wrapper allows events for the
 * Subscriber to be buffered and then flushed to the client later. 
 */
public class CollectionChangeSubscription extends Subscription {
	
	private CollectionChangeListener listener;
	
	public CollectionChangeListener getListener() {
		return listener;
	}
	
	
	public CollectionChangeSubscription(CollectionChangeListener listener, Subscriber subscriber, String className, String key, String property) {
		super(subscriber, className, key, property);
		this.listener = listener;
	}

	public void elementAdded(Event event) throws BrokenCometException {
		try {
			String msg = listener.elementAdded(event);
			dispatch(msg, event);
		} catch(Exception e) {
			throw new BrokenCometException(subscriber, e);
		}		
	}
	
	public void elementRemoved(Event event) throws BrokenCometException {
		try {
			String msg = listener.elementRemoved(event);
			dispatch(msg, event);
		} catch(Exception e) {
			throw new BrokenCometException(subscriber, e);
		}
	}

	public void remove() {
		EventManager.getInstance().removeCollectionChangeSubscription(className, key, property, this);
		Key k = new Key(className, key, property);
		//System.out.println("KEY: " + key.toString());
		ClusterManager.getInstance().delete(this, k, subscriber.getID());
	}
}
