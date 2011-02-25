package ore.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ore.api.ORE;
import ore.cluster.ClusterManager;
import ore.subscriber.Subscriber;
import ore.subscriber.SubscriberManager;
import ore.util.HTTPServletUtil;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

public class SwapServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String ip = request.getParameter("ip");
			String port = request.getParameter("port");
			ORE.reset();
		//	SubscriberManager.getInstance().redirectAll(ip, port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
