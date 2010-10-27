package ore.event;

import java.util.Collection;

import ore.api.Event;
import ore.exception.BrokenCometException;
import ore.subscriber.CollectionChangeSubscription;

/**
 * Used by the {@link EventManager} to detect changes to collections that
 * are implemented by a many-to-many relationship. 
 * <br/> (package-protected)
 */
class ManyToManyTableListener implements InsertListener, DeleteListener {

	CollectionChangeSubscription listener;
	
	private Collection owner;
	
	public void setOwner(Collection c) {
		owner = c;
	}
	
	public void delete() {
		owner.remove(this);
	}
	
	public ManyToManyTableListener(CollectionChangeSubscription listener) {
		this.listener = listener;
	}

	@Override
	public void insert(Event event) throws BrokenCometException {
		listener.elementAdded(event);
	}

	@Override
	public void delete(Event event) throws BrokenCometException {
		listener.elementRemoved(event);
	}
	
	
}
