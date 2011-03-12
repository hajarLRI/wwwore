package ore.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Server implements Notifiable {
	private Map<DataObject, Set<ServerSideSubscription>> serverSubscriptions = new HashMap<DataObject, Set<ServerSideSubscription>>();
	private Map<DataObject, Set<SubscriberClient>> clientSubscriptions = new HashMap<DataObject, Set<SubscriberClient>>();
	
	public void subscribeClient(SubscriberClient sc) {
		subscribe(sc, clientSubscriptions);
	}
	
	public void unsubscribeClient(SubscriberClient sc) {
		unsubscribe(sc, clientSubscriptions);
	}
	
	public void subscribeServer(ServerSideSubscription sc) {
		subscribe(sc, serverSubscriptions);
	}
	
	public void unsubscribeServer(ServerSideSubscription sc) {
		unsubscribe(sc, serverSubscriptions);
	}
	
	public void publishTo(DataObject dataObject) {
		sendNotification(dataObject, clientSubscriptions);
		sendNotification(dataObject, serverSubscriptions);
	}
	
	@Override
	public void notify(DataObject dataObject) {
		sendNotification(dataObject, clientSubscriptions);
	}
	
	@Override
	public Set<DataObject> getInterests() {
		throw new UnsupportedOperationException();
	}
	
	private <T extends Notifiable> void subscribe(T sc, Map<DataObject, Set<T>> map) {
		Set<DataObject> interests = sc.getInterests();
		for(DataObject interest : interests) {
			Set<T> subscriberClients = map.get(interest);
			if(subscriberClients == null) {
				subscriberClients = new HashSet<T>();
				map.put(interest, subscriberClients);
			}
			subscriberClients.add(sc);
		}
	}
	
	private <T extends Notifiable> void unsubscribe(T sc, Map<DataObject, Set<T>> map) {
		Set<DataObject> interests = sc.getInterests();
		for(DataObject interest : interests) {
			Set<T> subscriberClients = map.get(interest);
			assert(subscriberClients != null);
			subscriberClients.remove(sc);
			if(subscriberClients.size() == 0) {
				map.remove(interest);
			}
		}
	}
	
	private <T extends Notifiable> void sendNotification(DataObject dataObject, Map<DataObject, Set<T>> subscribers) {
		Set<T> targets = subscribers.get(dataObject);
		if(targets != null) {
			for(Notifiable sc : targets) {
				sc.notify(dataObject);
			}
		}
	}
	
}
