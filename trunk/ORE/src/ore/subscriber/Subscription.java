package ore.subscriber;

import java.io.PrintWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.jms.JMSException;

import ore.api.Event;
import ore.cluster.ClusterManager;
import ore.exception.BrokenCometException;
import ore.hibernate.Metadata;
import ore.util.LogMan;

public abstract class Subscription {
	
	protected Subscriber subscriber;
	protected String className;
	public String getClassName() {
		return className;
	}

	public abstract void remove();
	
	public void setClassName(String className) {
		this.className = className;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	protected String key;
	protected String property;
	
	public Subscriber getSubscriber() {
		return subscriber;
	}
	
	public Subscription(Subscriber subscriber, String className, String key, String property) {
		this.subscriber = subscriber;
		this.className = className;
		this.key = key;
		this.property = property;
	}

	protected void dispatch(String data, Event event) throws BrokenCometException, JMSException {
		String property = event.getPropertyName();
		String className = event.getEntity().getClass().getName();
		String id = Metadata.getPrimaryKeyValue(event.getEntity()).toString();
		ClusterManager.getInstance().publish("", data, className, id, property);
		print(data);
	}
	
	public void print(String data) throws BrokenCometException {
		subscriber.print(data);
	}
	
}
