
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;


public class CopyOfLoopWriters {

	private static int num = 20;
	private static int response = 1;


	public static void main(String[] args) throws Exception {
		loopWriters();
	}
	
	public static void loopWriters() throws InterruptedException{
		Date startTime = new Date();
		//System.out.println("startTime : " + startTime.toLocaleString());
		for (int i = 1; i <= num; i++) {
			Thread thread = new Thread(new Running("" + ( i ) , "" + i ));
			Thread.sleep(100);
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
			while(true)
			{
			try {
				HttpClient client = new HttpClient();
				GetMethod method_tmp = new GetMethod(
						"http://"+CopyOfPushing.IP+":"+CopyOfPushing.PORT+"/"+CopyOfPushing.PROJECT+"/chat?operation=chat&roomName=Rooom&userName="+id+"&message=hello");
				method_tmp.getParams().setCookiePolicy(
						CookiePolicy.IGNORE_COOKIES);
				method_tmp.setRequestHeader("Cookie", "sessionID=" + id);
				client.executeMethod(method_tmp);

				method_tmp.releaseConnection();
				Thread.sleep(1000);
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			}
		}
	}
}
