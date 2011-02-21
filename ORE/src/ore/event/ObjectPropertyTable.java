package ore.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ore.subscriber.Subscription;

public class ObjectPropertyTable<T extends Subscription> {
	//Classname -> Key -> Property
	private Map<String, Map<String, Map<String, Set<T>>>> table = new HashMap<String, Map<String, Map<String, Set<T>>>>();

	public void addSubscription(String entityType, String key, String property, T s) {
		Map<String, Map<String, Set<T>>> byKey = table.get(entityType);
		if(byKey == null) {
			byKey = new HashMap<String, Map<String, Set<T>>>();
			table.put(entityType, byKey);
		}
		Map<String, Set<T>> byProperty = byKey.get(key);
		if(byProperty == null) {
			byProperty = new HashMap<String, Set<T>>();
			byKey.put(key, byProperty);
		}
		 Set<T> subs = byProperty.get(property);
		 if(subs == null) {
			 subs = new HashSet<T>();
			 byProperty.put(property, subs);
		 }
		 subs.add(s);
	}
	
	public Set<T> lookupSubscription(String entityType, String key, String property) {
		Map<String, Map<String, Set<T>>> byKey = table.get(entityType);
		if(byKey == null) {
			return new HashSet<T>();
		}
		Map<String, Set<T>> byProperty = byKey.get(key);
		if(byProperty == null) {
			return new HashSet<T>();
		}
		 Set<T> subs = byProperty.get(property);
		 if(subs == null) {
			 return new HashSet<T>();
		 } else {
			 return subs;
		 }
	}
	
	public void removeSubscription(String entityType, String key, String property, T s) {
		Map<String, Map<String, Set<T>>> byKey = table.get(entityType);
		if(byKey == null) {
			throw new IllegalArgumentException();
		} 
		Map<String, Set<T>> byProperty = byKey.get(key);
		if(byProperty == null) {
			throw new IllegalArgumentException();
		}
		Set<T> subs = byProperty.get(property);
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
