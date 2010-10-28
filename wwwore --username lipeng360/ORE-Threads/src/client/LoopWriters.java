package client;

import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;


public class LoopWriters {

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
		private int[] interests = new int[Pushing.numOfInterests];
		private int interestIndex = 0;

		public Running(String itemId, String id) {
			for(int i=0;i < Pushing.numOfInterests;i++) {
				interests[i] = (int) Math.floor(Math.random() * Pushing.numOfRooms);
			}
			this.itemId = itemId;
			this.id = id;
		}
 
		public void run() {
			while(true)
			{
				int currentInterest = (interestIndex++ %Pushing.numOfInterests);
			try {
				HttpClient client = new HttpClient();
				GetMethod method_tmp = new GetMethod(
						"http://"+Pushing.IP+":"+Pushing.PORT+"/"+Pushing.PROJECT+"/chat?operation=chat&roomName="+interests[currentInterest]+"&userName="+id+"&message=hello");
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
