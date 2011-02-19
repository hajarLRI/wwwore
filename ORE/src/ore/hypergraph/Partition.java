package ore.hypergraph;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class Partition {
	private int id;
	
	private Set<HyperNode<?, ?>> nodes = new HashSet<HyperNode<?, ?>>();
	
	public Partition(int id) {
		this.id = id;
	}
	
	public void add(HyperNode<?, ?>  node) {
		nodes.add(node);
	}
	
	public void toDot(PrintWriter pw) {
		pw.println("subgraph cluster" + id + " {");
		pw.println("color=black;");
		pw.println("label=\"Partition " + id + "\";");
		for(HyperNode<?, ?> node : nodes) {
			node.toDot(pw);
		}
		pw.println("}");
	}
}
