package ore.client.generators;

import java.util.List;

import ore.hypergraph.Hypergraph;

public class SmallWorldGenerator implements WorkloadGenerator {
	private static int latticeSize = 20;
	private double clusteringExponent = .5;
	
	@Override
	public List generate() {
		return Hypergraph.generateKleinberg(latticeSize, clusteringExponent).generate();
	}
}
