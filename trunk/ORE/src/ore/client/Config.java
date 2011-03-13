package ore.client;

public class Config {
	public static int numMockMachines = 2;
	public static String[] IPs = {"localhost" , "localhost"};
	public static String[] httpPorts = {"8080", "8090"};
	public static String[] jmsPorts = {"61616", "61617"};
	
	public static String PROJECT = "ORE";
	public static int readerResponses = 0;
	public static float avg = 0;
	public static boolean timerFlag = false;
	public static long startTime;
	public static String mode = "normal"; //"weighted"
	public static String redirectOK = "no"; //"true"
	public static boolean longPolling = false;
	
	public static int readers = 50;
	public static int itemsPerUser = 10;
	public static double overlap = .5;
	public static double R = 0;
	
	public static int num = (int) Math.ceil(readers * (itemsPerUser * (1-overlap)));
	
	public static String generator = "ore.client.generators." + "SimpleGenerator"; //"PowerLawGenerator", "SmallWorldGenerator", "FileResourceGenerator", "SimpleGenerator"
	public static String executor = "ore.client.executors." + "NoOpExecutor"; //"GreedyExecutor"
	public static String initializer = "ore.client.initializers." + "ReaderWorkload"; //"ReaderWorkload"
	public static String writer = "ore.client.writers." + "WriterPerUser"; //"ReaderWorkload"
	public static boolean mock = false;
	
	public static int powerLawnumVertices = 10000;
	public static int powerLawnumEdges = 500000;
	public static int powerLawiterations = 50;
	public static int ubFactor = 5;
	
	public static int rowSize = 5;
	public static int columnSize = 5;
	public static double clusteringExponent = 20;
	public static boolean isToroidal = true;
	
	public static long cometBackoff = 100;
	public static long writerSleep = 1000;
	
	public static int nodeSize = 100;
	public static double p = .5;
	
	public static boolean jms = true;
	public static String clientJMS = "localhost:61615";
}
