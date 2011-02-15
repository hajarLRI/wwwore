package ore.event;

import java.util.HashMap;
import java.util.Map;

import ore.api.DefaultCollectionChangeListener;
import ore.api.Event;
import ore.api.HibernateUtil;
import ore.chat.entity.ChatMessage;
import ore.cluster.ClusterManager;
import ore.hibernate.Metadata;
import ore.subscriber.CollectionChangeSubscription;
import ore.subscriber.PropertyChangeSubscription;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

/**
 * This class is an adapter between Hibernate entity operations ({@link ore.hibernate.HibernateListener}) and database
 * table operations ({@link TableManger}). It provides the mapping between the two by using the {@link Metadata}
 * class.
 *
 */

public class EventManager {
	private static EventManager instance = new EventManager();
	
	public static EventManager getInstance() {
		return instance;
	}
	
	private TableManager tableManager = new TableManager();
	
	Map<String, PropertyUpdateDispatchListener> propertyDispatchers = new HashMap<String, PropertyUpdateDispatchListener>();
	
	public void addPropertyChangeSubscription(Object entity, String propertyName, PropertyChangeSubscription listener) {
		String className = entity.getClass().getName();
		PersistentClass pc = HibernateUtil.getConfiguration().getClassMapping(className);
		String tableName = pc.getTable().getName();
		String columnName = null;
		Property prop = pc.getProperty(propertyName);
		columnName = Metadata.getSingleColumnKey(prop);
		PropertyUpdateDispatchListener dispatcher = propertyDispatchers.get(tableName);
		if(dispatcher == null) {
			dispatcher = new PropertyUpdateDispatchListener();
			tableManager.addUpdateListener(tableName, dispatcher);
		}
		dispatcher.addUpdateListener(columnName, listener);
	}
	
	public void addCollectionChangeSubscription(Object entity, String propertyName, CollectionChangeSubscription listener) {
		String className = entity.getClass().getName();
		String roleName = className + '.' + propertyName;
		if(Metadata.isOneToMany(roleName)) {
			Collection collection = Metadata.getCollectionMetadata(roleName);
			KeyValue key = collection.getKey();
			String tableName = key.getTable().getName();
			String columnName = Metadata.getSingleColumnKey(key);
			Object idValue = Metadata.getPrimaryKeyValue(entity);
			ValueMatchingDeleteListener deletes = new ValueMatchingDeleteListener(listener, columnName, idValue);
			tableManager.addDeleteListener(tableName, deletes);
			((DefaultCollectionChangeListener) listener.getListener()).addOwner(deletes);
			ValueMatchingInsertListener inserts = new ValueMatchingInsertListener(listener, columnName, idValue);
			tableManager.addInsertListener(tableName, inserts);
			((DefaultCollectionChangeListener) listener.getListener()).addOwner(inserts);
			ValueMatchingUpdateListener updates = new ValueMatchingUpdateListener(listener, columnName, idValue);
			tableManager.addUpdateListener(tableName, updates);
			((DefaultCollectionChangeListener) listener.getListener()).addOwner(updates);
		} else {
			String manyToManyTableName = Metadata.getManyToManyTableName(roleName);
			ManyToManyTableListener ccrl = new ManyToManyTableListener(listener);
			tableManager.addDeleteListener(manyToManyTableName, ccrl);
			tableManager.addInsertListener(manyToManyTableName, ccrl);
			((DefaultCollectionChangeListener) listener.getListener()).addOwner(ccrl);
		}
	}
	
	public void entityPropertyChanged(String propertyName, Object entity, Object oldValue, Object newValue) {
		Event event = new Event(entity, propertyName, oldValue, newValue, Event.EventType.PropertyChanged);
		String tableName = Metadata.getEntityTableName(entity.getClass());
		tableManager.update(tableName, event);
	}
	
	public void collectionElementAdded(Object entity, String propertyName, Object element) {
		Event event = new Event(entity, propertyName, null, element, Event.EventType.CollectionChanged);
		String className = entity.getClass().getName();
		String roleName = className + '.' + propertyName;
		if(Metadata.isOneToMany(roleName)) {
			Collection collection = Metadata.getCollectionMetadata(roleName);
			KeyValue key = collection.getKey();
			String tableName = key.getTable().getName();
			tableManager.insert(tableName, event);
		} else {
			String manyToManyTableName = Metadata.getManyToManyTableName(roleName);
			tableManager.insert(manyToManyTableName, event);
		}
		//TODO Chat room specific code
		ChatMessage message = (ChatMessage) event.getNewValue();
		String str = message.toJSON();
		ClusterManager.getInstance().publish(str.toCharArray(), event);
	}
	
	public void collectionElementRemoved(Object entity, String propertyName, Object element) {
		Event event = new Event(entity, propertyName, null, element, Event.EventType.CollectionChanged);
		String className = entity.getClass().getName();
		String roleName = className + '.' + propertyName;
		if(Metadata.isOneToMany(roleName)) {
			Collection collection = Metadata.getCollectionMetadata(roleName);
			KeyValue key = collection.getKey();
			String tableName = key.getTable().getName();
			tableManager.delete(tableName, event);
		} else {
			String manyToManyTableName = Metadata.getManyToManyTableName(roleName);
			tableManager.delete(manyToManyTableName, event);
		}
	}
	
}
