package ore.model;

import java.util.Set;

import ore.client.User;

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
		return null;
		
	}
	
	public void notify(DataObject dataObject) {
		//TODO Implement 
	}
}
