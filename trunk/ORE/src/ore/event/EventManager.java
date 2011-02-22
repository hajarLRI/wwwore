package ore.event;

import java.io.Serializable;
import java.util.Set;

import ore.api.Event;
import ore.cluster.ClusterManager;
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
	
	public void clear() {
		propertySubscriptions.clear();
		collectionSubscriptions.clear();
	}
	
	public void addPropertyChangeSubscription(PropertyChangeSubscription subscription) {
		propertySubscriptions.addSubscription(subscription.getClassName(), subscription.getKey(), subscription.getProperty(), subscription);
	}
	
	public void addCollectionChangeSubscription(CollectionChangeSubscription subscription) {
		collectionSubscriptions.addSubscription(subscription.getClassName(), subscription.getKey(), subscription.getProperty(), subscription);
	}
	
	public void removePropertyChangeSubscription(String className, String key, String propertyName, PropertyChangeSubscription subscription) {
		propertySubscriptions.removeSubscription(className, key, propertyName, subscription);
	}
	
	public void removeCollectionChangeSubscription(String className, String key, String propertyName, CollectionChangeSubscription subscription) {
		collectionSubscriptions.removeSubscription(className, key, propertyName, subscription);
	}
	
	public void entityPropertyChanged(String propertyName, Object entity, Object oldValue, Object newValue) throws Exception {
		Event event = new Event(entity, propertyName, oldValue, newValue, Event.EventType.PropertyChanged);
		Serializable key = Metadata.getPrimaryKeyValue(entity);
		String className = entity.getClass().getName();
		Set<PropertyChangeSubscription> subs = propertySubscriptions.lookupSubscription(className, key.toString(), propertyName);
		for(PropertyChangeSubscription sub : subs) {
			sub.propertyChanged(event);
		}
	}
	
	public void collectionElementAdded(String user, Object entity, String propertyName, Object element) throws BrokenCometException {
		Event event = new Event(entity, propertyName, null, element, Event.EventType.CollectionChanged);
		Serializable key = Metadata.getPrimaryKeyValue(entity);
		String className = entity.getClass().getName();
		Set<CollectionChangeSubscription> subs = collectionSubscriptions.lookupSubscription(className, key.toString(), propertyName);
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
			ClusterManager.getInstance().publish(user, str, className, key.toString(), propertyName);
		}
	}
	
	public void collectionElementRemoved(Object entity, String propertyName, Object element) {
		
	}
	
}
