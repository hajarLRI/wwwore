package ore.client;

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
		GetMethod method_tmp = null;
		HttpClient client = Machine.createClient();
		while (true) {
			try {
				long insertTime = System.currentTimeMillis();
				int id = MathUtil.randomInt(0, user.getInterests().size());
				Object item = user.getInterests().get(id);
				method_tmp = Machine.makeMethod(machine.getUrlPrefix() + "/chat",
						"none", "operation", "chat", "roomName", item.toString(),
						"userName", user.getID(), "message", insertTime);
				client.executeMethod(method_tmp);
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				method_tmp.releaseConnection();
			}
		}
	}

}
