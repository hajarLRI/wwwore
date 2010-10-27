package ore.chat.action;

import static ore.util.HTTPServletUtil.getRequiredParameter;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import ore.api.CollectionChangeListener;
import ore.chat.entity.ChatSession;
import ore.chat.entity.User;

import org.hibernate.Session;

public class Leave extends Action {

	@Override
	protected void run(Session session, HttpServletRequest request, PrintWriter pw) {
		String roomName = getRequiredParameter(request, ROOM_NAME);
		String userName = getRequiredParameter(request, USER_NAME);
		ChatSession room = (ChatSession) session.get(ChatSession.class, roomName);
		User user = (User) session.createQuery("from User WHERE userName='" + userName + "'").uniqueResult();
		room.getCurrentUsers().remove(user);
		session.saveOrUpdate(room);
		List<CollectionChangeListener> e = (List<CollectionChangeListener>) request.getSession(true).getAttribute("events");
		for(CollectionChangeListener s : e) {
			s.delete();
		}
		e.clear();
	}

}
