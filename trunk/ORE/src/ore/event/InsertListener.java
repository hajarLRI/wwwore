package ore.event;

import ore.api.Deleteable;
import ore.api.Event;
import ore.exception.BrokenCometException;

/**
 * Callback for notification of insertion of a row in a database table
 * <br/>
 * (package-protected)
 * 
 * @see TableManager
 */
interface InsertListener extends Deleteable {
	public void insert(Event event) throws BrokenCometException;
}
