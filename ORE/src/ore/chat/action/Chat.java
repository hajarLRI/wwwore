package ore.chat.action;

import ore.chat.entity.ChatMessage;
import ore.chat.entity.ChatSession;
import static ore.util.HTTPServletUtil.*;

public class Chat extends Action {

	@Override
	protected void run() {
		String roomName = getRequiredParameter(request, ROOM_NAME);
		String message = getRequiredParameter(request, MESSAGE);
		String userName = getRequiredParameter(request, USER_NAME);	
		ChatSession room = (ChatSession) session.get(ChatSession.class, roomName);
		ChatMessage chatMessage = new ChatMessage(userName, message, room);
		room.addMessage(chatMessage);
	}

}
