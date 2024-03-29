package ore.subscriber;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ore.cluster.Key;
import ore.util.JSONable;

public class SubscriberDigest implements JSONable {

	private Set<Key> keys = new HashSet<Key>();
	
	public Set<Key> getKeys() {
		return keys;
	}
	
	public SubscriberDigest(List<Subscription> subs) {
		for(Subscription s : subs) {
			addKey(s.getKey());
		}
	}
	
	public void addKey(Key k) {
		keys.add(k);
	}
	
	
	public SubscriberDigest() {}
	
	@Override
	public String toJSON() {
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		int size = keys.size();
		int i=0;
		for(Key k : keys) {
			sb.append("'" + k.toString() + "'");
			if(i != (size-1)) {
				sb.append(',');
			}
			i++;
		}
		sb.append(']');
		return sb.toString();
	}

}
