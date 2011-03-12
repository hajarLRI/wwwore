package ore.client.generators;

import java.util.List;

import ore.client.Config;
import ore.hypergraph.Hypergraph;

public class PowerLawGenerator implements WorkloadGenerator {
	@Override
	public List generate() {
		return Hypergraph.generatePowerLaw(Config.powerLawnumVertices, Config.powerLawnumEdges, Config.powerLawiterations).generate();
	}

}
