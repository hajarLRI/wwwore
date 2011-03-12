package ore.hypergraph;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class Partition<V, E> {
	private int id;
	
	private Set<HyperNode<V, E>> nodes = new HashSet<HyperNode<V, E>>();
	
	public void removeNode(HyperNode<V, E> node) {
		nodes.remove(node);
	}
	
	public int getSize() {
		return nodes.size();
	}
	
	public Partition(int id) {
		this.id = id;
	}
	
	public void add(HyperNode<V, E>  node) {
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
	
	public void toCytoscape(PrintWriter pw) {
		for(HyperNode<?, ?> node : nodes) {
			node.toCytoscape(id, pw);
		}
	}

	public Set<HyperNode<V, E>> getNodes() {
		return nodes;
	}
}
