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
	public String elementAdded(Event event) {
		User user = (User) event.getNewValue();
		return toJSONObject(
				"type", quote("usersJoined"),
				"room", quote("any"),
				"users", "[" + user.toJSON() + "]" 
		);
	}
	
	@Override
	public String elementRemoved(Event event) {
		User user = (User) event.getNewValue();
		return toJSONObject(
				"type", quote("usersLeave"),
				"room", quote("any"),
				"users", "[" + user.toJSON() + "]" 
		);
	}
	
}