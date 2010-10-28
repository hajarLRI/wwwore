package clientcopy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class Pushing {

	public static int numOfInterests = 20;
	public static int numOfRooms = 4;
	public static long roomTime = 30000;
	//	public static String IP = "198.162.54.73";
	//	public static String PORT = "9080";
	public static String IP = "10.220.194.144";
	//	public static String IP = "127.0.0.1";
	public static String PORT = "8080";
	public static String PROJECT = "ORE";//"ORE";
	//	public static String PROJECT = "ORE-addPush";
	//	 public static String PROJECT = "ORE-trigger";
	public static int modCount = 40;
	public static int sleepTime = 500;
	public static int responses = 0;
	private static long backoff = 2000;

	public static void main(String[] args) throws Exception {
		//initHibernate();
		for (int i = 1; i <= modCount; i++) {
			Thread thread = new Thread(new Running("" + (i), "" + i));
			Thread.sleep(sleepTime);
			thread.start();
		}
	}

	public static void initHibernate() {
		try {
			//			MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
			//			HttpClient client = new HttpClient(connectionManager);
			HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true));
			GetMethod method_tmp = new GetMethod("http://" + Pushing.IP + ":"
					+ Pushing.PORT + "/" + Pushing.PROJECT
					+ "/servlet/InitDBServlet");
			client.executeMethod(method_tmp);
			method_tmp.setRequestHeader("Connection", "close");
			method_tmp.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler());
			method_tmp.releaseConnection();
		} catch (HttpException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	static class Running implements Runnable {
		private String itemId;
		private String id;
		private int[] interests = new int[numOfInterests];
		private int interestIndex = 0;

		public Running(String itemId, String id) {
			for(int i=0;i < numOfInterests;i++) {
				interests[i] = (int) Math.floor(Math.random() * numOfRooms);
			}
			this.itemId = itemId;
			this.id = id;
		}

		public void run() {
			String sessionID = null;
			// Step 1
			try {

				//					MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
				//					HttpClient client = new HttpClient(connectionManager);
				HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true));
				GetMethod method_tmp = new GetMethod("http://" + Pushing.IP
						+ ":" + Pushing.PORT + "/" + Pushing.PROJECT
						+ "/connect");
				method_tmp.getParams().setCookiePolicy(
						CookiePolicy.IGNORE_COOKIES);
				method_tmp.setRequestHeader("Cookie", "sessionID=none");
				method_tmp.setRequestHeader("Connection", "close");
				//					method_tmp.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				//							new DefaultHttpMethodRetryHandler());
				client.executeMethod(method_tmp);
				Header header = method_tmp.getResponseHeader("Set-Cookie");
				String sessionStr = header.getValue();
				sessionID = sessionStr.split("=")[1];
				method_tmp.releaseConnection();

			} catch (HttpException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			if (sessionID == null)
				return;
			while(true) {
				int currentInterest = (interestIndex++ % numOfInterests);
				// Step 2
				try {
					//				MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
					//				HttpClient client = new HttpClient(connectionManager);
					HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true));
					GetMethod method_tmp = new GetMethod(
							"http://"
							+ Pushing.IP
							+ ":"
							+ Pushing.PORT
							+ "/"
							+ Pushing.PROJECT
							+ "/chat?operation=join&userName="+sessionID+"&roomName=" + interests[currentInterest] + "&login=true");
					method_tmp.getParams().setCookiePolicy(
							CookiePolicy.IGNORE_COOKIES);
					method_tmp.setRequestHeader("Cookie", "sessionID=" + sessionID);
					method_tmp.setRequestHeader("Connection", "close");
					//				method_tmp.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					//						new DefaultHttpMethodRetryHandler());
					client.executeMethod(method_tmp);
					InputStream stream = method_tmp.getResponseBodyAsStream();
					char[] x = new char[1024];
					System.out.print("Got msg: ");
					Reader r = new InputStreamReader(stream);
					while (r.read(x) != -1) {
						System.out.print(new String(x));
					}

					method_tmp.releaseConnection();
				} catch (HttpException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				long roomStart = System.currentTimeMillis();
				// Step 3
				while ((System.currentTimeMillis()-roomStart) < roomTime) {
					try {
						HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true));
						GetMethod method_tmp = new GetMethod("http://" + Pushing.IP
								+ ":" + Pushing.PORT + "/" + Pushing.PROJECT
								+ "/connect");
						method_tmp.getParams().setCookiePolicy(
								CookiePolicy.IGNORE_COOKIES);
						method_tmp.setRequestHeader("Connection", "close");
						method_tmp.setRequestHeader("Cookie", "sessionID="
								+ sessionID);
						//					method_tmp.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
						//							new DefaultHttpMethodRetryHandler());
						client.executeMethod(method_tmp);
						InputStream stream = method_tmp.getResponseBodyAsStream();
						Reader r2 = new InputStreamReader(stream);
						char[] x = new char[1024];
						while(r2.read(x) != -1) {
							System.out.print(new String(x));
						}
						System.out.println("");
						method_tmp.releaseConnection();
						synchronized (Running.class) {
							// System.out.println("response");
							Pushing.responses++;
							Date endTime = new Date();
							System.out.println(" Number of successful Writer is : " + Pushing.responses + "  ----- endTime: " + endTime.toLocaleString());
						}
						try {
							Thread.sleep(backoff);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} catch (HttpException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}

				try {
					//				MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
					//				HttpClient client = new HttpClient(connectionManager);
					HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true));
					GetMethod method_tmp = new GetMethod(
							"http://"
							+ Pushing.IP
							+ ":"
							+ Pushing.PORT
							+ "/"
							+ Pushing.PROJECT
							+ "/chat?operation=leave&userName="+sessionID+"&roomName=" + interests[currentInterest]);
					method_tmp.getParams().setCookiePolicy(
							CookiePolicy.IGNORE_COOKIES);
					method_tmp.setRequestHeader("Cookie", "sessionID=" + sessionID);
					method_tmp.setRequestHeader("Connection", "close");
					//				method_tmp.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					//						new DefaultHttpMethodRetryHandler());
					client.executeMethod(method_tmp);
					InputStream stream = method_tmp.getResponseBodyAsStream();
					char[] x = new char[1024];
					System.out.print("Got msg: ");
					Reader r = new InputStreamReader(stream);
					while (r.read(x) != -1) {
						System.out.print(new String(x));
					}

					method_tmp.releaseConnection();
				} catch (HttpException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		}
	}
}
