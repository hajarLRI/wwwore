package ore.client;

public class Config {
	public static int numMockMachines = 2;
	public static String[] IPs;// = {"localhost" , "localhost", "foo", "bar", "baz"};
	public static String[] httpPorts;// = {"8080", "8090", "8080", "8090", "1"};
	public static String[] jmsPorts;// = {"61616", "61617", "61616", "61617", "1"};
	
	public static String PROJECT = "ORE";
	public static int readerResponses = 0;
	public static float avg = 0;
	static long cometBackoff = 100;
	public static boolean timerFlag = false;
	public static long startTime;
	public static String mode = "normal"; //"weighted"
	public static String redirectOK = "no"; //"true"
	public static boolean longPolling = false;
	
	public static int readers = 1000;
	public static int itemsPerUser = 50;
	public static double overlap = .5;
	public static double R = 0;
	
	public static int num = (int) Math.ceil(readers * (itemsPerUser * (1-overlap)));
	
	public static String generator = "ore.client.generators." + "SmallWorldGenerator"; //"PowerLawGenerator", "SmallWorldGenerator", "FileResourceGenerator", "SimpleGenerator"
	public static String executor = "ore.client.executors." + "NoOpExecutor"; //"GreedyExecutor"
	public static String initializer = "ore.client.initializers." + "PartitionedWorkload"; //"ReaderWorkload"
	public static String writer = "ore.client.writers." + "MockWriterWorkload"; //"ReaderWorkload"
	public static boolean mock = true;
	
	public static int powerLawnumVertices = 10000;
	public static int powerLawnumEdges = 500000;
	public static int powerLawiterations = 50;
	public static int ubFactor = 5;
	
	public static int latticeSize = 5;
	public static double clusteringExponent = 20;
}
