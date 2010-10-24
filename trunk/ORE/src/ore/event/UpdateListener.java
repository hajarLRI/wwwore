package ore.event;

import ore.api.Event;
import ore.exception.BrokenCometException;

/**
 * Callback for notification of an update of a row in a database table
 * <br/>
 * (package-protected)
 * 
 * @see TableManager
 */
interface UpdateListener {
	public void update(Event event) throws BrokenCometException;
}
