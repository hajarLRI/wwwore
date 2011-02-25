package ore.client;

import java.util.LinkedList;
import java.util.List;

public class WorkloadDriver {
	public static void main(String[] args) throws Exception {
		Machine.createMachines(Config.IPs, Config.httpPorts, Config.jmsPorts);
		List<Thread> threads = new LinkedList<Thread>();
		for(Machine m : Machine.machines) {
			Thread t = new Thread(m);
			threads.add(t);
			t.start();
		}
		for(Thread t : threads) {
			t.join();
		}
		WorkloadGenerator generator = new SimpleGenerator();
		List<User> users = generator.generate(Config.readers, Config.itemsPerUser, Config.overlap);
		ReaderWorkload read = new ReaderWorkload(users);
		read.run();
		Writers.loopWriters();
		Thread.sleep(5000);
		Config.redirectOK = "true";
		while(true) {
			Thread.sleep(1000);
			read.changeOldest();
		}
	}
}