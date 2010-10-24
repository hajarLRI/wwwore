package ore.chat.action;

import static ore.util.HTTPServletUtil.getRequiredParameter;
import ore.api.ORE;
import ore.chat.entity.ChatSession;
import ore.chat.entity.User;
import ore.chat.event.MessageListener;

public class NewRoom extends Action {

	@Override
	protected void run() {
		String roomName = getRequiredParameter(request, ROOM_NAME);
		String userName = getRequiredParameter(request, USER_NAME);	
		User user;
		user = (User) session.createQuery("from User WHERE userName='" + userName + "'").uniqueResult();
		if(user == null) {
			user = new User(userName);
		} else {
			user.getRooms().clear();
		}
		ChatSession room = new ChatSession(roomName);
		room.userJoined(user);
		ORE.addCollectionChangeListener(room, "messages", new MessageListener());
		session.save(room);
		session.saveOrUpdate(user);
	}

}
