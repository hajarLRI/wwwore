package ore.subscriber;

import javax.jms.JMSException;

import ore.api.Event;
import ore.cluster.ClusterManager;
import ore.cluster.Key;
import ore.exception.BrokenCometException;
import ore.hibernate.Metadata;

public abstract class Subscription {
	
	protected Subscriber subscriber;
	protected Key key;
	
	public Key getKey() {
		return key;
	}
	
	public abstract void remove();

	public Subscriber getSubscriber() {
		return subscriber;
	}
	
	public Subscription(Subscriber subscriber, String className, String key, String property) {
		this.subscriber = subscriber;
		this.key = Key.create(className, key, property);
	}

	protected void dispatch(String data, Event event) throws BrokenCometException, JMSException {
		ClusterManager.getInstance().publish("", data, key);
		print(data);
	}
	
	public void print(String data) throws BrokenCometException {
		subscriber.print(data);
	}
	
}
