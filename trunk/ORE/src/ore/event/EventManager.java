package ore.event;

import java.io.Serializable;
import java.util.Set;

import ore.api.Event;
import ore.cluster.ClusterManager;
import ore.cluster.Key;
import ore.exception.BrokenCometException;
import ore.hibernate.Metadata;
import ore.subscriber.CollectionChangeSubscription;
import ore.subscriber.PropertyChangeSubscription;
import ore.subscriber.Subscription;
import ore.util.JSONable;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.KeyValue;


public class EventManager {
	private static EventManager instance = new EventManager();
	
	public static EventManager getInstance() {
		return instance;
	}
	
	private ObjectPropertyTable<PropertyChangeSubscription> propertySubscriptions = new ObjectPropertyTable();
	private ObjectPropertyTable<CollectionChangeSubscription> collectionSubscriptions = new ObjectPropertyTable();
	
	public synchronized void clear() {
		propertySubscriptions.clear();
		collectionSubscriptions.clear();
	}
	
	public synchronized void addPropertyChangeSubscription(PropertyChangeSubscription subscription) {
		propertySubscriptions.addSubscription(subscription);
	}
	
	public synchronized void addCollectionChangeSubscription(CollectionChangeSubscription subscription) {
		collectionSubscriptions.addSubscription(subscription);
	}
	
	public synchronized void removePropertyChangeSubscription(PropertyChangeSubscription subscription) {
		propertySubscriptions.removeSubscription(subscription);
	}
	
	public synchronized void removeCollectionChangeSubscription(CollectionChangeSubscription subscription) {
		collectionSubscriptions.removeSubscription(subscription);
	}
	
	public void entityPropertyChanged(String propertyName, Object entity, Object oldValue, Object newValue) throws Exception {
		Event event = new Event(entity, propertyName, oldValue, newValue, Event.EventType.PropertyChanged);
		Serializable key = Metadata.getPrimaryKeyValue(entity);
		String className = entity.getClass().getName();
		Set<PropertyChangeSubscription> subs = null;
		Key k = Key.create(className, key.toString(), propertyName);
		synchronized(this) {
			subs = propertySubscriptions.lookupSubscription(k);
		}
		for(PropertyChangeSubscription sub : subs) {
			sub.propertyChanged(event);
		}
	}
	
	public void collectionElementAdded(String user, Object entity, String propertyName, Object element) throws BrokenCometException {
		Event event = new Event(entity, propertyName, null, element, Event.EventType.CollectionChanged);
		Serializable key = Metadata.getPrimaryKeyValue(entity);
		String className = entity.getClass().getName();
		Set<CollectionChangeSubscription> subs = null;
		Key k = Key.create(className, key.toString(), propertyName);
		synchronized(this) {
			subs = collectionSubscriptions.lookupSubscription(k);
		}
		for(CollectionChangeSubscription sub : subs) {
			try {
				sub.elementAdded(event);
			} catch(BrokenCometException e) {
				
			}
		}
		
		Object obj = event.getNewValue();
		if(obj instanceof JSONable) {
			JSONable message = (JSONable) obj;
			String str = message.toJSON();
			ClusterManager.getInstance().publish(user, str, k);
		}
	}
	
	public void collectionElementRemoved(Object entity, String propertyName, Object element) {
		
	}
	
}
