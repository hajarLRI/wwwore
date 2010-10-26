import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;

public class Pushing {
	
//	public static String IP = "198.162.54.73";
//	public static String PORT = "9080";
	public static String IP = "127.0.0.1";
	public static String PORT = "8080";
	public static String PROJECT = "ORE-trigger";
	private static int modCount = 100;

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < modCount; i++) {
			Thread thread = new Thread(new Running("" + (i), "" + i));
			Thread.sleep(150);
			thread.start();
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

			//Step 1
			try {
				HttpClient client = new HttpClient();
				GetMethod method_tmp = new GetMethod(
						"http://"+IP+":"+PORT+"/"+PROJECT+"/connect");
				method_tmp.getParams().setCookiePolicy(
						CookiePolicy.IGNORE_COOKIES);
				method_tmp.setRequestHeader("Cookie", "sessionID=none");
				client.executeMethod(method_tmp);
			} catch (HttpException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			//Step 2
			try {
				HttpClient client = new HttpClient();
				GetMethod method_tmp = new GetMethod(
						"http://"+IP+":"+PORT+"/"+PROJECT+"/servlet/RubisServlet?operation=ViewiBidAction&itemId="
								+ itemId);
				method_tmp.getParams().setCookiePolicy(
						CookiePolicy.IGNORE_COOKIES);
				method_tmp.setRequestHeader("Cookie", "sessionID=" + id);
				client.executeMethod(method_tmp);
				InputStream stream = method_tmp.getResponseBodyAsStream();
				int x = 0;
				while ((x = stream.read()) != -1) {
					System.out.print((char) x);
				}

				method_tmp.releaseConnection();
			} catch (HttpException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			//Step 3
			loopConnect(id);

		}
		
		public void loopConnect(String id){
			try {
				HttpClient client = new HttpClient();
				GetMethod method_tmp = new GetMethod(
						"http://"+IP+":"+PORT+"/"+PROJECT+"/connect");
				method_tmp.getParams().setCookiePolicy(
						CookiePolicy.IGNORE_COOKIES);
				method_tmp.setRequestHeader("Cookie", "sessionID=" + id);
				client.executeMethod(method_tmp);
				InputStream stream = method_tmp.getResponseBodyAsStream();
				int x = 0;
				while ((x = stream.read()) != -1) {
					System.out.print((char) x);
				}
				System.out.println("");
				method_tmp.releaseConnection();
				loopConnect(id);
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
