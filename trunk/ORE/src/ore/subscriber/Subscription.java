package ore.subscriber;

import java.io.PrintWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

import ore.api.Event;
import ore.cluster.ClusterManager;
import ore.exception.BrokenCometException;

public abstract class Subscription implements Flushable {
	private ConcurrentLinkedQueue<char[]> buffer = new ConcurrentLinkedQueue<char[]>();
	protected Subscriber subscriber;
	
	public Subscription(Subscriber subscriber) {
		this.subscriber = subscriber;
	}
	
	private void buffer(char[] data) {
		buffer.add(data);
	}
	
	protected void dispatch(char[] data, Event event) throws BrokenCometException {
		ClusterManager.getInstance().publish(data, event);
		print(data);
	}
	
	public void print(char[] data) throws BrokenCometException {
		synchronized(subscriber) {
			try {
				if(subscriber.isSuspended()) {
					PrintWriter out = subscriber.getContinuation().getServletResponse().getWriter();
					out.println(data);
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
				subscriber.getContinuation().getServletResponse().getWriter().print(data);
				gotData = true;
			}
			buffer.clear();
		} catch(Exception e) {
			throw new BrokenCometException(subscriber, e);
		}
		return gotData;
	}
}
