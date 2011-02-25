package ore.client;

public class Config {

	public static String[] IPs = {"localhost", "localhost"};
	public static String[] httpPorts = {"8080", "8090"};
	public static String[] jmsPorts = {"61616", "61617"};
	
	public static String PROJECT = "ORE";
	public static int readerResponses = 0;
	public static int realReaderResponses = 0;
	public static float avg = 0;
	static long cometBackoff = 100;
	public static boolean timerFlag = false;
	public static long startTime;
	
	public static int readers = 50;
	public static int itemsPerUser = 10;
	public static double overlap = .5;
	public static double R = 0;
	
}
