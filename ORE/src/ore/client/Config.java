package ore.client;

public class Config {

	public static String[] IPs = {"192.168.0.2", "192.168.0.10"};
	public static String[] httpPorts = {"8080", "8080"};
	public static String[] jmsPorts = {"61616", "61616"};
	
	public static String PROJECT = "ORE";
	public static int readerResponses = 0;
	public static int realReaderResponses = 0;
	public static float avg = 0;
	static long cometBackoff = 100;
	public static boolean timerFlag = false;
	public static long startTime;
	public static String mode = "normal"; //"weighted"
	public static String redirectOK = "no"; //"true"
	
	public static int readers = 50;
	public static int itemsPerUser = 5;
	public static double overlap = .5;
	public static double R = 0;
	public static boolean random = true;

	public static int num = (int) Math.ceil(readers * (itemsPerUser * (1-overlap)));
	
	public static boolean generated = true; //false means generate new interest graph, true means using old one 
}
