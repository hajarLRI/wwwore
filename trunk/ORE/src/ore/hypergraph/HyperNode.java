package ore.hypergraph;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class HyperNode<T, E> {
	private T data;
	private Hypergraph<T, E> graph;
	private Set<HyperEdge<E, T>> edges;
	private int assignedPartition;
	
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
		if(edges == null) {
			edges = new HashSet<HyperEdge<E, T>>();
		}
		edges.add(edge);
	}
	
	public void print(PrintWriter pw) {
		pw.print(Integer.parseInt(data.toString()) + 1);
	}
	
	public void toDot(PrintWriter pw) {
		pw.println("u" + getData().toString() + " [shape=ellipse];");	
	}
}
