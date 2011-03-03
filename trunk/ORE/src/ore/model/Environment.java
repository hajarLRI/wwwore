package ore.model;

import java.util.Set;

public class Environment {
	private Set<Server> servers;
	private Set<PublisherClient> publishers;
	private Set<SubscriberClient> subscribers;
	
	public Environment(Set<Server> servers, Set<PublisherClient> publishers, Set<SubscriberClient> subscribers) {
		this.servers = servers;
		this.publishers = publishers;
		this.subscribers = subscribers;
	}

	public Set<? extends Task> getTasks() {
		return publishers;
	}
}
