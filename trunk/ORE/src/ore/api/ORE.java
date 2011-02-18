package ore.api;

import java.io.PrintWriter;

import javax.jms.JMSException;

import ore.hibernate.Metadata;
import ore.hypergraph.HyperEdge;
import ore.hypergraph.Hypergraph;
import ore.servlet.CookieFilter;
import ore.subscriber.Subscriber;
import ore.subscriber.SubscriberManager;

/**
 * Developers use this class to register for mutation events.
 *
 */
public class ORE {

	private static Hypergraph<Integer, Integer> graph = new Hypergraph();
	/**
	 * Register for notification of a simple property change event
	 * 
	 * @param	entity	The Hibernate object which has the property 
	 * @param	property	The name of the property as a String
	 * @param	listener	Callback for a property change event
	 * @throws JMSException 
	 */
	public static void addPropertyChangeListener(Object entity, String property, PropertyChangeListener listener) throws JMSException {
		String sessionID = CookieFilter.getSessionID();
		Subscriber subscriber = SubscriberManager.getInstance().get(sessionID);
		subscriber.addPropertyChangeListener(entity, property, listener);
		
		int edge = Metadata.getPrimaryKeyValue(entity).hashCode();
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
	public static void addCollectionChangeListener(Object entity, String property, CollectionChangeListener listener) throws JMSException {
		String sessionID = CookieFilter.getSessionID();
		System.out.println("Session ID is "+sessionID);
		Subscriber subscriber = SubscriberManager.getInstance().get(sessionID);
		subscriber.addCollectionChangeListener(entity, property, listener);
		
		int edge = Metadata.getPrimaryKeyValue(entity).hashCode();
		int node = Integer.parseInt(subscriber.getID());
		HyperEdge<Integer, Integer> hyperEdge = graph.getEdge(edge);
		graph.putNodeOnEdge(node, hyperEdge);
	}
	
	public static void printGraph(PrintWriter pw) {
		graph.print(pw);
	}
	
}
