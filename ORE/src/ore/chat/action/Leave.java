package ore.chat.action;

import static ore.util.HTTPServletUtil.getRequiredParameter;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;

import ore.chat.entity.ChatSession;
import ore.chat.entity.User;

public class Leave extends Action {

	@Override
	protected void run(Session session, HttpServletRequest request, PrintWriter pw) {
		String roomName = getRequiredParameter(request, ROOM_NAME);
		String userName = getRequiredParameter(request, USER_NAME);
		ChatSession room = (ChatSession) session.get(ChatSession.class, roomName);
		User user = (User) session.createQuery("from User WHERE userName='" + userName + "'").uniqueResult();
		room.getCurrentUsers().remove(user);
		session.saveOrUpdate(room);
	}

}
