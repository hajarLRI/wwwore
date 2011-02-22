package ore.api;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import ore.event.*;

/**
 * Convenience implementation of {@link ore.api.CollectionChangeListener} for users who only
 * want to implement one of the methods
 */

public class DefaultCollectionChangeListener implements CollectionChangeListener {

	@Override
	public String elementAdded( Event event) throws Exception {
		return "";
	}

	@Override
	public String elementRemoved(Event event) throws Exception {
		return "";
	}
	
}
