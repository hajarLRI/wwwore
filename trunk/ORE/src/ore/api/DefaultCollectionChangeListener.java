package ore.api;

import java.io.PrintWriter;

/**
 * Convenience implementation of {@link ore.api.CollectionChangeListener} for users who only
 * want to implement one of the methods
 */

public class DefaultCollectionChangeListener implements CollectionChangeListener {

	@Override
	public void elementAdded(PrintWriter pw, Event event) throws Exception {
		//Empty convenience implementation
	}

	@Override
	public void elementRemoved(PrintWriter pw, Event event) throws Exception {
		//Empty convenience implementation
	}
}
