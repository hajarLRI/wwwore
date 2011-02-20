package ore.cluster;

import ore.subscriber.Subscription;

public class RemoteSubscriber {

	private String userID;
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public Peer getHost() {
		return host;
	}

	private Peer host;
	
	public RemoteSubscriber(String userID, Peer host) {
		this.userID = userID;
		this.host = host;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof RemoteSubscriber) {
			RemoteSubscriber other = (RemoteSubscriber) obj;
			return userID.equals(other.userID) && host.equals(other.host);
		}
		return false;
	}
	
	public int hashCode() {
		int result = 17;
		result = 37*result + userID.hashCode();
		result = 37*result + host.hashCode();
		return result;
	}
}
