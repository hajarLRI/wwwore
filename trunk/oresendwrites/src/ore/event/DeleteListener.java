package ore.event;

import ore.api.Deleteable;
import ore.api.Event;
import ore.exception.BrokenCometException;

/**
 * Callback for notification of deletion of a row in a database table
 * <br/>
 * (package-protected)
 * 
 * @see TableManager
 */
interface DeleteListener extends Deleteable {
	public void delete(Event event) throws BrokenCometException;
}
