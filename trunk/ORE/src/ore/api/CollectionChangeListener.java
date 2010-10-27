package ore.api;

import java.io.PrintWriter;

/**
 * Users implement this interface to be notified when a one-to-many or
 * many-to-many collection has elements that are added or removed
 */
public interface CollectionChangeListener {
	public void elementAdded(PrintWriter pw, Event event) throws Exception;
	public void elementRemoved(PrintWriter pw, Event event) throws Exception;
	public void delete();
}
