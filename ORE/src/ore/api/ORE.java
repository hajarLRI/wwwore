package ore.api;

import java.io.PrintWriter;
import java.io.Serializable;

import javax.jms.JMSException;

import ore.cluster.ClusterManager;
import ore.event.EventManager;
import ore.exception.NoSuchSubscriber;
import ore.hibernate.Metadata;
import ore.hypergraph.HyperEdge;
import ore.hypergraph.Hypergraph;
import ore.servlet.CookieFilter;
import ore.subscriber.Subscriber;
import ore.subscriber.SubscriberManager;
import ore.util.LogMan;

/**
 * Developers use this class to register for mutation events.
 *
 */
public class ORE {

	private static Hypergraph<Integer, Integer> graph = new Hypergraph();
	public static Hypergraph<Integer, Integer> getGraph() {
		return graph;
	}
	/**
	 * Register for notification of a simple property change event
	 * 
	 * @param	entity	The Hibernate object which has the property 
	 * @param	property	The name of the property as a String
	 * @param	listener	Callback for a property change event
	 * @throws JMSException 
	 */
	public static void addPropertyChangeListener(String userID, Object entity, String property, PropertyChangeListener listener) throws JMSException {
		String sessionID = CookieFilter.getSessionID();
		Subscriber subscriber = null;
		try {
			subscriber = SubscriberManager.getInstance().get(sessionID);
		} catch(NoSuchSubscriber nss) {
			nss.printStackTrace();
		}
		Serializable key = Metadata.getPrimaryKeyValue(entity);
		String className = entity.getClass().getName();
		subscriber.addPropertyChangeListener(userID, className, key.toString(), property, listener);
		
		int edge = key.hashCode();
		int node = Integer.parseInt(subscriber.getID());
		HyperEdge<Integer, Integer> hyperEdge = graph.getEdge(edge);
		graph.putNodeOnEdge(node, hyperEdge);
	}
	
	/**
	 * Register for notification of a many-to-many or one-to-many collection change event (addition and removal)
	 * 
	 * @param	entity	The Hibernate object which has the collection 
	 * @param	property	The name of the property which references the collection, as a String
	 * @param	listener	Callback for the collection change events
	 * @throws JMSException 
	 */
	public static void addCollectionChangeListener(String userID, Object entity, String property, CollectionChangeListener listener) throws JMSException {
		String sessionID = CookieFilter.getSessionID();
		LogMan.info("Session ID is "+sessionID);
		Subscriber subscriber = null;
		try {
			subscriber = SubscriberManager.getInstance().get(sessionID);
		} catch(NoSuchSubscriber nss) {
			nss.printStackTrace();
		}
		Serializable key = Metadata.getPrimaryKeyValue(entity);
		String className = entity.getClass().getName();
		subscriber.addCollectionChangeListener(userID, className, key.toString(), property, listener);
		
		int edge = key.hashCode();
		int node = Integer.parseInt(subscriber.getID());
		HyperEdge<Integer, Integer> hyperEdge = graph.getEdge(edge);
		graph.putNodeOnEdge(node, hyperEdge);
	}
	
	public static void printGraph(PrintWriter pw) {
		graph.print(pw);
	}
	
	public static void reset() {
		EventManager.getInstance().clear();
		ClusterManager.getInstance().clear();
	}
}
