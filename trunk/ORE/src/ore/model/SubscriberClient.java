package ore.model;

import java.util.Set;

public class SubscriberClient implements Notifiable {
	private Server server;
	private User user;
	
	public Server getServer() {
		return server;
	}
	
	public void setServer(Server s) {
		this.server = s;
	}
	
	public Set<DataObject> getInterests() {
		return user.getInterests();
	}
	
	public void notify(DataObject dataObject) {
		//TODO Implement 
	}
}
