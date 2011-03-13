package ore.chat.action;

import static ore.util.HTTPServletUtil.getRequiredParameter;

import java.io.PrintWriter;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import ore.chat.entity.ChatMessage;
import ore.chat.entity.ChatSession;
import ore.exception.BrokenCometException;

import org.hibernate.Session;

public class Chat extends Action {

	@Override
	protected void run(Session session, HttpServletRequest request, PrintWriter pw) {
		String roomName = getRequiredParameter(request, ROOM_NAME);
		String message = getRequiredParameter(request, MESSAGE);
		String userName = getRequiredParameter(request, USER_NAME);	
		//ChatSession room = (ChatSession) session.get(ChatSession.class, roomName);
		ChatSession room = new ChatSession();
		room.setCurrentUsers(new HashSet());
		room.setMessages(new HashSet());
		room.setRoomName(roomName);
		ChatMessage chatMessage = new ChatMessage(userName, message, room);
		try {
			room.addMessage(session, chatMessage);
		} catch (BrokenCometException e) {
			e.printStackTrace();
		}
	}

}
