package ore.client.generators;

import java.util.List;

import ore.client.Config;
import ore.hypergraph.Hypergraph;

public class SmallWorldGenerator implements WorkloadGenerator {
	
	@Override
	public List generate() {
		return Hypergraph.generateKleinberg(Config.rowSize, Config.columnSize, Config.isToroidal, Config.clusteringExponent).generate();
	}
}
