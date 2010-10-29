package ore.exception;

import ore.subscriber.Subscriber;

/**
 * This exception is thrown when Jetty is unable to reach a client through
 * the Comet connection. Presumably the client's browser has become unreachable 
 * unexpectedly (i.e. force quit). 
 */
@SuppressWarnings("serial")
public class BrokenCometException extends Exception {
	
	private Subscriber subscriber;
	
	/**
	 * Gives access to the {@link ore.subscriber.Subscriber} for which some Comet message was intended but
	 * unable to be delivered.
	 */
	public Subscriber getSubscriber() {
		return subscriber;
	}
	
	public BrokenCometException(Subscriber subscriber) {
		super();
		this.subscriber = subscriber;
	}
	
	public BrokenCometException(Subscriber subscriber, Exception wrappedException) {
		super(wrappedException);
		this.subscriber = subscriber;
	}
	
}
