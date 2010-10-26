package ore.chat.action;

import static ore.util.HTTPServletUtil.getRequiredParameter;
import static ore.util.JSONUtil.quote;
import static ore.util.JSONUtil.toJSONObject;

import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;

import ore.api.Event;
import ore.api.ORE;
import ore.api.PropertyChangeListener;
import ore.chat.entity.ChatSession;
import ore.chat.entity.User;
import ore.chat.event.MessageListener;
import ore.chat.event.UsersListener;

public class Join extends Action {

	@Override
	protected void run(Session session, HttpServletRequest request, PrintWriter pw) {
		try {
			String roomName = getRequiredParameter(request, ROOM_NAME);
			String userName = getRequiredParameter(request, USER_NAME);	
			User user = (User) session.createQuery("from User WHERE userName='" + userName + "'").uniqueResult();
			if(user == null) {
				user = new User(userName);
				session.saveOrUpdate(user);
			}
			ChatSession room = (ChatSession) session.get(ChatSession.class, roomName);
			Set<ChatSession> rooms = user.getRooms();
			if(rooms != null) {
				rooms.clear();
			}
			Set<User> users = room.getCurrentUsers();
			for(User current : users) {
				ORE.addPropertyChangeListener(current, "userName", new PropertyChangeListener() {
					@Override
					public void propertyChanged(PrintWriter pw, Event event)
							throws Exception {
						String oldName = (String) event.getOldValue();
						String newName = (String) event.getNewValue();
						pw.print(toJSONObject("type", 	"\"changeName\"", 
								"oldName",	quote(oldName), 
								"newName",	quote(newName)
						));
					}
				});
			}
			room.userJoined(user);
			ORE.addCollectionChangeListener(room, "messages", new MessageListener());
			ORE.addCollectionChangeListener(room, "currentUsers", new UsersListener());
			pw.print(room.toJSON(session));
			try {
				session.saveOrUpdate(room);
			} catch(Exception e) {
				System.out.println("JOIN_ERROR: " + session + ", " + room);
				if(session == null) {
					System.out.println("SESSION NULL");
				}
				if(room == null) {
					System.out.println("ROOM NULL");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
