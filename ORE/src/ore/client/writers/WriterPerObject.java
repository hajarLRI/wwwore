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
			Thread thread = new Thread(new Running(i));
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
		private Machine machine;
		
		public Running(int id) {
			this.id = id;
			machine = Machine.getMachine(id % Machine.getNumMachines());
		}

		public void run() {
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
					
					machine.sendChat(id+"", id, insertTime);

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
				} 
			}
		}
	}
}
