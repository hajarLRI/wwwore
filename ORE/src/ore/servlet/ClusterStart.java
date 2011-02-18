package ore.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ore.cluster.ClusterManager;
import ore.subscriber.Subscriber;
import ore.subscriber.SubscriberManager;
import ore.util.HTTPServletUtil;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

public class ClusterStart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String selfIP = request.getParameter("selfIP");
			String peerIP = request.getParameter("peerIP");
			String[] peerIPs = null;
			if(peerIP != null) {
				peerIP = peerIP.replace('~', ':');
				peerIPs = peerIP.split("_");
			}
			if(selfIP != null) {
				selfIP.replace('~', ':');
			}
			ClusterManager.init(selfIP, peerIPs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
