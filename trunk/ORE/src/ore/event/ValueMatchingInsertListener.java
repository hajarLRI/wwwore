package ore.event;

import ore.api.Event;
import ore.exception.BrokenCometException;
import ore.hibernate.Metadata;
import ore.subscriber.CollectionChangeSubscription;

/**
 * @see ValueMatchingListener
 */
class ValueMatchingInsertListener extends ValueMatchingListener implements InsertListener {

	public ValueMatchingInsertListener(CollectionChangeSubscription subscription, String propertyName, Object value) {
		super(subscription, propertyName, value);
	}

	@Override
	public void insert(Event event) throws BrokenCometException {
		Object value = getPropertyValue(event.getEntity(), this.propertyName);
		if(this.value.equals(value)) {
			subscription.elementAdded(event);
		}
	}
}
