package ore.model;

import java.util.Set;

public class ServerSideSubscription implements Notifiable {
	private Server subscriber;
	private Set<DataObject> interests;
	
	public void notify(DataObject dataObject) {
		subscriber.notify(dataObject);
	}
	
	public Set<DataObject> getInterests() {
		return interests;
	}
}
