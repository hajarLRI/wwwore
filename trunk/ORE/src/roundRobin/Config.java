package roundRobin;

public class Config {

	public static String[] IPs = {"10.125.1.110","10.125.3.116", "10.122.29.170", "10.122.85.231","10.113.38.235", "10.116.155.214"};
	public static String PORT = "8080";
	public static String PROJECT = "ORE";
	public static int readerResponses = 0;
	public static int realReaderResponses = 0;
	public static float avg = 0;
	static long cometBackoff = 100;
	public static boolean timerFlag = false;
	
	public static int readers = 200;
	public static int itemsPerUser = 10;
	public static double overlap = 0;
	public static double R = 0;
	
}
