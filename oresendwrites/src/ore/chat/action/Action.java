package ore.chat.action;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;

public abstract class Action {
	public static final String ROOM_NAME = "roomName";
	public static final String OPERATION = "operation";
	public static final String NEW_ROOM = "new";
	public static final String CHAT = "chat";
	public static final String MESSAGE = "message";
	public static final String USER_NAME = "userName";
	public static final String OLD_NAME = "oldName";
	public static final String NEW_NAME = "newName";
	public static final String CHANGE_NAME = "changeName";
	public static final String JOIN = "join";
	public static final String SESSION_ID = "sessionID";
	public static final String CHECK_ROOMS = "checkRooms";
	public static final String LEAVE = "leave";
	
	public void service(Session session, HttpServletRequest request, PrintWriter pw) {
		run(session, request, pw);
	}
	
	protected abstract void run(Session session, HttpServletRequest request, PrintWriter pw);
	
}
