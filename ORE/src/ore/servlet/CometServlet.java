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
import org.json.JSONObject;
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
				JSONObject digest = new JSONObject(new JSONTokener(reader));
				String swapString = digest.getString("swap");
				boolean swap = false;
				if((swapString != null) && (swapString.equals("true"))) {
					swap = true;
				}
				String sessionID = SubscriberManager.getInstance().newSubscriber(digest, swap);
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
				subscriber.redirect(request.getParameter("ip"), request.getParameter("port"), false);
			} else {
				String redirectString = request.getParameter("redirect");
				if(redirectString == null) {
					redirectString = "no";
				}
				subscriber.pickup(c, redirectString);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(500);
		}
	}

}
