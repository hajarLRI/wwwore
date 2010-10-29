package ore.api;

/**
 * Context data associated with an event for which a user wants to be notified. 
 */
public class Event {

	private Object entity;
	private String propertyName;
	private Object oldValue;
	private Object newValue;
	private EventType type;
	
	public EventType getType() {
		return type;
	}
	
	public Event(Object entity, String propertyName, Object oldValue, Object newValue, EventType type) {
		this.type = type;
		this.entity = entity;
		this.propertyName = propertyName;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	/**
	 * The hibernate object on which the event occurred
	 */
	public Object getEntity() {
		return entity;
	}
	
	/**
	 * The property of the entity on which the event occurred
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Applies only to propertyChange events. Gives access to the previous value
	 * of the property. 
	 */
	public Object getOldValue() {
		return oldValue;
	}
	
	/**
	 * Applies to elementAdded, elementRemoved, and propertyChanged.
	 * For elementAdded and elementRemoved, it gives access to the element added
	 * or removed (respectively). For propertyChanged events it gives access to
	 * the new property value.
	 */
	public Object getNewValue() {
		return newValue;
	}
	
	public enum EventType {PropertyChanged, CollectionChanged};
}
