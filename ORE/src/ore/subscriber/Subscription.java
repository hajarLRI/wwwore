package ore.subscriber;

import java.io.PrintWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.jms.JMSException;

import ore.api.Event;
import ore.cluster.ClusterManager;
import ore.exception.BrokenCometException;
import ore.util.LogMan;

public abstract class Subscription implements Flushable {
	private ConcurrentLinkedQueue<char[]> buffer = new ConcurrentLinkedQueue<char[]>();
	protected Subscriber subscriber;
	protected String className;
	public String getClassName() {
		return className;
	}

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
	
	public abstract void remove();
	
	private void buffer(char[] data) {
		buffer.add(data);
	}
	
	protected void dispatch(char[] data, Event event) throws BrokenCometException, JMSException {
		ClusterManager.getInstance().publish(data, event);
		print(data);
	}
	
	public void print(char[] data) throws BrokenCometException {
		synchronized(subscriber) {
			try {
				if(subscriber.isSuspended()) {
					PrintWriter out = subscriber.getContinuation().getServletResponse().getWriter();
					out.print('[');
					out.println(data);
					out.print(']');
					LogMan.info("Subscriber " + subscriber.getID() + " got pushed.");
					if(subscriber.isRepartitioning()) {
						SubscriberManager.getInstance().remove(subscriber);
					}
					subscriber.getContinuation().complete();
				} else {
					buffer(data);
					subscriber.getQueue().add(this);
				}
			} catch(Exception e) {
				throw new BrokenCometException(subscriber, e);
			}
		}
	}
	
	
	@Override
	public boolean flushEvents(PrintWriter pw) throws BrokenCometException {
		boolean gotData = false;
		try {
			pw.print("[");
			int i = 0;
			for(char[] data : buffer) {
				pw.print(data);
				gotData = true;
				if(i == (buffer.size()-1)) {
					pw.print(',');
				}
				i++;
			}
			pw.print("]");
			buffer.clear();
		} catch(Exception e) {
			throw new BrokenCometException(subscriber, e);
		}
		return gotData;
	}
	
	
}
