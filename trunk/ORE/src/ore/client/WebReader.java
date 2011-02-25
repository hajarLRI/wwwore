package ore.client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WebReader implements Runnable {
	private Machine machine;
	private String sessionID;
	private User user;
	private boolean stop = false;
	
	public void stop() throws Exception {
		machine.stopMe(sessionID);
		stop = true;
	}

	public WebReader(Machine machine, User user) {
		this.machine = machine;
		this.user = user;
	}
	
	public void init() throws Exception {
		
		// Step 1
		sessionID = machine.connect();

		if (sessionID == null)
			return;

		// Step 2
		machine.join(user, sessionID);
	}

	private void redirect(JSONObject obj) throws Exception {
		String ip = obj.getString("ip");
		String port = obj.getString("port");
		Machine newMachine = Machine.getMachine(ip, port);
		this.machine = newMachine;
		init();
	}
	
	private void receive(JSONObject obj) throws Exception {
		String type = obj.getString("type");
		if(type.equals("chatMessage")) {
			receiveChat(obj);
		} else if(type.equals("redirect")) {
			redirect(obj);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	private void receiveChat(JSONObject chat) throws JSONException {
		String insertTime = chat.getString("message");
		long receiveTime = System.currentTimeMillis();
		long roundTime = receiveTime - Long.parseLong(insertTime);
		
		synchronized(Machine.class) {
			Config.readerResponses++;
			if (Config.timerFlag) {
				Config.avg = Config.avg + roundTime;
				System.out.println("Avg: " + Config.avg / Config.readerResponses );
				System.out.println("Throughput: " + Config.readerResponses/(double) ((receiveTime - Config.startTime)/(double) 1000));
			} else {
				if (Config.readerResponses > 1500) {
					Config.timerFlag = true;
					Config.readerResponses = 0;
					Config.startTime = System.currentTimeMillis();
				}
			}
		}
	}
	
	public void run() {
		// Step 3
		JSONArray arr = null;
		while(!stop) {
			try {
				arr = machine.receiveMessages(sessionID);
				if(arr != null) {
					for(int i=0; i < arr.length(); i++) {
						JSONObject obj = arr.getJSONObject(i);
						receive(obj);
					}
				} else {
					return;
				}
				Thread.sleep(Config.cometBackoff);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	}
}