package ore.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ore.subscriber.Subscriber;
import ore.subscriber.SubscriberManager;
import ore.util.HTTPServletUtil;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

/**
 * CometServlet implements long-polling used to notify clients of events. 
 * Clients are identified by a sessionID that is saved as a cookie. The sessionID
 * maps clients to a {@link ore.subscriber.Subscriber} object that is managed
 * by {@link ore.subscriber.SubscriberManager}
 */
public class CometServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = HTTPServletUtil.getCookie("sessionID", request);
		if((id == null) || (id.equals("none"))) {
			String sessionID = SubscriberManager.getInstance().newSubscriber();
			response.setHeader("Set-Cookie", "sessionID=" + sessionID);
			response.setStatus(200);
			return;
		}
		Continuation c = ContinuationSupport.getContinuation(request);
		c.setTimeout(10*60*1000);
		Subscriber subscriber = SubscriberManager.getInstance().get(id);
		try {
			subscriber.pickup(c);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
