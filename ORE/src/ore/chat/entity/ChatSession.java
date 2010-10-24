package ore.chat.entity;

import static ore.util.JSONUtil.quote;
import static ore.util.JSONUtil.toJSONArray;
import static ore.util.JSONUtil.toJSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ore.util.JSONable;

public class ChatSession implements JSONable {
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

	public void setMessages(List<ChatMessage> messages) {
		this.messages = messages;
	}

	private List<ChatMessage> messages;
	
	private Set<User> currentUsers = new HashSet<User>();
	
	public ChatSession() {}
	
	public ChatSession(String name) {
		this.roomName = name;
	}
	
	public String getRoomName() {
		return roomName;
	}
	
	public void addMessage(ChatMessage msg) {
		messages.add(msg);
	}
	
	public List<ChatMessage> getMessages() {
		return messages;
	}
	
	public void userJoined(User user) {
		currentUsers.add(user);
	}

	@Override
	public String toJSON() {
		return toJSONObject("type", quote("room"), 
							"name",	quote(roomName),
							"messages", toJSONArray(messages),
							"users", toJSONArray(currentUsers));
	}
}
