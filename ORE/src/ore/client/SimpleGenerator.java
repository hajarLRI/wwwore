package ore.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class SimpleGenerator implements WorkloadGenerator<Integer> {

	@Override
	public List<User<Integer>> generate() {
		List<User<Integer>> users = new LinkedList<User<Integer>>();
		for(int i=0; i < Config.readers; i++) {
			User<Integer> user = new User<Integer>();
			for(int j=0; j < Config.itemsPerUser; j++) {
				Integer interest = (int) Math.ceil(((i*Config.itemsPerUser) * (1 - Config.overlap) + j));
				System.out.println("Interest(" + i + ", " + interest + ")");
				user.addInterest(interest);
			}
			users.add(user);
		}
		return users;
	}
	
	public static void main(String[] args) throws Exception {
		SimpleGenerator s = new SimpleGenerator();
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
