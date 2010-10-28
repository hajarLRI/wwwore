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
	
	public Subscriber getSubscriber() {
		return subscriber;
	}
	
	public Subscription(Subscriber subscriber) {
		this.subscriber = subscriber;
	}
	
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
					out.println(data);
					LogMan.info("Subscriber " + subscriber.getID() + " got pushed.");
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
			for(char[] data : buffer) {
				pw.print(data);
				gotData = true;
			}
			buffer.clear();
		} catch(Exception e) {
			throw new BrokenCometException(subscriber, e);
		}
		return gotData;
	}
}
