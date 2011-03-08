package ore.client;

public class Config {

	public static String[] IPs = {"localhost" , "localhost"};
	public static String[] httpPorts = {"8080", "8090"};
	public static String[] jmsPorts = {"61616", "61617"};
	
	public static String PROJECT = "ORE";
	public static int readerResponses = 0;
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
	
	public static int num = (int) Math.ceil(readers * (itemsPerUser * (1-overlap)));
	
	public static String generator = "ore.client.generators." + "PowerLawGenerator"; //"PowerLawGenerator", "SmallWorldGenerator", "FileResourceGenerator", "SimpleGenerator"
	public static String executor = "ore.client.executors." + "NoOpExecutor"; //"GreedyExecutor"
	public static String initializer = "ore.client.initializers." + "ReaderWorkload"; //"ReaderWorkload"
	
}
