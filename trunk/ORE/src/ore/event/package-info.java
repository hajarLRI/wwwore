/** This package maintains subscriptions for events and routes events to
 * subscribers. The only public class in this package is {@link EventManager}. 
 * The {@link ore.subscriber.Subscriber} class uses EventManger to add subscriptions
 * and the {@link ore.hibernate.HibernateListener} notifies the EventManager when
 * mutations occur.
 * 
 */
package ore.event;