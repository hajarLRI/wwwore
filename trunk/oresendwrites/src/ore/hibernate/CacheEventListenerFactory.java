package ore.hibernate;

import java.util.Properties;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import ore.util.LogMan;

/**
 * This class is never used. If implemented, it would allow interception of operations on the
 * Hibernate cache. Seems like this could be useful at some point. 
 */
public class CacheEventListenerFactory extends net.sf.ehcache.event.CacheEventListenerFactory {

	@Override
	public CacheEventListener createCacheEventListener(Properties arg0) {
		return new TestCacheEventListener();
	}

	static class TestCacheEventListener implements CacheEventListener {

		@Override
		public Object clone() {
			return new TestCacheEventListener();
		}
		
		@Override
		public void dispose() {
			
		}

		@Override
		public void notifyElementEvicted(Ehcache arg0, Element arg1) {
			LogMan.trace("Cache EVICTED: " + arg1.getKey().toString());
		}

		@Override
		public void notifyElementExpired(Ehcache arg0, Element arg1) {
			LogMan.trace("Cache EXPIRED: " + arg1.getKey().toString());
		}

		@Override
		public void notifyElementPut(Ehcache arg0, Element arg1)
				throws CacheException {
			LogMan.trace("Cache PUT: " + arg1.getKey().toString());
		}

		@Override
		public void notifyElementRemoved(Ehcache arg0, Element arg1)
				throws CacheException {
			LogMan.trace("Cache REMOVED: " + arg1.getKey().toString());
		}

		@Override
		public void notifyElementUpdated(Ehcache arg0, Element arg1)
				throws CacheException {
			LogMan.trace("Cache UPDATED: " + arg1.getKey().toString());
		}

		@Override
		public void notifyRemoveAll(Ehcache arg0) {
			LogMan.trace("Cache REMOVE_ALL. ");
		}
		
	}
}
