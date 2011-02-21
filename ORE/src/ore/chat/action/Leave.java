package ore.chat.action;

import static ore.util.HTTPServletUtil.getRequiredParameter;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import ore.api.CollectionChangeListener;
import ore.chat.entity.ChatSession;
import ore.chat.entity.User;
import ore.exception.NoSuchSubscriber;
import ore.servlet.CookieFilter;
import ore.subscriber.SubscriberManager;

import org.hibernate.Session;

public class Leave extends Action {

	@Override
	protected void run(Session session, HttpServletRequest request, PrintWriter pw) {
		String roomName = getRequiredParameter(request, ROOM_NAME);
		System.out.println("Leaving " + CookieFilter.getSessionID() + " from room " + roomName);
		String userName = getRequiredParameter(request, USER_NAME);
		ChatSession room = (ChatSession) session.get(ChatSession.class, roomName);
		User user = (User) session.createQuery("from User WHERE userName='" + userName + "'").uniqueResult();
		room.getCurrentUsers().remove(user);
		session.saveOrUpdate(room);
		try {
			SubscriberManager.getInstance().get(CookieFilter.getSessionID()).clear();
		} catch (NoSuchSubscriber e) {
			e.printStackTrace();
		}
	}

}
