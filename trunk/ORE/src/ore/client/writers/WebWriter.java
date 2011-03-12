package ore.client.writers;

import ore.client.Machine;
import ore.client.User;
import ore.util.MathUtil;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

public class WebWriter extends Thread {
	
	private User user;
	private Machine machine;
	
	public WebWriter(User user, Machine machine) {
		this.user = user;
		this.machine = machine;
	}

	public void run() {
		while (true) {
			try {
				long insertTime = System.currentTimeMillis();
				int id = MathUtil.randomInt(0, user.getInterests().size());
				Object item = user.getInterests().get(id);
				machine.sendChat(item.toString(), user.getID(), insertTime);
				Thread.sleep(200);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}

}
