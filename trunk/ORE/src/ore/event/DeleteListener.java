package ore.event;

import ore.api.Event;
import ore.exception.BrokenCometException;

/**
 * Callback for notification of deletion of a row in a database table
 * <br/>
 * (package-protected)
 * 
 * @see TableManager
 */
interface DeleteListener {
	public void delete(Event event) throws BrokenCometException;
}
