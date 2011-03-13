package ore.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ore.cluster.ClusterManager;
import ore.jms.Receiver;
import ore.subscriber.Subscriber;
import ore.subscriber.SubscriberManager;
import ore.util.HTTPServletUtil;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

public class ClusterStart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static boolean longPolling = false;
	public static String clientIP = null;
	public static ActiveMQConnectionFactory connectionFactory;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String selfIP = request.getParameter("selfIP");
			String peerIP = request.getParameter("peerIP");
			clientIP = request.getParameter("clientIP");
			if(clientIP != null) {
				boolean done = false;
				while(!done) {
					try {
						connectionFactory = new ActiveMQConnectionFactory("tcp://"+clientIP);
						done = true;
					} catch (Exception e ) {
						System.out.print("Connection is null");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
			String mode = request.getParameter("mode");
			String longPollingString = request.getParameter("longPolling");
			boolean longPoll= Boolean.parseBoolean(longPollingString);
			longPolling = longPoll;
			
			String[] peerIPs = null;
			if(peerIP != null) {
				peerIP = peerIP.replace('~', ':');
				peerIPs = peerIP.split("_");
			}
			if(selfIP != null) {
				selfIP = selfIP.replace('~', ':');
			}
			ClusterManager.init(selfIP, peerIPs, mode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
