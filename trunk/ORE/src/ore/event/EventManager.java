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
	
	private ObjectPropertyTable propertySubscriptions;
	private ObjectPropertyTable collectionSubscriptions;
	
	public void addPropertyChangeSubscription(Object entity, String propertyName, PropertyChangeSubscription subscription) {
		String className = entity.getClass().getName();
		Serializable key = Metadata.getPrimaryKeyValue(entity);
		propertySubscriptions.addSubscription(className, key.toString(), propertyName, subscription);
	}
	
	public void addCollectionChangeSubscription(Object entity, String propertyName, CollectionChangeSubscription subscription) {
		String className = entity.getClass().getName();
		Serializable key = Metadata.getPrimaryKeyValue(entity);
		collectionSubscriptions.addSubscription(className, key.toString(), propertyName, subscription);
	}
	
	public void entityPropertyChanged(String propertyName, Object entity, Object oldValue, Object newValue) throws Exception {
		Event event = new Event(entity, propertyName, oldValue, newValue, Event.EventType.PropertyChanged);
		Serializable key = Metadata.getPrimaryKeyValue(entity);
		String className = entity.getClass().getName();
		Set<Subscription> subs = propertySubscriptions.lookupSubscription(className, key.toString(), propertyName);
		for(Subscription sub : subs) {
			PropertyChangeSubscription pcs = (PropertyChangeSubscription) sub;
			pcs.propertyChanged(event);
		}
	}
	
	public void collectionElementAdded(Object entity, String propertyName, Object element) throws BrokenCometException {
		Event event = new Event(entity, propertyName, null, element, Event.EventType.CollectionChanged);
		Serializable key = Metadata.getPrimaryKeyValue(entity);
		String className = entity.getClass().getName();
		Set<Subscription> subs = propertySubscriptions.lookupSubscription(className, key.toString(), propertyName);
		for(Subscription sub : subs) {
			CollectionChangeSubscription pcs = (CollectionChangeSubscription) sub;
			pcs.elementAdded(event);
		}
		
		Object obj = event.getNewValue();
		if(obj instanceof JSONable) {
			JSONable message = (JSONable) obj;
			String str = message.toJSON();
			ClusterManager.getInstance().publish(str.toCharArray(), event);
		}
	}
	
	public void collectionElementRemoved(Object entity, String propertyName, Object element) {
		
	}
	
}
