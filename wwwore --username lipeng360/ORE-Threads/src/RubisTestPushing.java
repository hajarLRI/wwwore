import java.io.IOException;
import java.io.InputStream;

import javax.swing.text.SimpleAttributeSet;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class RubisTestPushing {

//	public static String IP = "198.162.54.73";
//	public static String PORT = "9080";
//	public static String IP = "10.195.186.207";
	public static String IP = "10.212.75.112";
//	public static String IP = "127.0.0.1";
	public static String PORT = "8080";
	public static String PROJECT = "rubisTest";
	public static int modCount = 1000;
	public static int sleepTime = 5;
	public static int responses = 0;
	private static long backoff = 2000;

	public static void main(String[] args) throws Exception {
		initHibernate();
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
			GetMethod method_tmp = new GetMethod("http://" + RubisTestPushing.IP + ":"
					+ RubisTestPushing.PORT + "/" + RubisTestPushing.PROJECT
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

		public Running(String itemId, String id) {
			this.itemId = itemId;
			this.id = id;
		}

		public void run() {
			String sessionID = null;
			// Step 1
			try {
				synchronized (Running.class) {
//					MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
//					HttpClient client = new HttpClient(connectionManager);
					HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true));
					GetMethod method_tmp = new GetMethod("http://" + RubisTestPushing.IP
							+ ":" + RubisTestPushing.PORT + "/" + RubisTestPushing.PROJECT
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
				}
			} catch (HttpException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			if (sessionID == null)
				return;

			// Step 2
			try {
//				MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
//				HttpClient client = new HttpClient(connectionManager);
				HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true));
				GetMethod method_tmp = new GetMethod(
						"http://"
								+ RubisTestPushing.IP
								+ ":"
								+ RubisTestPushing.PORT
								+ "/"
								+ RubisTestPushing.PROJECT
								+ "/test?operation=viewItems&itemID="
								+ itemId);
				method_tmp.getParams().setCookiePolicy(
						CookiePolicy.IGNORE_COOKIES);
				method_tmp.setRequestHeader("Cookie", "sessionID=" + sessionID);
				method_tmp.setRequestHeader("Connection", "close");
//				method_tmp.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
//						new DefaultHttpMethodRetryHandler());
				client.executeMethod(method_tmp);
				InputStream stream = method_tmp.getResponseBodyAsStream();
				int x = 0;
				while ((x = stream.read()) != -1) {
					System.out.print((char) x);
				}
				System.out.println("");
				method_tmp.releaseConnection();
			} catch (HttpException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Step 3
			while (true) {
				try {
					

//					MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
//					HttpClient client = new HttpClient(connectionManager);
					HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true));
					GetMethod method_tmp = new GetMethod("http://" + RubisTestPushing.IP
							+ ":" + RubisTestPushing.PORT + "/" + RubisTestPushing.PROJECT
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
					int x = 0;
					while ((x = stream.read()) != -1) {
						System.out.print((char) x);
					}
					System.out.println("");
					method_tmp.releaseConnection();
					synchronized (Running.class) {
						// System.out.println("response");
						RubisTestPushing.responses++;
						System.out.println(" Number of successful Writer is : " + RubisTestPushing.responses );
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

		}
	}
}
