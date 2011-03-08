package ore.client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Stack;

import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WebReader implements Runnable {
	private Machine machine;
	private String sessionID;
	private User user;
	private String redirect = "yes";
	public User getUser() {
		return user;
	}
	
	private boolean stop = false;
	
	public synchronized void stop() throws Exception {
		stop = true;
		machine.stopMe(sessionID);
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
	
	public void direct(String ip, String port) throws Exception {
		machine.redirect(Machine.getMachine(ip, port), sessionID);
	}

	private void redirect(JSONObject obj) throws Exception {
		redirect = "no";
		String ip = obj.getString("ip");
		String port = obj.getString("port");
		//System.out.println(digest.toString());
		Machine newMachine = Machine.getMachine(ip, port);
		this.machine = newMachine;
		System.out.println("GOT REDIRECTED: " + obj.getString("swap"));
		sessionID = newMachine.directed(obj);
	}
	
	private void receive(JSONObject obj) throws Exception {
		if(obj.has("type")) {
			String type = obj.getString("type");
			if(type.equals("chatMessage")) {
				receiveChat(obj);
			} else if(type.equals("redirect")) {
				redirect(obj);
			} 
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
		GetMethod response = null;
		while(!stop) {
			try {
				response = machine.receiveMessagesStreaming(sessionID, redirect);
				if(response != null) {
					Reader reader = new InputStreamReader(response.getResponseBodyAsStream());
					ResponseHandler handler = new ResponseHandler();
					JSONParser parser = new JSONParser();
					parser.parse(reader, handler);
				} else {
					return;
				}
				Thread.sleep(Config.cometBackoff);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				response.releaseConnection();
			}
		}
	
	}
	
	private class ResponseHandler implements ContentHandler {
		Stack<Object> building = new Stack<Object>();
		String currentKey = null;
		Object finalValue;
		
		private void finishValue(Object value) {
			if(building.size() > 0) {
				Object top = building.peek();
				if(top instanceof JSONArray) {
					JSONArray arr = (JSONArray) top;
					arr.put(value);
				} else if(top instanceof JSONObject) {
					JSONObject obj = (JSONObject) top;
					try {
						obj.put(currentKey, value);
					} catch (JSONException e) {
						throw new RuntimeException(e);
					}
				} else {
					throw new IllegalStateException();
				}
			} else if(value instanceof JSONObject){
				JSONObject obj = (JSONObject) value;
				try {
					receive(obj);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				throw new IllegalStateException();
			}
		}
		
		@Override
		public boolean endArray() throws ParseException, IOException {
			if(building.size() > 0) {
				Object obj = building.pop();
				finishValue(obj);
			} 
			return true;
		}

		@Override
		public void endJSON() throws ParseException, IOException {}

		@Override
		public boolean endObject() throws ParseException, IOException {
			Object obj = building.pop();
			finishValue(obj);
			return true;
		}

		@Override
		public boolean endObjectEntry() throws ParseException, IOException {
			this.currentKey = null;
			return true;
		}

		@Override
		public boolean primitive(Object arg0) throws ParseException, IOException {
			finishValue(arg0);
			return true;
		}

		@Override
		public boolean startArray() throws ParseException, IOException {
			if(building.size() > 0) {
				JSONArray arr = new JSONArray();
				building.push(arr);
			} 
			return true;
		}

		@Override
		public void startJSON() throws ParseException, IOException {}

		@Override
		public boolean startObject() throws ParseException, IOException {
			JSONObject obj = new JSONObject();
			building.push(obj);
			return true;
		}

		@Override
		public boolean startObjectEntry(String key) throws ParseException, IOException {
			currentKey = key;
			return true;
		}
		
	}
	
	public static void main(String[] args) {
		 String jsonText = "{\"first\": 123, \"second\": [{\"k1\":{\"id\":\"id1\"}}, 4, 5, 6, {\"id\": 123}], \"third\": 789, \"id\": null}";
		  JSONParser parser = new JSONParser();
		  TestHandler finder = new TestHandler();
		
		  try{
		      parser.parse(jsonText, finder, true);
		  }
		  catch(ParseException pe){
		    pe.printStackTrace();
		  }
	}
	
	private static class TestHandler implements ContentHandler {
		
		public TestHandler() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean endArray() throws ParseException, IOException {
			System.out.println("endArray");
			return true;
		}

		@Override
		public void endJSON() throws ParseException, IOException {}

		@Override
		public boolean endObject() throws ParseException, IOException {
			System.out.println("endArray");
			return true;
		}

		@Override
		public boolean endObjectEntry() throws ParseException, IOException {
			System.out.println("endObjectEntry");
			return true;
		}

		@Override
		public boolean primitive(Object arg0) throws ParseException, IOException {
			System.out.println("primitive");
			return true;
		}

		@Override
		public boolean startArray() throws ParseException, IOException {
			System.out.println("startArray");
			return true;
		}

		@Override
		public void startJSON() throws ParseException, IOException {}

		@Override
		public boolean startObject() throws ParseException, IOException {
			System.out.println("startObject");
			return true;
		}

		@Override
		public boolean startObjectEntry(String key) throws ParseException, IOException {
			System.out.println("startObjectEntry");
			return true;
		}
	}

	public Machine getMachine() {
		return machine;
	}
}
