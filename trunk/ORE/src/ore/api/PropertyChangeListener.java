package ore.api;

import java.io.PrintWriter;

/**
 * Users implement this interface to be notified when a 
 * property of a hibernate entity is modified directly. Does not apply
 * to collection properties which are mutated through Collection interface methods. 
 */
public interface PropertyChangeListener  {
	public String propertyChanged(Event event) throws Exception;
}
