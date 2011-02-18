package roundRobin;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class User<T> {
	private static int idCounter = 0;
	private String id;
	
	public User() {
		synchronized(User.class) {
			this.id = (idCounter++)+"";
		}
	}
	
	public String getID() {
		return id;
	}
	
	private List<T> interests = new LinkedList<T>();
	
	public void addInterest(T interest) {
		interests.add(interest);
	}
	
	public List<T> getInterests() {
		return interests;
	}
	
	public static void printHyperGraph(List<User<Integer>> users, PrintWriter pw) {
		int numOfVertices = users.size();
		Map<Integer, Set<Integer>> edges = new HashMap<Integer, Set<Integer>>();
		int i = 1;
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
		int numOfEdges = edges.size();
		pw.println(numOfEdges + " " + numOfVertices);
		for(Map.Entry<Integer, Set<Integer>> edge : edges.entrySet()) {
			for(Integer node : edge.getValue()) {
				pw.print(node + " ");
			}
			pw.println();
		}
	}
	
}
