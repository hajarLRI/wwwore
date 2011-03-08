package ore.client.generators;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import ore.client.Config;
import ore.client.HMetis;
import ore.client.User;
import ore.util.MathUtil;

public class RandomGenerator implements WorkloadGenerator<Integer> {

	@Override
	public List<User<Integer>> generate() {
		List<User<Integer>> users = new LinkedList<User<Integer>>();
		for(int i=0; i < Config.readers; i++) {
			User<Integer> user = User.newRandomUser(Config.itemsPerUser);
			users.add(user);
		}
		return users;
	}
	
	public static void main(String[] args) throws Exception {
		RandomGenerator s = new RandomGenerator();
		ore.hypergraph.Hypergraph graph = User.makeHyperGraph(s.generate());
		long start = System.currentTimeMillis();
		HMetis.shmetis(graph, 7, 5);
		long stop = System.currentTimeMillis();
		PrintWriter pw = new PrintWriter(System.out);
		graph.toDot(pw);
		pw.flush();
		System.out.println("Time: " + (stop-start));
	}

}
