package ore.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ore.cluster.Key;
import ore.subscriber.Subscription;

public class ObjectPropertyTable<T extends Subscription> {
	//Classname -> Key -> Property
	private Map<Key, Set<T>> table = new HashMap<Key, Set<T>>();

	public void addSubscription(T s) {
		 Set<T> subs = table.get(s.getKey());
		 if(subs == null) {
			 subs = new HashSet<T>();
			 table.put(s.getKey(), subs);
		 }
		 subs.add(s);
	}
	
	public Set<T> lookupSubscription(Key key) {
		Set<T> ret = table.get(key);
		if(ret == null) {
			table.put(key, ret);
			return new HashSet<T>();
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
