package ore.hypergraph;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class HyperEdge<T, N> {
	private T data;
	private Hypergraph<N, T> graph;
	private Set<HyperNode<N, T>> nodes;
	
	public void removeNode(HyperNode<N, T> node) {
		nodes.remove(node);
	}
	
	public T getData() {
		return data;
	}
	
	HyperEdge(T data, Hypergraph graph) {
		this.graph = graph;
		this.data = data;
	}
	
	public void addNode(HyperNode<N, T> node) {
		if(nodes == null) {
			nodes = new HashSet<HyperNode<N, T>>();
		}
		nodes.add(node);
	}

	public void print(PrintWriter pw) {
		if(nodes == null) {
			return;
		}
		for(HyperNode<N, T> node : nodes) {
			node.print(pw);
			pw.print(' ');
		}
	}
	
	public void toDot(PrintWriter pw) {
		String edgeName = 'o' + getData().toString();
		for(HyperNode<N, T> node : nodes) {
			pw.println(edgeName + " -- u" + node.getData().toString() + ';');
		}
	}
	
	public void toCytoscape(PrintWriter pw) {
		String edgeName = 'o' + getData().toString();
		for(HyperNode<N, T> node : nodes) {
			pw.println("<edge id='e" + edgeName + "' label='e" + edgeName + "' target='" + edgeName + "' source='u" + node.getData().toString() + "'/>");
		}
	}
}
