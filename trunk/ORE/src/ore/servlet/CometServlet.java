package ore.servlet;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ore.subscriber.Subscriber;
import ore.subscriber.SubscriberManager;
import ore.util.HTTPServletUtil;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.json.JSONArray;
import org.json.JSONTokener;

/**
 * CometServlet implements long-polling used to notify clients of events. 
 * Clients are identified by a sessionID that is saved as a cookie. The sessionID
 * maps clients to a {@link ore.subscriber.Subscriber} object that is managed
 * by {@link ore.subscriber.SubscriberManager}
 */
public class CometServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String operation = request.getParameter("operation");

			if((operation != null) && operation.equals("directed")) {
				InputStreamReader reader = new InputStreamReader(request.getInputStream());
				JSONArray digest = new JSONArray(new JSONTokener(reader));
				String sessionID = SubscriberManager.getInstance().newSubscriber(digest);
				response.setHeader("Set-Cookie", "sessionID=" + sessionID);
				response.setStatus(200);
				return;
			}
		} catch(Exception e) {
			e.printStackTrace();
			response.setStatus(500);
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String id = HTTPServletUtil.getCookie("sessionID", request);

			String operation = request.getParameter("operation");
			
			if((id == null) || (id.equals("none"))) {
				String sessionID = SubscriberManager.getInstance().newSubscriber();
				response.setHeader("Set-Cookie", "sessionID=" + sessionID);
				response.setStatus(200);
				return;
			}
			Subscriber subscriber = null;

			subscriber = SubscriberManager.getInstance().get(id);
			Continuation c = ContinuationSupport.getContinuation(request);
			c.setTimeout(10*60*1000);

			if((operation != null) && (operation.equals("stop"))) {
				subscriber.stop();
			} else if((operation != null) && (operation.equals("redirect"))) {
				subscriber.redirect(request.getParameter("ip"), request.getParameter("port"));
			} else {
				String redirectString = request.getParameter("redirectOK");
				boolean redirectOK = (!redirectString.equals("false"));
				subscriber.pickup(c, redirectOK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(500);
		}
	}

}
