package ore.chat.action;

import static ore.util.HTTPServletUtil.getRequiredParameter;
import static ore.util.JSONUtil.quote;
import static ore.util.JSONUtil.toJSONObject;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import ore.api.CollectionChangeListener;
import ore.api.Event;
import ore.api.ORE;
import ore.api.PropertyChangeListener;
import ore.chat.entity.ChatSession;
import ore.chat.entity.User;
import ore.chat.event.MessageListener;
import ore.chat.event.UsersListener;
import ore.servlet.CookieFilter;

import org.hibernate.Session;

public class Join extends Action {

	@Override
	protected void run(Session session, HttpServletRequest request, PrintWriter pw) {
		try {
			String roomNamesString = getRequiredParameter(request, ROOM_NAME);
			String[] roomNames = roomNamesString.split("_");
			String userName = getRequiredParameter(request, USER_NAME);	
			User user = (User) session.createQuery("from User WHERE userName='" + userName + "'").uniqueResult();
			if(user == null) {
				user = new User(userName);
				session.saveOrUpdate(user);
			}

			for(String roomName : roomNames) {
				System.out.println("User " + CookieFilter.getSessionID() + " joined room " + roomName);
				ChatSession room = (ChatSession) session.get(ChatSession.class, roomName);
				Set<ChatSession> rooms = user.getRooms();
				if(rooms != null) {
					rooms.clear();
				}
				room.userJoined(user);
				MessageListener m = new MessageListener();
				//UsersListener u = new UsersListener();
				ORE.addCollectionChangeListener(String.valueOf(user.getId()), room, "messages", m);
				//ORE.addCollectionChangeListener(room, "currentUsers", u);
				pw.print(room.toJSON(session));
				session.saveOrUpdate(room);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
