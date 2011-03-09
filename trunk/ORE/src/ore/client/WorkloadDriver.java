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
	public static void main(String[] args) throws Exception {
		Machine.createMachines(Config.IPs, Config.httpPorts, Config.jmsPorts);
		List<Thread> threads = new LinkedList<Thread>();
		for (Machine m : Machine.machines) {
			Thread t = new Thread(m);
			threads.add(t);
			t.start();
		}
		for (Thread t : threads) {
			t.join();
		}
		
		List<User<Integer>> users = generate();
		saveGraph(users);
		ReaderWorkload read = initialize(users);
		write(read);
		execute(read);
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
	
	private static void write(ReaderWorkload read) throws Exception {
		WriterWorkload writers = (WriterWorkload) Class.forName(Config.writer).newInstance();
		writers.init(read);
		writers.run();
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
