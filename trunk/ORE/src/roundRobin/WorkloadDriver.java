package roundRobin;

import java.util.LinkedList;
import java.util.List;

public class WorkloadDriver {
	public static void main(String[] args) throws Exception {
		Machine.createMachines(Config.IPs, Config.httpPorts);
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
	}
}
