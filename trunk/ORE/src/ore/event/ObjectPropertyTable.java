package ore.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ore.subscriber.Subscription;

public class ObjectPropertyTable {
	//Classname -> Key -> Property
	private Map<String, Map<String, Map<String, Set<Subscription>>>> table = new HashMap<String, Map<String, Map<String, Set<Subscription>>>>();

	public void addSubscription(String entityType, String key, String property, Subscription s) {
		Map<String, Map<String, Set<Subscription>>> byKey = table.get(entityType);
		if(byKey == null) {
			byKey = new HashMap<String, Map<String, Set<Subscription>>>();
			table.put(key, byKey);
		}
		Map<String, Set<Subscription>> byProperty = byKey.get(key);
		if(byProperty == null) {
			byProperty = new HashMap<String, Set<Subscription>>();
			byKey.put(key, byProperty);
		}
		 Set<Subscription> subs = byProperty.get(property);
		 if(subs == null) {
			 subs = new HashSet<Subscription>();
			 byProperty.put(property, subs);
		 }
		 subs.add(s);
	}
	
	public Set<Subscription> lookupSubscription(String entityType, String key, String property) {
		Map<String, Map<String, Set<Subscription>>> byKey = table.get(entityType);
		if(byKey == null) {
			return new HashSet<Subscription>();
		}
		Map<String, Set<Subscription>> byProperty = byKey.get(key);
		if(byProperty == null) {
			return new HashSet<Subscription>();
		}
		 Set<Subscription> subs = byProperty.get(property);
		 if(subs == null) {
			 return new HashSet<Subscription>();
		 } else {
			 return subs;
		 }
	}
	
	public void removeSubscription(String entityType, String key, String property, Subscription s) {
		Map<String, Map<String, Set<Subscription>>> byKey = table.get(entityType);
		if(byKey == null) {
			throw new IllegalArgumentException();
		} 
		Map<String, Set<Subscription>> byProperty = byKey.get(key);
		if(byProperty == null) {
			throw new IllegalArgumentException();
		}
		Set<Subscription> subs = byProperty.get(property);
		if(subs == null) {
			throw new IllegalArgumentException();
		}
		subs.remove(s);
		if(subs.size() == 0) {
			byProperty.remove(property);
		}
		if(byProperty.size() == 0) {
			byKey.remove(key);
		}
		if(byKey.size() == 0) {
			table.remove(entityType);
		}
	}
}
