package ore.client.writers;

import ore.client.Config;
import ore.client.Machine;
import ore.client.initializers.WorkloadInitializer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

public class WriterPerObject implements WriterWorkload {
	private int numObjects;
	private static int msgs = 0;
	
	public void init(WorkloadInitializer readerWorkload) {
		this.numObjects = readerWorkload.getNumObjects();
	}
	
	public void run()  {
		for (int i = 0; i < numObjects; i++) {
			HttpClient client = Machine.createClient();
			Thread thread = new Thread(new Running(i, client));
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
			String st = null;
			int numOfMachines = 0;
			double chunkSize = 0.0;
			try {
				numOfMachines = Config.IPs.length;
				chunkSize = Math.ceil(Config.num / numOfMachines) + 1;
				double position = id / chunkSize;
				int index = (int) position;
				st = "http://" + Config.IPs[index] + ':'
						+ Config.httpPorts[index] + '/' + Config.PROJECT;
			} catch (Exception e) {
				System.err.println("hello" + Config.num + "   " + numOfMachines
						+ "   " + chunkSize + "   " + id);
			}
			return st;
		}

		public void run() {
			GetMethod method_tmp = null;
			long st = System.currentTimeMillis();
			boolean start = false;
			while (true) {
				try {
					long insertTime = System.currentTimeMillis();

					if (id == (Config.num - 1)) {
						start = true;
						// System.err.println(num+" Writers created");
					}
					// System.out.println("Write: " + id + ", to ");
					getAddress();

					method_tmp = Machine.makeMethod(getAddress() + "/chat",
							"none", "operation", "chat", "roomName", id,
							"userName", id, "message", insertTime);

					client.executeMethod(method_tmp);

					// method_tmp.releaseConnection();
					// client.getHttpConnectionManager().closeIdleConnections(0);

					synchronized (WriterPerObject.class) {
						if (start) {
							msgs++;
							long stop = System.currentTimeMillis();
							double elapsed = (stop - st) / (double) 1000;
							// System.out.println("Write/Sec: " +
							// ((double)msgs/(double)elapsed));
						}
					}
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					method_tmp.releaseConnection();
					client.getHttpConnectionManager().closeIdleConnections(0);
				}
			}
		}
	}
}
