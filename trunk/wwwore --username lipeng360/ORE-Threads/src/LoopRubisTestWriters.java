import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;


public class LoopRubisTestWriters {

	private static int num =  773137+990*49;
	private static int wln = 1; // writer listener number
	private static int response = 1;

	public static void main(String[] args) throws Exception {
		loopWriters();
	}
	
	public static void loopWriters() throws InterruptedException{
		Date startTime = new Date();
		System.out.println("startTime : " + startTime.toLocaleString());
		for (int i =773137; i <= num; i+=49) {
			Thread thread = new Thread(new Running("" + ( i ) , "" + i ));
			Thread.sleep(20);
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
					synchronized (Writers.class) {
						response++;
						if(response==num*wln){
							response = 1;
							Thread.sleep(1000);
							loopWriters();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
