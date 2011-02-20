package ore.chat.action;

import static ore.util.HTTPServletUtil.getRequiredParameter;

import java.io.PrintWriter;

import javax.jms.JMSException;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;

import ore.api.ORE;
import ore.chat.entity.ChatSession;
import ore.chat.entity.User;
import ore.chat.event.MessageListener;

public class NewRoom extends Action {

	@Override
	protected void run(Session session, HttpServletRequest request, PrintWriter pw) {
		try {
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

			ORE.addCollectionChangeListener(String.valueOf(user.getId()), room, "messages", new MessageListener());
			session.save(room);
			session.saveOrUpdate(user);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
