package ore.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import ore.util.StreamCleaner;

public class HMetis {

	private static String shmetis = "C:\\cygwin\\home\\Eric\\hmetis-1.5.3-WIN32\\shmetis.exe";
	
	public static void shmetis(ore.hypergraph.Hypergraph graph, int parts, int factor) throws Exception {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("C:\\Temp\\hmetisIn")));
		graph.print(pw);
		pw.close();
		Process proc = Runtime.getRuntime().exec(shmetis + " " + "C:\\Temp\\hmetisIn" + " " + parts + " " + factor, null, new File("C:\\Temp"));
		final InputStream input = proc.getInputStream();
		final InputStream error = proc.getErrorStream();
		new StreamCleaner(input).start();
		new StreamCleaner(error).start();
		proc.waitFor();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Temp\\hmetisIn.part." + parts)));
		String partString = br.readLine();
		int i=0;
		while(partString != null) {
			int part = Integer.parseInt(partString);
			graph.setPartition(i, part);
			i++;
			partString = br.readLine();
		}
	}
	
}
