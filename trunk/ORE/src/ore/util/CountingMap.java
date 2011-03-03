package ore.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ore.subscriber.Subscriber;

public class CountingMap  {

	private boolean nonNegative = false;
	
	public CountingMap(boolean nonNegative) {
		this.nonNegative = nonNegative;
	}
	
	private Map<Subscriber, Integer> map = new ConcurrentHashMap<Subscriber, Integer>();
	
	public void inc(Subscriber t) {
		Integer count = map.get(t);
		if(count == null) {
			map.put(t, 1);
		} else {
			map.put(t, count+1);
		}
	}
	
	public void dec(Subscriber t) {
		Integer count = map.get(t);
		if(count == null) {
			if(nonNegative) {
				throw new IllegalStateException();
			} else {
				map.put(t, -1);
			}
		} else {
			map.put(t, count-1);
		}
	}
	
	public Integer get(Subscriber t) {
		Integer count = map.get(t);
		if(count == null) {
			return 0;
		} else {
			return count;
		}
	}
	
	public Subscriber max() {
		int max = Integer.MIN_VALUE;
		Subscriber maxKey = null;
		for(Map.Entry<Subscriber, Integer> entry : map.entrySet()) {
			Integer value = entry.getValue();
			Subscriber key = entry.getKey();
			if((value > max) && (key.isSuspended())){
				max = value;
				maxKey = key;
			}
		}
		return maxKey;
	}
}
