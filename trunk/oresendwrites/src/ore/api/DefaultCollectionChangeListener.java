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

	private List<Deleteable> owners = new LinkedList<Deleteable>();;
	
	@Override
	public void elementAdded(PrintWriter pw, Event event) throws Exception {
		//Empty convenience implementation
	}

	@Override
	public void elementRemoved(PrintWriter pw, Event event) throws Exception {
		//Empty convenience implementation
	}
	
	@Override
	public void delete() {
		for(Deleteable x : owners) {
			x.delete();
		}
	}
	
	public void addOwner(Deleteable c) {
		owners.add(c);
	}
}
