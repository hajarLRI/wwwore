package ore.hypergraph;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class HyperNode<T, E> {
	private T data;
	private Hypergraph<T, E> graph;
	private Set<HyperEdge<E, T>> edges;
	
	public HyperNode(T data, Hypergraph<T, E> graph) {
		this.graph = graph;
		this.data = data;
	}
	
	public void addEdge(HyperEdge<E, T> edge) {
		if(edges == null) {
			edges = new HashSet<HyperEdge<E, T>>();
		}
		edges.add(edge);
	}
	
	public void print(PrintWriter pw) {
		pw.print(data.toString());
	}
}
