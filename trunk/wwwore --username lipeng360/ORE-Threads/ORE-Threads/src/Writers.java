import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;

public class Writers {

	private static int num = 50;


	public static void main(String[] args) throws Exception {
		for (int i = 0; i < num; i++) {
			Thread thread = new Thread(new Running("" + ( i ) , "" + i ));
			Thread.sleep(200);
			thread.start();
		}
	}

	static class Running implements Runnable {
		private String itemId;
		private String id;

		public Running(String itemId,String id) {
			this.itemId = itemId;
			this.id = id;
		}

		public void run() {
			try {
				HttpClient client = new HttpClient();
				GetMethod method_tmp = new GetMethod(
						"http://"+Pushing.IP+":"+Pushing.PORT+"/"+Pushing.PROJECT+"/servlet/RubisServlet?operation=BidAction&itemId="
						+ itemId
						+ "&userId=1&qty=1&bid=61&maxBid=81&minBid=61&maxQty=1");
				method_tmp.getParams().setCookiePolicy(
						CookiePolicy.IGNORE_COOKIES);
				method_tmp.setRequestHeader("Cookie", "sessionID=" + id);
				client.executeMethod(method_tmp);

				method_tmp.releaseConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
