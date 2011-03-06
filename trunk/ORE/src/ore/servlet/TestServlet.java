package ore.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ore.subscriber.Subscriber;
import ore.subscriber.SubscriberManager;
import ore.util.HTTPServletUtil;
import ore.util.LogMan;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

/**
 * CometServlet implements long-polling used to notify clients of events. 
 * Clients are identified by a sessionID that is saved as a cookie. The sessionID
 * maps clients to a {@link ore.subscriber.Subscriber} object that is managed
 * by {@link ore.subscriber.SubscriberManager}
 */
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Queue<Continuation> q = new ConcurrentLinkedQueue<Continuation>();
	
	@Override
	public void init() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for(Continuation c : q) {
						if(c.isSuspended()) { 
							try {
								PrintWriter pw = c.getServletResponse().getWriter();
								pw.println("HELLO WORLD");
								pw.flush();
							} catch(Exception e) {
								e.printStackTrace();
							}
						} 
					}
				}
			}
		});
		t.start();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Continuation c = ContinuationSupport.getContinuation(request);
			c.setTimeout(10*60*1000);
			c.suspend();
			q.add(c);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(500);
		}
	}

}
