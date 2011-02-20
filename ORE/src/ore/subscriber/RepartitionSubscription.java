package ore.subscriber;

import ore.exception.BrokenCometException;

public class RepartitionSubscription extends Subscription {

	public RepartitionSubscription(Subscriber subscriber) {
		super(subscriber);
	}

	public void repartition(String ipAddress, String port) throws BrokenCometException {
		String message = "{\"type\":'redirect',\"ip\":'" + ipAddress +"',\"port\"='" + port + "'}";
		this.print(message.toCharArray());
	}
}
