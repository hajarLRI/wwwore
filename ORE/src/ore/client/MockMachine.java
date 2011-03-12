package ore.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import ore.util.LogMan;

import org.json.JSONArray;
import org.json.JSONObject;

public class MockMachine extends Machine {

	public MockMachine(String ip, String port, String jmsPort) {
		super(ip, port, jmsPort);
	}
	private Map<Integer, Set<MockMachine>> serverSubscriptions = new HashMap<Integer, Set<MockMachine>>();
	private Map<Integer, Set<User>> clientSubscriptions = new HashMap<Integer, Set<User>>();
		
	private static AtomicInteger id = new AtomicInteger(-1);

	@Override
	public String connect() {
		int myId = id.incrementAndGet();
		return String.valueOf(myId);
	}
	
	@Override
	public String directed(JSONObject obj) {
		throw new UnsupportedOperationException();
	}
	
	@Override 
	public void join(User<Integer> user, String str) {
		for(Integer i : user.getInterests()) {
			Set<User> users = clientSubscriptions.get(i);
			if(users == null) {
				users = new HashSet<User>();
				clientSubscriptions.put(i, users);
			}
			users.add(user);
			handleBroadcast(i);
		}
	}
	
	private void handleBroadcast(Integer i) {
		List<Machine> machines = Machine.getMachines();
		for(Machine m : machines) {
			if(m != this) {
				((MockMachine) m).receiveBroadcastSubscription(i, this);
			}
		}
	}
	
	@Override 
	public JSONArray receiveMessages(String str, String str1) {
		throw new UnsupportedOperationException();
	}
	
	@Override 
	public StreamingResponse receiveMessagesStreaming(String sessionID, String redirect) {
		return new MockStreamingResponse();
	}
	
	private static class MockStreamingResponse implements StreamingResponse {
		
		@Override
		public InputStream getResponseBodyAsStream() throws IOException {
			String response = "{}";
			return new ByteArrayInputStream(response.getBytes());
		}

		@Override
		public void releaseConnection() {}
	}
	
	@Override 
	public void sendChat(String roomName, int userName, long timeStamp) {
		notifyUsers(Integer.parseInt(roomName));
		notifyMachines(Integer.parseInt(roomName));
	}
	
	@Override
	public void stopMe(String str) {
		throw new UnsupportedOperationException();
	}
	
	public void notifyUsers(Integer i) {
		Set<User> users = clientSubscriptions.get(i);
		if(users != null) {
			for(User user : users) {
				//TODO
			}
		}
	}
	
	private AtomicInteger totalWrites = new AtomicInteger(-1);
	private AtomicInteger totalSend = new AtomicInteger(-1);
	
	public double getRatio() {
		return (totalSend.intValue()/(double) totalWrites.intValue());
	}
	
	public void notifyMachines(Integer i) {
		totalWrites.incrementAndGet();
		Set<MockMachine> machines = serverSubscriptions.get(i);
		if(machines != null) {
			for(MockMachine machine : machines) {
				machine.receiveNotifyMachine(i);
				synchronized(this) {
					totalSend.incrementAndGet();
					if((totalSend.intValue() % 10) == 0) {
						//System.out.println(name() + "Send/Write: " + (totalSend.intValue()/(double) totalWrites.intValue()));
					}
				}
			}
		}
	}
	
	public String name() {
		return ip;
	}

	public void receiveNotifyMachine(Integer i) {
		notifyUsers(i);
	}
	
	public void receiveBroadcastSubscription(Integer i, MockMachine m) {
			Set<MockMachine> machines = serverSubscriptions.get(i);
			if(machines == null) {
				machines = new HashSet<MockMachine>();
				serverSubscriptions.put(i, machines);
			}
			machines.add(m);
	}
	
	@Override
	public void start() {
		
	}
	
}
