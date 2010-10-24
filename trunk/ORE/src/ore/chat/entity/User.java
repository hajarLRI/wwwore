package ore.chat.entity;

import java.util.Set;

import ore.util.JSONUtil;
import ore.util.JSONable;

public class User implements JSONable {
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String name) {
		this.userName = name;
	}
	
	public Set<ChatSession> getRooms() {
		return rooms;
	}
	
	public void setRooms(Set<ChatSession> rooms) {
		this.rooms = rooms;
	}
	
	private int id;
	private String userName;
	private Set<ChatSession> rooms;
	
	public User() {
		id = 0;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public User(String name) {
		this.id = 0;
		this.userName = name;
	}

	@Override
	public String toJSON() {
		return JSONUtil.quote(userName);
	}
}
