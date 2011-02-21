package ore.client;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

public class WebReader implements Runnable {
	private Machine machine;
	private String sessionID;
	private User user;

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

	public void run() {
		// Step 3
		while(true) {
			try {
				machine.receiveMessages(sessionID);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
