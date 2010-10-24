/**
 * 
 */
package ore.chat.event;

import static ore.util.JSONUtil.quote;
import static ore.util.JSONUtil.toJSONObject;

import java.io.PrintWriter;

import ore.api.DefaultCollectionChangeListener;
import ore.api.Event;
import ore.chat.entity.User;

public class UsersListener extends DefaultCollectionChangeListener {
	
	@Override
	public void elementAdded(PrintWriter pw, Event event) {
		User user = (User) event.getNewValue();
		pw.print(toJSONObject(
				"type", quote("usersJoined"),
				"room", quote("any"),
				"users", "[" + user.toJSON() + "]" 
		));
	}
	
	@Override
	public void elementRemoved(PrintWriter pw, Event event) {
		User user = (User) event.getNewValue();
		pw.print(toJSONObject(
				"type", quote("usersLeave"),
				"room", quote("any"),
				"users", "[" + user.toJSON() + "]" 
		));
	}
	
}