package ore.subscriber;
import java.io.PrintWriter;

/**
 * Listeners implement this interface so events can be buffered when a Subscriber
 * does have a current CometConnection. When the Comet connection becomes available
 * the events are flushed from the buffer. 
 */
interface Flushable {
	public abstract boolean flushEvents(PrintWriter pw) throws Exception;
}
