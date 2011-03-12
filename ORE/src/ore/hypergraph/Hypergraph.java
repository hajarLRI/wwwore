package ore.hypergraph;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import ore.client.HMetis;
import ore.client.User;
import ore.client.generators.RandomGenerator;
import ore.client.generators.WorkloadGenerator;
import ore.jung.JungUtil;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class Hypergraph<N, E> implements WorkloadGenerator<E>{
	private Map<N, HyperNode<N, E>> nodes = new HashMap<N, HyperNode<N, E>>();
	private Map<E, HyperEdge<E, N>> edges =  new HashMap<E, HyperEdge<E, N>>();
	private Map<Integer, Partition<N, E>> parts = new HashMap<Integer, Partition<N, E>>();
	
	public static Hypergraph<Integer, Integer> generatePowerLaw(int numVertices, int numEdges, int r) {
		Graph<Integer, Integer> graph = JungUtil.generatePowerLaw(numVertices, numEdges, r);
		return fromJUNG(graph);
	}
	
	public static Hypergraph<Integer, Integer> generateKleinberg(int latticeSize, double clusteringExponent) {
		Graph<Integer, Integer> graph = JungUtil.generateKleinberg(latticeSize, clusteringExponent);
		System.out.println("Foo");
		Hypergraph<Integer, Integer> g = fromJUNG(graph);
		System.out.println("Bar");
		return g;
	}
	
	public static <N> Hypergraph<N, N> fromJUNG(edu.uci.ics.jung.graph.Graph<N, N> jung) {
		Hypergraph<N, N> graph = new Hypergraph<N, N>();
		SortedSet<N> sorted = new TreeSet();
		for(N nodeData : jung.getVertices()) {
			sorted.add(nodeData);
		}
		for(N nodeData : sorted) {
			HyperEdge<N, N> myEdge = graph.getEdge(nodeData);
			graph.putNodeOnEdge(nodeData, myEdge);
		}
		for(N edgeData : jung.getEdges()) {
			Pair<N> nodes = jung.getEndpoints(edgeData);
			N fstNodeData = nodes.getFirst();
			N sndNodeData = nodes.getSecond();
			HyperEdge<N, N> fstEdge = graph.getEdge(fstNodeData);
			HyperEdge<N, N> sndEdge = graph.getEdge(sndNodeData);
			graph.putNodeOnEdge(fstNodeData, sndEdge);
			graph.putNodeOnEdge(sndNodeData, fstEdge);
		}
		return graph;
	}

	public void removeNode(N data) {
		HyperNode<N, E> node = nodes.remove(data);
		for(Partition<N, E> part : parts.values()) {
			part.removeNode(node);
		}
		for(HyperEdge<E, N> edge : edges.values()) {
			edge.removeNode(node);
		}
	}
	
	public N addUserToPartition(N userID, User<E> user, int partNum) {
		Partition<N, E> part = parts.get(partNum);
		HyperNode<N, E> node = getNode(userID);
		for(E edgeData : user.getInterests()) {
			HyperEdge<E, N> edge = this.getEdge(edgeData);
			this.putNodeOnEdge(node.getData(), edge);
		}
		part.add(node);
		return userID;
	}
	
	public N bestNodeForPartitionFromPartition(int toPartition, int fromPartition) {
		Partition<N, E> toPart = parts.get(toPartition);
		Partition<N, E> fromPart = parts.get(fromPartition);
		int max = Integer.MIN_VALUE;
		N bestNode = null;
		for(HyperNode<N, E> i : fromPart.getNodes()) {
			int size = relationWeight(i, toPart);
			if(size > max) {
				max = size;
				bestNode = i.getData();
			}
		}
		return bestNode;
	}
	
	public int findMostRelatedPartition(N nodeData) {
		HyperNode<N, E> node = this.getNode(nodeData);
		int max = Integer.MIN_VALUE;
		int partition = -1;
		for(Map.Entry<Integer, Partition<N, E>> part : parts.entrySet()) {
			int weight = relationWeight(node, part.getValue());
			if(weight > max) {
				max = weight;
				partition = part.getKey();
			}
		}
		return partition;
	}
	
	private int relationWeight(HyperNode<N, E> node, Partition<N, E> p) {
		int weight = 0;
		for(HyperNode<N, E> other : p.getNodes()) {
			weight += node.relationWeight(other);
		}
		return weight;
	}
	
	public int getLeastLoadedPartition() {
		int min = Integer.MAX_VALUE;
		int partitionNumber = -1;
		for(Map.Entry<Integer, Partition<N, E>> part : parts.entrySet()) {
			Partition p = part.getValue();
			int size = p.getSize();
			if(size < min) {
				min = size;
				partitionNumber = part.getKey();
			}
		}
		return partitionNumber;
	}
	
	public int getNumOfEdges() {
		return edges.size();
	}
	
	public int getNumOfNodes() {
		return nodes.size();
	}
	
	public void setPartition(N nodeData, int part) {
		HyperNode<N, E> node = getNode(nodeData);
		node.setPart(part);
		Partition partition = parts.get(part);
		if(partition == null) {
			partition = new Partition(part);
			parts.put(part, partition);
		}
		partition.add(node);
	}
	
	public void putNodeOnEdge(N nodeData, HyperEdge<E, N> edge) {
		HyperNode<N, E> node = getNode(nodeData);
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
	
	public HyperNode<N, E> getNode(N data) {
		HyperNode<N, E> node = nodes.get(data);
		if(node == null) {
			node = new HyperNode<N, E>(data, this);
			nodes.put(data, node);
		}
		return node;
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
	
	public void toDot(PrintWriter pw) {
		pw.println("graph G {");
		pw.println("overlap = false;");
		for(HyperEdge<E, N> edge : edges.values()) {
			pw.println("o" + edge.getData().toString() + " [shape=box];");	
		}
		for(Partition p : parts.values()) {
			p.toDot(pw);
		}
		//for(HyperNode<N, E> node : nodes.values()) {
		//	pw.println("u" + node.getData().toString() + " [shape=ellipse];");	
		//}
		for(HyperEdge<E, N> edge : edges.values()) {
			edge.toDot(pw);
		}
		
		pw.println("}");
	}
	
	public void toCytoscape(PrintWriter pw) {
		pw.println("<?xml version='1.0'?><graph id='graph' label='graph' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:ns1='http://www.w3.org/1999/xlink' xmlns:dc='http://purl.org/dc/elements/1.1/' xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' xmlns='http://www.cs.rpi.edu/XGMML'>");
		for(HyperEdge<E, N> edge : edges.values()) {
			pw.println("<node id='o" + edge.getData().toString() + "' label='o" + edge.getData().toString() + "'/>");	
		}
		for(Partition p : parts.values()) {
			p.toCytoscape(pw);
		}
		//for(HyperNode<N, E> node : nodes.values()) {
		//	pw.println("u" + node.getData().toString() + " [shape=ellipse];");	
		//}
		for(HyperEdge<E, N> edge : edges.values()) {
			edge.toCytoscape(pw);
		}
		
		pw.println("</graph>");
	}
	
	public static void main(String[] args) throws Exception {
		Hypergraph hg = Hypergraph.generateKleinberg(20, 24);
		System.out.println(hg.getNumOfNodes());
		System.out.println(hg.getNumOfEdges());
		HMetis.shmetis(hg, 5, 5);
		PrintWriter pw = new PrintWriter(new FileOutputStream("C:/Temp/dot.xgmml"));
		hg.toCytoscape(pw);
		pw.close();
	}

	public void moveNode(N userNumber, int mostRelated) {
		HyperNode<N, E> node = nodes.get(userNumber);
		for(Partition<N, E> part : parts.values()) {
			part.removeNode(node);
		}
		Partition partition = parts.get(mostRelated);
		partition.add(node);
	}

	@Override
	public List<User<E>> generate() {
		List<User<E>> list = new LinkedList<User<E>>();
		for(HyperNode<N, E> node : nodes.values()) {
			list.add(node.generateUser());
		}
		Collections.sort(list);
		return list;
	}
}
