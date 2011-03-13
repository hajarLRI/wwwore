package ore.client.generators;

import java.util.List;

import ore.client.Config;
import ore.hypergraph.Hypergraph;

public class ErdosRenyiGenerator implements WorkloadGenerator {

	@Override
	public List generate() throws Exception {
		return Hypergraph.generateErdosRenyi(Config.nodeSize, Config.p).generate();
	}

}
