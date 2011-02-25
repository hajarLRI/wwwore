package ore.subscriber;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import ore.api.Event;
import ore.api.PropertyChangeListener;
import ore.cluster.ClusterManager;
import ore.cluster.Key;
import ore.event.EventManager;
import ore.exception.BrokenCometException;

/**
 * This class is a wrapper around a {@link PropertyChangeListener} that makes the
 * listener specific to a given {@link Subscriber}. The wrapper allows events for the
 * Subscriber to be buffered and then flushed to the client later. 
 */
public class PropertyChangeSubscription extends Subscription {
	private PropertyChangeListener listener;
	
	public PropertyChangeSubscription(PropertyChangeListener listener, Subscriber subscriber, String className, String key, String property) {
		super(subscriber, className, key, property);
		this.listener = listener;
	}
	
	public void propertyChanged(Event event) throws Exception {
		try {
			String msg = listener.propertyChanged(event);
			dispatch(msg, event);
		} catch(Exception e) {
			throw new BrokenCometException(subscriber, e);
		}
	}
	
	public void remove() {
		EventManager.getInstance().removePropertyChangeSubscription(className, key, property, this);
		ClusterManager.getInstance().delete(new Key(className, key, property), subscriber.getID());
	}
}
