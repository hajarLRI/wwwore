package ore.client;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
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
		} else if(Config.graphGenerator) {
			generator = Hypergraph.generatePowerLaw(200, 200, 20);
		} else {
			generator = new SimpleGenerator();
		}
		List<User<Integer>> users = null;
		
		if (!Config.generated) {
			users = generator.generate();
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
		ReaderWorkload read;
		if(Config.readerWorkload) {
			read = new ReaderWorkload(users);
		} else {
			Hypergraph<Integer, Integer> hg = User.makeHyperGraph(users);	
			HMetis.shmetis(hg, Config.IPs.length, 5);
			read = new PartitionedWorkload(users, hg);
			PrintWriter pw = new PrintWriter(System.out);
			hg.toDot(pw);
			pw.flush();
		}
		
		read.run();
		WriterWorkload writers = new WriterWorkload(read.getReaders());
		writers.run();
//		Thread.sleep(1000);
//		Config.redirectOK = "no";
//		while(true) {
//			Thread.sleep(1000);
//			read.changeAtRandom();
//			int stopped = Integer.parseInt(read.stopAtRandom().getID());
//			
//			hg.removeNode(stopped);
//			User<Integer> u = User.newRandomUser(Config.itemsPerUser);
//			int part = hg.getLeastLoadedPartition();
//			System.out.println(part);
//			int userNumber = hg.addUserToPartition(Integer.parseInt(u.getID()), u, part);
//			int mostRelated = hg.findMostRelatedPartition(userNumber);
//			hg.moveNode(userNumber, mostRelated);
//			int nodeToSwap = hg.bestNodeForPartitionFromPartition(part, mostRelated);
//			hg.moveNode(nodeToSwap, part);
//			//
//			read.addUser(u, mostRelated);
//			User swappedUser = read.stop(nodeToSwap);
//			read.addUser(swappedUser, part);
//		}
	}
}
