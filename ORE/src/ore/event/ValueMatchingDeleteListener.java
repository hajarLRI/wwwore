package ore.event;

import ore.api.Event;
import ore.exception.BrokenCometException;
import ore.hibernate.Metadata;
import ore.subscriber.CollectionChangeSubscription;

/**
 * @see ValueMatchingListener
 */
class ValueMatchingDeleteListener extends ValueMatchingListener implements DeleteListener {

	public ValueMatchingDeleteListener(CollectionChangeSubscription subscription, String propertyName, Object value) {
		super(subscription, propertyName, value);
	}

	@Override
	public void delete(Event event) throws BrokenCometException {
		Object value = getPropertyValue(event.getEntity(), this.propertyName);
		if(this.value.equals(value)) {
			subscription.elementRemoved(event);
		}
	}

}
