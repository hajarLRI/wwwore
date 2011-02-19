package roundRobin;

public class Config {

	public static String[] IPs = {"localhost"};
	public static String PORT = "8080";
	public static String PROJECT = "ORE";
	public static int readerResponses = 0;
	public static int realReaderResponses = 0;
	public static float avg = 0;
	static long cometBackoff = 100;
	public static boolean timerFlag = false;
	
	public static int readers = 100;
	public static int itemsPerUser = 15;
	public static double overlap = .8;
	public static double R = .5;
	
}
