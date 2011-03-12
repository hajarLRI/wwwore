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
		HMetis.shmetis(hg, Config.IPs.length, Config.ubFactor);
		this.graph = hg;
		return this;
	}
	
	@Override
	public void setup() {
		for(final User<T> user : users) {
			int part = graph.getNode(user.getID()).getPart();
			final Machine m = Machine.getMachine(part);
			final WebReader wr = WebReader.create(m, user);
			try {
				wr.init();
			} catch (Exception e) {
				e.printStackTrace();
			}
			runners.add(wr);
		}
		for(final WebReader runner : runners) {
			try {
				new Thread( new Runnable() { public void run() {
					try {
						runner.execute();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}}).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
