package ore.chat.action;

import static ore.util.HTTPServletUtil.getRequiredParameter;
import ore.chat.entity.ChatSession;
import ore.chat.entity.User;

public class Leave extends Action {

	@Override
	protected void run() {
		String roomName = getRequiredParameter(request, ROOM_NAME);
		String userName = getRequiredParameter(request, USER_NAME);
		ChatSession room = (ChatSession) this.session.get(ChatSession.class, roomName);
		User user = (User) this.session.createQuery("from User WHERE userName='" + userName + "'").uniqueResult();
		room.getCurrentUsers().remove(user);
		session.saveOrUpdate(room);
	}

}
