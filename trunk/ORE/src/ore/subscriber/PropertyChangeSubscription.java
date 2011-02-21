package ore.subscriber;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import ore.api.Event;
import ore.api.PropertyChangeListener;
import ore.exception.BrokenCometException;

/**
 * This class is a wrapper around a {@link PropertyChangeListener} that makes the
 * listener specific to a given {@link Subscriber}. The wrapper allows events for the
 * Subscriber to be buffered and then flushed to the client later. 
 */
public class PropertyChangeSubscription extends Subscription implements Flushable {
	private PropertyChangeListener listener;
	
	public PropertyChangeSubscription(PropertyChangeListener listener, Subscriber subscriber) {
		super(subscriber);
		this.listener = listener;
	}
	
	public void propertyChanged(Event event) throws Exception {
		try {
			CharArrayWriter buffer = new CharArrayWriter();
			PrintWriter pw = new PrintWriter(buffer);
			listener.propertyChanged(pw, event);
			char[] data = buffer.toCharArray();
			dispatch(data, event);
		} catch(Exception e) {
			throw new BrokenCometException(subscriber, e);
		}
	}
	
}
