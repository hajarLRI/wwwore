package ore.client.executors;

import java.util.LinkedList;
import java.util.List;

import ore.client.Config;
import ore.client.User;
import ore.client.WebReader;
import ore.client.initializers.ReaderWorkload;
import ore.hypergraph.Hypergraph;

public class GreedyExecutor implements WorkloadExecutor {

	@Override
	public void execute(ReaderWorkload read) throws Exception {
		List<User<Integer>> users = new LinkedList<User<Integer>>();
		List<WebReader> readers = read.getReaders();
		for(WebReader reader : readers) {
			users.add(reader.getUser());
		}
		Hypergraph<Integer, Integer> hg = User.makeHyperGraph(users);
		Thread.sleep(1000);
		Config.redirectOK = "no";
		while(true) {
			Thread.sleep(1000);
			read.changeAtRandom();
			int stopped = read.stopAtRandom().getID();
			
			hg.removeNode(stopped);
			User<Integer> u = User.newRandomUser(Config.itemsPerUser);
			int part = hg.getLeastLoadedPartition();
			System.out.println(part);
			int userNumber = hg.addUserToPartition(u.getID(), u, part);
			int mostRelated = hg.findMostRelatedPartition(userNumber);
			hg.moveNode(userNumber, mostRelated);
			int nodeToSwap = hg.bestNodeForPartitionFromPartition(part, mostRelated);
			hg.moveNode(nodeToSwap, part);
			//
			read.addUser(u, mostRelated);
			User swappedUser = read.stop(nodeToSwap);
			read.addUser(swappedUser, part);
		}
	}

}
