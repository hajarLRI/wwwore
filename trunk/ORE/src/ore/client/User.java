package ore.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ore.hypergraph.HyperEdge;
import ore.util.MathUtil;

public class User<T> implements Serializable, Comparable<User<T>> {
	private static int idCounter = 0;
	private int id;
	private int partition;
	
	public static User<Integer> newRandomUser(int itemsPerUser) {
		User<Integer> user = new User<Integer>();
		int items = MathUtil.randomInt(1, itemsPerUser + 1);
		for(int j=0; j < items; j++) {
			Integer interest = MathUtil.randomInt(0, Config.num - 1);
			user.addInterest(interest);
		}
		return user;
	}
	
	public int getPartition() {
		return partition;
	}

	public void setPartition(int partition) {
		this.partition = partition;
	}

	public User(int id) {
		this.id = id;
	}
	
	public User() {
		synchronized(User.class) {
			this.id = (idCounter++);
		}
	}
	
	public int getID() {
		return id;
	}
	
	private List<T> interests = new LinkedList<T>();
	
	public void addInterest(T interest) {
		interests.add(interest);
	}
	
	public List<T> getInterests() {
		return interests;
	}
	
	public static ore.hypergraph.Hypergraph<Integer, Integer> makeHyperGraph(List<User<Integer>> users) {
		Map<Integer, Set<Integer>> edges = new HashMap<Integer, Set<Integer>>();
		int i = 0;
		for(User<Integer> user : users) {
			for(Integer edge : user.getInterests()) {
				Set<Integer> nodes = edges.get(edge);
				if(nodes == null) {
					nodes = new HashSet<Integer>();
					edges.put(edge, nodes);
				}
				nodes.add(i);
			}
			i++;
		}
		ore.hypergraph.Hypergraph<Integer, Integer> graph = new ore.hypergraph.Hypergraph();
		for(Map.Entry<Integer, Set<Integer>> edge : edges.entrySet()) {
			int edgeData = edge.getKey();
			HyperEdge hyperEdge = graph.getEdge(edgeData);
			for(Integer node : edge.getValue()) {
				graph.putNodeOnEdge(node, hyperEdge);
			}
		}
		return graph;
	}

	@Override
	public int compareTo(User<T> other) {
		if(this.id < other.id) {
			return -1;
		} else if(this.id == other.id) {
			return 0;
		} else {
			return 1;
		}
	}
	
}
