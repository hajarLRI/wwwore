package ore.hypergraph;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class HyperNode<T, E> {
	private T data;
	private Hypergraph<T, E> graph;
	private Set<HyperEdge<E, T>> edges = new HashSet<HyperEdge<E, T>>();
	private int assignedPartition;
	
	public int relationWeight(HyperNode<T, E> other) {
		int weight = 0;
		Set<HyperEdge<E, T>> edgesClone = new HashSet<HyperEdge<E, T>>();
		edgesClone.addAll(edges);
		edgesClone.retainAll(other.edges);
		return edgesClone.size();
	}
	
	public int getPart() {
		return assignedPartition;
	}
	
	public void setPart(int part) {
		assignedPartition = part;
	}
	
	public T getData() {
		return data;
	}
	
	public HyperNode(T data, Hypergraph<T, E> graph) {
		this.graph = graph;
		this.data = data;
	}
	
	public void addEdge(HyperEdge<E, T> edge) {
		edges.add(edge);
	}
	
	public void print(PrintWriter pw) {
		pw.print(Integer.parseInt(data.toString()) + 1);
	}
	
	public void toDot(PrintWriter pw) {
		pw.println("u" + getData().toString() + " [shape=ellipse];");	
	}
}
