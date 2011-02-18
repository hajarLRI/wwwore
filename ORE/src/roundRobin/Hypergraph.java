package roundRobin;

import java.io.PrintWriter;
import java.util.List;

public class Hypergraph {
	public static void main(String[] args) {
		WorkloadGenerator generator = new SimpleGenerator();
		List<User<Integer>> users = generator.generate(Config.readers, Config.itemsPerUser, Config.overlap);
		PrintWriter pw = new PrintWriter(System.out);
		User.printHyperGraph(users, pw);
		pw.flush();
		pw.close();
	}
}
