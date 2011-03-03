package ore.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import ore.util.MathUtil;

public class RandomGenerator implements WorkloadGenerator<Integer> {

	@Override
	public List<User<Integer>> generate(int clients, int itemsPerUser, double overlap) {
		List<User<Integer>> users = new LinkedList<User<Integer>>();
		for(int i=0; i < clients; i++) {
			User<Integer> user = new User<Integer>();
			int items = MathUtil.randomInt(1, itemsPerUser + 1);
			for(int j=0; j < items; j++) {
				Integer interest = MathUtil.randomInt(0, Config.num - 1);
				System.out.println("Interest(" + i + ", " + interest + ")");
				user.addInterest(interest);
			}
			users.add(user);
		}
		return users;
	}
	
	public static void main(String[] args) throws Exception {
		RandomGenerator s = new RandomGenerator();
		ore.hypergraph.Hypergraph graph = User.makeHyperGraph(s.generate(Config.readers, Config.itemsPerUser, Config.overlap));
		long start = System.currentTimeMillis();
		HMetis.shmetis(graph, 7, 5);
		long stop = System.currentTimeMillis();
		PrintWriter pw = new PrintWriter(System.out);
		graph.toDot(pw);
		pw.flush();
		System.out.println("Time: " + (stop-start));
	}

}
