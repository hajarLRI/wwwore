package ore.chat.entity;

import static ore.util.JSONUtil.*;
import ore.util.JSONable;

public class ChatMessage implements JSONable {
	private int id;
	private String userName;
	private String message;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ChatSession getSession() {
		return session;
	}

	public void setSession(ChatSession session) {
		this.session = session;
	}

	private ChatSession session;
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ChatMessage(String userName, String message, ChatSession session) {
		this.userName = userName;
		this.message = message;
		this.session = session;
	}
	
	public ChatMessage() {}
	
	public String toJSON() {
		return toJSONObject("type", 	"\"chatMessage\"", 
							"userName",	quote(userName), 
							"message", 	quote(message),
							"room", 	quote(session.getRoomName()));
	}
}
