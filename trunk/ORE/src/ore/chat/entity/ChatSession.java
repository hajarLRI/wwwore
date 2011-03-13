package ore.chat.entity;

import static ore.util.JSONUtil.quote;
import static ore.util.JSONUtil.toJSONArray;
import static ore.util.JSONUtil.toJSONObject;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import ore.event.EventManager;
import ore.exception.BrokenCometException;

import org.hibernate.Session;

public class ChatSession {
	private String roomName;
	
	public Set<User> getCurrentUsers() {
		return currentUsers;
	}

	public void setCurrentUsers(Set<User> currentUsers) {
		this.currentUsers = currentUsers;
	}

	public void setRoomName(String name) {
		this.roomName = name;
	}

	public void setMessages(Set<ChatMessage> messages) {
		this.messages = messages;
	}

	private Set<ChatMessage> messages;
	
	private Set<User> currentUsers = new HashSet<User>();
	
	public ChatSession() {}
	
	public ChatSession(String name) {
		this.roomName = name;
	}
	
	public String getRoomName() {
		return roomName;
	}
	
	public void addMessage(Session s, ChatMessage msg) throws BrokenCometException {
		//try {
		//	s.createSQLQuery("INSERT INTO chatmessages VALUES ('" + msg.getId() + "','" + msg.getUserName() + "','" + msg.getMessage() + "','" + msg.getSession().getRoomName() +"')").executeUpdate();
		//} catch(Exception e) {
		//	e.printStackTrace();
		//}
		String user = msg.getUserName();
		EventManager.getInstance().collectionElementAdded(user, this, "messages", msg);
	}

	public Set<ChatMessage> getMessages() {
		return messages;
	}
	
	public void userJoined(User user) {
		currentUsers.add(user);
	}

	
	public String toJSON(Session s) {
		//List newMessages = s.createFilter(this.getMessages(), "").setMaxResults(10).list();
		return toJSONObject("type", quote("room"), 
							"name",	quote(roomName),
							"messages", toJSONArray(new LinkedList()),
							"users", toJSONArray(currentUsers));
	}
}
