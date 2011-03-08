package ore.client.initializers;

import java.util.List;

import ore.client.Config;
import ore.client.HMetis;
import ore.client.Machine;
import ore.client.User;
import ore.client.WebReader;
import ore.hypergraph.Hypergraph;

public class PartitionedWorkload<T> extends ReaderWorkload<T> {

	private Hypergraph graph;
	
	@Override
	public ReaderWorkload initialize(List users) throws Exception {
		super.initialize(users);
		Hypergraph<Integer, Integer> hg = User.makeHyperGraph(users);	
		HMetis.shmetis(hg, Config.IPs.length, 5);
		this.graph = hg;
		return this;
	}
	
	@Override
	public void setup() {
		for(User<T> user : users) {
			int part = graph.getNode(user.getID()).getPart();
			Machine m = Machine.getMachine(part);
			WebReader wr = new WebReader(m, user);
			try {
				wr.init();
			} catch (Exception e) {
				e.printStackTrace();
			}
			runners.add(wr);
		}
	}

}
