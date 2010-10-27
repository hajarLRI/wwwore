package ore.event;

import java.lang.reflect.Method;
import java.util.Collection;

import ore.api.CollectionChangeListener;
import ore.api.Deleteable;
import ore.subscriber.CollectionChangeSubscription;

/**
 * This abstract class serves as the basis for classes that help detect mutations of collections which are implements by
 * a one-to-many relationship. Other classes provide the implementation for dealing with update, insert, and delete respectively.
 * All of these classes act as a filter which allows events to pass only if they
 * are related to a specific entity through a foreign-key value, hence the name
 * ValueMatchingListener. If the event occurs on a row which has a matching key
 * value, the event is passed on to the wrapped {@link CollectionChangeListener}
 * <br/> (package-protected)
 * 
 * @see ValueMatchingDeleteListener
 * @see ValueMatchingUpdateListener
 * @see ValueMatchingInsertListener
 */
public abstract class ValueMatchingListener implements Deleteable {
	protected String propertyName;
	protected Object value;
	protected CollectionChangeSubscription subscription;
	protected Method getter;
	private Collection owner;
	
	public void setOwner(Collection c) {
		owner = c;
	}
	
	public void delete() {
		owner.remove(this);
	}
	
	public ValueMatchingListener(CollectionChangeSubscription  subscription, String propertyName, Object value) {
		String getterName = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
		Class entityClass = subscription.getEntity().getClass();
		try {
			this.getter = entityClass.getMethod(getterName, null);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		this.subscription = subscription;
		this.propertyName = propertyName;
		this.value = value;
	}
	
	public Object getPropertyValue(Object entity, String property) {
		Object retVal = null;
		try {
			retVal = getter.invoke(entity, null); 
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return retVal;
	}
	
}
