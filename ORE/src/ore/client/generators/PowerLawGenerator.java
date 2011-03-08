package ore.client.generators;

import java.util.List;

import ore.hypergraph.Hypergraph;

public class PowerLawGenerator implements WorkloadGenerator {
	private static int numVertices = 200;
	private static int numEdges = 200;
	private static int iterations = 20;
	
	@Override
	public List generate() {
		return Hypergraph.generatePowerLaw(numVertices, numEdges, iterations).generate();
	}

}
