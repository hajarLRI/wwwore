package ore.subscriber;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import ore.api.CollectionChangeListener;
import ore.api.Event;
import ore.event.EventManager;
import ore.exception.BrokenCometException;

/**
 * This class is a wrapper around a CollectionChangeListener that makes the
 * listener specific to a given Subscriber. The wrapper allows events for the
 * Subscriber to be buffered and then flushed to the client later. 
 */
public class CollectionChangeSubscription extends Subscription implements Flushable {
	
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
			CharArrayWriter buffer = new CharArrayWriter();
			PrintWriter pw = new PrintWriter(buffer);
			listener.elementAdded(pw, event);
			char[] data = buffer.toCharArray();
			dispatch(data, event);
		} catch(Exception e) {
			throw new BrokenCometException(subscriber, e);
		}		
	}
	
	public void elementRemoved(Event event) throws BrokenCometException {
		try {
			CharArrayWriter buffer = new CharArrayWriter();
			PrintWriter pw = new PrintWriter(buffer);
			listener.elementRemoved(pw, event);
			char[] data = buffer.toCharArray();
			dispatch(data, event);
		} catch(Exception e) {
			throw new BrokenCometException(subscriber, e);
		}
	}

	@Override
	public void remove() {
		EventManager.getInstance().removeCollectionChangeSubscription(className, this.key, this.property, this);
	}
	
}
