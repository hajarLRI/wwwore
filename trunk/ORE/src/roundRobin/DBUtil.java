package roundRobin;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

public class DBUtil {
	
	public static String[] IPs = {"10.125.3.116", "10.125.1.110"};
	
	private static String getAddress(String ip) {
		return "http://" + ip + ':' + Config.PORT + '/' + Config.PROJECT + "/dbServlet";
	}
	
	public static void main(String[] args) throws HttpException, IOException {
		String addr = getAddress(IPs[1]);
		GetMethod method = makeMethod(addr, "none");
		HttpClient client = createClient();
		client.executeMethod(method);
		method.releaseConnection();
		System.out.println("Executing with status: " + method.getStatusCode());
	}
	
	static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
	static {
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		params.setDefaultMaxConnectionsPerHost(5000);	        
    	params.setMaxTotalConnections(10000);
    	connectionManager.setParams(params); 
	}
	
	private static HttpClient createClient() {
		return new HttpClient(connectionManager);
	}
	
	private static GetMethod makeMethod(String url, String sessionID) {
		GetMethod method_tmp = new GetMethod(url);
		method_tmp.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		method_tmp.setRequestHeader("Cookie", "sessionID=" + sessionID);
		method_tmp.setRequestHeader("Connection", "close");
		//					method_tmp.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
		//							new DefaultHttpMethodRetryHandler());
		return method_tmp;
	}
}
