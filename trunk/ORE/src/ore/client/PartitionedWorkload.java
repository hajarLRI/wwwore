package ore.client;

import java.util.List;

import ore.hypergraph.Hypergraph;

public class PartitionedWorkload<T> extends ReaderWorkload<T> {

	private Hypergraph graph;
	
	public PartitionedWorkload(List<User<T>> users, Hypergraph graph) {
		super(users);
		this.graph = graph;
	}
	
	@Override
	public void setup() {
		int i = 0;
		for(User<T> user : users) {
			int part = graph.getNode(i).getPart();
			Machine m = Machine.getMachine(part);
			WebReader wr = new WebReader(m, user);
			try {
				wr.init();
			} catch (Exception e) {
				e.printStackTrace();
			}
			runners.add(wr);
			i++;
		}
	}

}
