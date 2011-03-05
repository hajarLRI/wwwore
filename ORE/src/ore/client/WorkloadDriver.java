package ore.client;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import ore.hypergraph.Hypergraph;

public class WorkloadDriver {
	public static void main(String[] args) throws Exception {
		Machine.createMachines(Config.IPs, Config.httpPorts, Config.jmsPorts);
		List<Thread> threads = new LinkedList<Thread>();
		for (Machine m : Machine.machines) {
			Thread t = new Thread(m);
			threads.add(t);
			t.start();
		}
		for (Thread t : threads) {
			t.join();
		}
		WorkloadGenerator generator = null;
		if (Config.random) {
			generator = new RandomGenerator();
		} else {
			generator = new SimpleGenerator();
		}
		List<User<Integer>> users = null;
		if (!Config.generated) {
			users = generator.generate(Config.readers, Config.itemsPerUser,Config.overlap);
			FileOutputStream fos = new FileOutputStream("C:\\Temp\\test.text");
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(users);
			out.close();
		} else {
			FileInputStream fis = new FileInputStream("C:\\Temp\\test.text");
			ObjectInputStream in = new ObjectInputStream(fis);
			users = (LinkedList) in.readObject();
			in.close();
		}

		//ReaderWorkload read = new ReaderWorkload(users);
		
	    Hypergraph hg = User.makeHyperGraph(users); HMetis.shmetis(hg,Config.IPs.length, 5); 
	    PartitionedWorkload read = new PartitionedWorkload(users, hg);
		 
		read.run();
		Writers.loopWriters();
		// Thread.sleep(10000);
		// Config.redirectOK = "yes";
		// while(true) {
		// Thread.sleep(100);
		// read.changeAtRandom();
		// }
	}
}
