package ore.client;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

public class Writers {

	private static int num = (int) Math.ceil(Config.readers * (Config.itemsPerUser * (1-Config.overlap)));
	static {
		System.out.println("Num of Writers: " + num);
	}
	private static int msgs = 0;

	public static void loopWriters() throws InterruptedException{
		for(int i = 0; i < num; i++) {
			HttpClient client = Machine.createClient();
			Thread thread = new Thread(new Running(i , client));
			Thread.sleep(50);
			thread.start();
		}
		
	}

	static class Running implements Runnable {
		private int id;
		private HttpClient client;

		public Running(int id, HttpClient client) {
			this.id = id;
			this.client = client;
		}
		
		private String getAddress() {
			int numOfMachines = Config.IPs.length;
			double chunkSize = num / numOfMachines;
			double position = id / chunkSize;
			int index = (int) position;
			return "http://" + Config.IPs[index] + ':' + Config.httpPorts[index] + '/' + Config.PROJECT;
		}
 
		public void run() {
			GetMethod method_tmp = null;
			long st = System.currentTimeMillis();
			boolean start = false;
			while(true) {   
				try {
					long insertTime = System.currentTimeMillis();

					if (id==(num-1)) { 
						start = true;
						System.err.println(num+" Writers created");
					}
					//System.out.println("Write: " + id + ", to ");
					getAddress();
					
					method_tmp = Machine.makeMethod(getAddress() + "/chat", "none", "operation", "chat", "roomName", id, "userName", id, "message", insertTime);

					client.executeMethod(method_tmp);

					//method_tmp.releaseConnection();
					//client.getHttpConnectionManager().closeIdleConnections(0);
					
					synchronized(Writers.class) {
						if(start) {
							msgs++;
							long stop = System.currentTimeMillis();
							double elapsed = (stop-st)/(double) 1000;
							System.out.println("Write/Sec: " + ((double)msgs/(double)elapsed));
						}
					}
					Thread.sleep(1000);
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					method_tmp.releaseConnection();
					client.getHttpConnectionManager().closeIdleConnections(0);
				}
			}
		}
	}
}
