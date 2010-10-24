package ore.event;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ore.api.Event;
import ore.exception.BrokenCometException;
import ore.subscriber.PropertyChangeSubscription;

/**
 * Used by the {@link EventManager} to detect mutations to specific columns of the
 * database. It acts as a filter on database update events, allowing only those events
 * which affect the specified column to pass onto some other wrapped {@link PropertyChangeSubscription}s
 * <br/> (package-protected)
 */
class PropertyUpdateDispatchListener implements UpdateListener {

	private Map<String, List<PropertyChangeSubscription>> listeners = new HashMap<String, List<PropertyChangeSubscription>>();
	
	void addUpdateListener(String columnName, PropertyChangeSubscription listener) {
		List<PropertyChangeSubscription> list = listeners.get(columnName);
		if(list == null) {
			list = new LinkedList<PropertyChangeSubscription>();
			listeners.put(columnName, list);
		}
		list.add(listener);
	}
	
	@Override
	public void update(Event event) {
		String propertyName = event.getPropertyName();
		List<PropertyChangeSubscription> list = listeners.get(propertyName);
		if(list != null) {
			List<PropertyChangeSubscription> broken = new LinkedList<PropertyChangeSubscription>();
			for(PropertyChangeSubscription subscription : list) {
				try {
					subscription.propertyChanged(event);
				} catch(BrokenCometException bce) {
					broken.add(subscription);
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			}
			for(PropertyChangeSubscription subscription : broken) {
				list.remove(subscription);
			}
		}
	}

}
