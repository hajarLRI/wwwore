package ore.client;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import ore.client.executors.WorkloadExecutor;
import ore.client.generators.WorkloadGenerator;
import ore.client.initializers.ReaderWorkload;
import ore.client.initializers.WorkloadInitializer;
import ore.client.writers.WriterWorkload;

public class WorkloadDriver {
	private static void setupMachines(int num) {
		Config.IPs = new String[num];
		Config.httpPorts = new String[num];
		Config.jmsPorts = new String[num];
		for(int i=0; i < num; i++) {
			Config.IPs[i] = "Machine(" + i + ")";
			Config.httpPorts[i] = String.valueOf(i);
			Config.jmsPorts[i] = String.valueOf(i);
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		if(!Config.mock) {
			List<Thread> threads = new LinkedList<Thread>();
			for (Machine m : Machine.machines) {
				Thread t = new Thread(m);
				threads.add(t);
				t.start();
			}
			for (Thread t : threads) {
				t.join();
			}
		}
		
		for(int i=0;i < 10; i++) {
			setupMachines(i+2);
			Config.latticeSize = 100;
			Config.clusteringExponent = 20;
			Machine.createMachines(Config.IPs, Config.httpPorts, Config.jmsPorts);
			
			List<User<Integer>> users = generate();
			saveGraph(users);
			ReaderWorkload read = initialize(users);
			WriterWorkload writers = write(read);
			execute(read);

			if(Config.mock) {
				Thread.sleep(5000);
				double total = 0;
				for(Machine m : Machine.getMachines()) {
					total += ((MockMachine) m).getRatio();
				}
				System.err.println("Result: " + (total/Machine.getNumMachines()));
			}
			writers.stop();
		}
	}
	
	private static List<User<Integer>> generate() throws Exception {
		WorkloadGenerator generator = (WorkloadGenerator) Class.forName(Config.generator).newInstance();
		List<User<Integer>> users = generator.generate();
		return users;
	}
	
	private static ReaderWorkload initialize(List<User<Integer>> users) throws Exception {
		WorkloadInitializer initializer = (WorkloadInitializer) Class.forName(Config.initializer).newInstance();
		ReaderWorkload read = initializer.initialize(users);
		read.run();
		return read;
	}
	
	private static WriterWorkload write(ReaderWorkload read) throws Exception {
		WriterWorkload writers = (WriterWorkload) Class.forName(Config.writer).newInstance();
		writers.init(read);
		writers.run();
		return writers;
	}
	
	private static void execute(ReaderWorkload read) throws Exception {
		WorkloadExecutor executor = (WorkloadExecutor) Class.forName(Config.executor).newInstance();
		executor.execute(read);
	}
	
	private static <T> void saveGraph(List<User<T>> users) throws Exception {
		FileOutputStream fos = new FileOutputStream("C:\\Temp\\test.text");
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(users);
		out.close();
	}
}
