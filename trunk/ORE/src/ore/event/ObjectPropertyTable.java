package ore.event;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ore.cluster.Key;
import ore.subscriber.Subscription;

public class ObjectPropertyTable<T extends Subscription> {
	//Classname -> Key -> Property
	private Map<Key, Set<T>> table = new ConcurrentHashMap<Key, Set<T>>();

	public void addSubscription(T s) {
		 Set<T> subs = table.get(s.getKey());
		 if(subs == null) {
			 subs = Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
			 table.put(s.getKey(), subs);
		 }
		 subs.add(s);
	}
	
	public Set<T> lookupSubscription(Key key) {
		Set<T> ret = table.get(key);
		if(ret == null) {
			table.put(key, ret);
			return Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
		}
		return ret;
	}
	
	public void removeSubscription(T s) {
		Set<T> subs = table.get(s.getKey());
		subs.remove(s);
	}
	
	public void clear() {
		table.clear();
	}
}
