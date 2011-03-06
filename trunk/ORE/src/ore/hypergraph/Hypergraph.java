package ore.hypergraph;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ore.client.Config;
import ore.client.HMetis;
import ore.client.RandomGenerator;
import ore.client.User;

public class Hypergraph<N, E> {
	private Map<N, HyperNode<N, E>> nodes = new HashMap<N, HyperNode<N, E>>();
	private Map<E, HyperEdge<E, N>> edges =  new HashMap<E, HyperEdge<E, N>>();
	private Map<Integer, Partition<N, E>> parts = new HashMap<Integer, Partition<N, E>>();

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
		HyperNode<N, E> node = this.createNode(userID);
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
		HyperNode<N, E> node = nodes.get(nodeData);
		node.setPart(part);
		Partition partition = parts.get(part);
		if(partition == null) {
			partition = new Partition(part);
			parts.put(part, partition);
		}
		partition.add(node);
	}
	
	private HyperNode<N, E> createNode(N nodeData) {
		HyperNode<N, E> node = nodes.get(nodeData);
		if(node == null) {
			node = new HyperNode(nodeData, this);
			nodes.put(nodeData, node);
		}
		return node;
	}
	
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
	
	public HyperNode<N, E> getNode(N data) {
		HyperNode<N, E> node = nodes.get(data);
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
	
	public static void main(String[] args) throws Exception {
		RandomGenerator rg = new RandomGenerator();
		List<User<Integer>> user = rg.generate(Config.readers, Config.itemsPerUser, Config.overlap);
		Hypergraph hg = User.makeHyperGraph(user);
		HMetis.shmetis(hg, 7, 5);
		PrintWriter pw = new PrintWriter(new FileOutputStream("C:/Temp/dot.out"));
		hg.toDot(pw);
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
}
