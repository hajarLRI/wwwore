package ore.client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.json.JSONArray;
import org.json.JSONTokener;

public class Machine implements Runnable {

	public static List<Machine> machines = new LinkedList<Machine>();
	
	public static Machine getMachine(String ip, String port) {
		for(Machine m : machines) {
			if(m.ip.equals(ip) && m.port.equals(port)) {
				return m;
			}
		}
		return null;
	}
	
	private static ThreadLocal<HttpClient> current = new ThreadLocal<HttpClient>();
	
	public static void createMachines(String[] ips, String[] ports, String[] jmsPorts) {
		for(int i=0; i < ips.length; i++) {
			Machine m = new Machine(ips[i], ports[i], jmsPorts[i]);
			machines.add(m);
		}
	}
	
	private String ip;
	private String port;
	private String jmsPort;
	
	public Machine(String ip, String port, String jmsPort) {
		this.ip = ip;
		this.port = port;
		this.jmsPort = jmsPort;
	}
	
	public void start() {
		GetMethod method = null;
		if(Config.IPs.length > 1) {
			String peerIP = "";
			int num = 1;
			for(Machine m : machines) {
				if(m != this) {
					peerIP += m.ip + '~' + m.jmsPort;
				}
				if((num <= (Config.IPs.length-1))&&(m != this)) {
					peerIP += '_';
				}
				num++;
			}
			method = makeMethod(getUrlPrefix() + "/clusterStart", "none", "selfIP", ip + '~' + jmsPort, "peerIP", peerIP);
			try {
				System.out.println(method.getURI().toString());
			} catch (URIException e) {
				e.printStackTrace();
			}
		} else {
			method = makeMethod(getUrlPrefix() + "/clusterStart", "none");
		}
		
		try {
			HttpClient client = createClient();
			System.out.println("Starting: " + this.getUrlPrefix());
			client.executeMethod(method);
		} catch (Exception e) {
			e.printStackTrace();
		}
		method.releaseConnection();
	}
	
	public void redirect(Machine other) throws Exception {
		GetMethod method = makeMethod(getUrlPrefix() + "/swap", "none", "ip", other.ip, "port", other.port);
		HttpClient client = createClient();
		client.executeMethod(method);
		method.releaseConnection();
	}
	
	public void join(User user, String sessionID) throws Exception {
		HttpClient client = getCurrent();
		int size = user.getInterests().size();
		int i = 0;
		String roomName = "";
		for(Object interest : user.getInterests()) {
			roomName += interest.toString();
			if(i != (size-1)) {
				roomName += '_';
			}
			i++;
		}
		GetMethod method_tmp = makeMethod(getUrlPrefix() + "/chat", sessionID, "operation", "join", "userName", user.getID(), "roomName", roomName, "login", true);
		client.executeMethod(method_tmp);
		InputStream stream = method_tmp.getResponseBodyAsStream();
		char[] x = new char[1024];
		//System.out.print("Got msg: ");
		Reader r = new InputStreamReader(stream);
		while (r.read(x) != -1) {
			/*String msg = new String(x);
				String insertTime = msg.substring(msg.indexOf("hello_")+6);
				System.out.print(msg+"    "+insertTime);*/
			//System.out.print(new String(x));
		}

		method_tmp.releaseConnection();
		client.getHttpConnectionManager().closeIdleConnections(0);
	}
	
	public String connect() throws Exception {
		HttpClient client = createClient();
		GetMethod method_tmp = makeMethod(this.getUrlPrefix() + "/connect", "none");
		client.executeMethod(method_tmp);
		Header header = method_tmp.getResponseHeader("Set-Cookie");
		String sessionStr = header.getValue();
		String sessionID = sessionStr.split("=")[1];
		method_tmp.releaseConnection();
		client.getHttpConnectionManager().closeIdleConnections(0);
		return sessionID;
	}
	
	public void stopMe(String sessionID) throws Exception {
		HttpClient client = createClient();
		GetMethod method_tmp = makeMethod(this.getUrlPrefix() + "/connect", sessionID, "operation", "stop");
		client.executeMethod(method_tmp);
		method_tmp.releaseConnection();
		client.getHttpConnectionManager().closeIdleConnections(0);
	}
	
	public JSONArray receiveMessages(String sessionID) throws Exception {
		HttpClient client = getCurrent();
		GetMethod method_tmp = makeMethod(getUrlPrefix() + "/connect", sessionID);
		client.executeMethod(method_tmp);
		//InputStream stream = method_tmp.getResponseBodyAsStream();
		String stream = method_tmp.getResponseBodyAsString();
		if(method_tmp.getStatusCode() == 200) { 
			Reader r2 = new InputStreamReader(new ByteArrayInputStream(stream.getBytes()));
			try {
				JSONArray arr = new JSONArray(new JSONTokener(r2));
				//TODO Is the stream ready to release?
				method_tmp.releaseConnection();
				client.getHttpConnectionManager().closeIdleConnections(0);
				return arr;
			} catch(Exception e) {
				System.err.println("Bad message format: " + stream);
			}
		} 
		return null;
	}
	
	public String getUrlPrefix() {
		return "http://" + ip + ':' + port + '/' + Config.PROJECT;
	}
	
	public static HttpClient getCurrent() {
		HttpClient client = current.get();
		if(client == null) {
			client = createClient();
			current.set(client);
		}
		return client;
	}
	
	static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
	static {
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		params.setDefaultMaxConnectionsPerHost(5000);	        
    	params.setMaxTotalConnections(10000);
    	connectionManager.setParams(params); 
	}
	
	static HttpClient createClient() {
		return new HttpClient(connectionManager);
	}
	
	static GetMethod makeMethod(String url, String sessionID, Object ... parms) {
		if((parms != null) && (parms.length > 0)) {
			url += '?';
		}
		for(int i=0; i < parms.length; i += 2) {
			url += parms[i];
			url += '=';
			url += parms[i+1].toString();
			if(i != (parms.length-2)) {
				url += '&';
			}
		}
		GetMethod method_tmp = new GetMethod(url);
		method_tmp.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		method_tmp.setRequestHeader("Cookie", "sessionID=" + sessionID);
		method_tmp.setRequestHeader("Connection", "close");
		//					method_tmp.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
		//							new DefaultHttpMethodRetryHandler());
		return method_tmp;
	}

	@Override
	public void run() {
		start();
	}
}
