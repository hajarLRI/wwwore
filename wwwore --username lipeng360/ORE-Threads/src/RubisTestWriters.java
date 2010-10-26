import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;

public class RubisTestWriters {

	private static int num = 100;
	private static int wln = 1; // writer listener number

	public static void main(String[] args) throws Exception {
		for (int i = 1; i <= num; i++) {
			Thread thread = new Thread(new Running("" + (i), "" + i));
			Thread.sleep(5);
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
			try {
				HttpClient client;
				GetMethod method_tmp;
				for (int i = 1; i <= wln; i++) {
					client = new HttpClient();
					method_tmp = new GetMethod(
							"http://"
									+ RubisTestPushing.IP
									+ ":"
									+ RubisTestPushing.PORT
									+ "/"
									+ RubisTestPushing.PROJECT
									+ "/test?operation=newBid"
									+ i
									+ "&itemId="+itemId+"&minBid=1&bid=1&maxBid=2&maxQty=3&qty=4");
					method_tmp.getParams().setCookiePolicy(
							CookiePolicy.IGNORE_COOKIES);
					method_tmp.setRequestHeader("Cookie", "sessionID=" + id);
					client.executeMethod(method_tmp);
					method_tmp.releaseConnection();
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
