package ore.hypergraph;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Hypergraph<N, E> {
	private Map<N, HyperNode<N, E>> nodes = new HashMap<N, HyperNode<N, E>>();
	private Map<E, HyperEdge<E, N>> edges =  new HashMap<E, HyperEdge<E, N>>();
	
	public void putNodeOnEdge(N nodeData, HyperEdge<E, N> edge) {
		HyperNode<N, E> node = nodes.get(nodeData);
		if(node == null) {
			node = new HyperNode(nodeData, this);
			nodes.put(nodeData, node);
		}
		edge.addNode(node);
		node.addEdge(edge);
	}
	
	public HyperEdge<E, N> getEdge(E data) {
		HyperEdge<E, N> edge = edges.get(data);
		if(edge == null) {
			edge = new HyperEdge(data, this);
			edges.put(data, edge);
		}
		return edge;
	}
	
	public void print(PrintWriter pw) {
		int numOfVertices = nodes.size();
		int numOfEdges = edges.size();
		pw.println(numOfEdges + " " + numOfVertices);
		for(HyperEdge<E, N> edge : edges.values()) {
			edge.print(pw);
			pw.println();
		}
	}
}
