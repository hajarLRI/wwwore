package ore.chat.entity;

import static ore.util.JSONUtil.quote;
import static ore.util.JSONUtil.toJSONArray;
import static ore.util.JSONUtil.toJSONObject;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ore.chat.servlet.ChatServlet;
import ore.event.EventManager;

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
	
	public void addMessage(Session s, ChatMessage msg) {
		synchronized(ChatSession.class) {
			msg.setId(ChatServlet.msgCount++);
		}
		boolean finished = false;
		while(!finished) {
			try {
				s.createSQLQuery("INSERT INTO chatmessages VALUES (" + msg.getId() + ",'" + msg.getUserName() + "','" + msg.getMessage() + "','" + msg.getSession().getRoomName() +"')").executeUpdate();
				finished = true;
			} catch(Exception e) {
				System.out.println(msg);
				System.out.println(msg.getId());
				System.out.println(msg.getUserName());
				System.out.println(msg.getMessage());
				System.out.println(msg.getSession().getRoomName());
			}
		}
		EventManager.getInstance().collectionElementAdded(this, "messages", msg);
	}

	public Set<ChatMessage> getMessages() {
		return messages;
	}
	
	public void userJoined(User user) {
		currentUsers.add(user);
	}

	
	public String toJSON(Session s) {
		List newMessages = s.createFilter(this.getMessages(), "ORDER BY id DESC LIMIT 10").list();
		return toJSONObject("type", quote("room"), 
							"name",	quote(roomName),
							"messages", toJSONArray(newMessages),
							"users", toJSONArray(currentUsers));
	}
}
