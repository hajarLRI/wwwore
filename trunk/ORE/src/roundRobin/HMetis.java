package roundRobin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class HMetis {

	private static String shmetis = "C:\\cygwin\\home\\Eric\\hmetis-1.5.3-WIN32\\shmetis.exe";
	
	public static void shmetis(ore.hypergraph.Hypergraph graph, int parts, int factor) throws Exception {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("C:\\Temp\\hmetisIn")));
		graph.print(pw);
		pw.close();
		Process proc = Runtime.getRuntime().exec(shmetis + " " + "C:\\Temp\\hmetisIn" + " " + parts + " " + factor, null, new File("C:\\Temp"));
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Temp\\hmetisIn.part." + parts)));
		int size = graph.getNumOfNodes();
		for(int i=0; i < size; i++) {
			int part = Integer.parseInt(br.readLine());
			graph.setPartition(i, part);
		}
	}
}
