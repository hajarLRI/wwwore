package ore.simulator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ore.model.DataObject;
import ore.model.Environment;
import ore.model.PublisherClient;
import ore.model.Task;

public class Simulator {
	private Environment env;
	private long timeStepMs = 20;
	private BigInteger time = BigInteger.ZERO;
	private Map<BigInteger, Set<Task>> nextExecute = new HashMap<BigInteger, Set<Task>>();
	private Map<Task, BigInteger> numberOfExecutions = new HashMap<Task, BigInteger>();

	public Simulator(Environment env) {
		this.env = env;
		Set<? extends Task> tasks = env.getTasks();
		for(Task t : tasks) {
			schedule(t);
		}
	}
	
	private void schedule(Task t) {
		BigInteger numExecution = numberOfExecutions.get(t);
		if(numExecution == null) {
			numExecution = BigInteger.ONE;
			numberOfExecutions.put(t, BigInteger.ONE);
		} else {
			numExecution = numExecution.add(BigInteger.ONE);
			numberOfExecutions.put(t, numExecution);
		}
		double freq = t.getFrequency();
		double freqInv = 1/freq;
		double freqMs = freqInv * 1000;
		double scale = (freqMs/timeStepMs);
		BigDecimal count = BigDecimal.valueOf(scale).multiply(new BigDecimal(numExecution));
		BigInteger nextTime = count.setScale(0, RoundingMode.CEILING).toBigInteger().multiply(BigInteger.valueOf(timeStepMs)); 
		addExecutor(nextTime, t);
	}
	
	private void addExecutor(BigInteger bi, Task t) {
		Set<Task> tasks = nextExecute.get(bi);
		if(tasks == null) {
			tasks = new HashSet<Task>();
			nextExecute.put(bi, tasks);
		}
		tasks.add(t);
	}
	
	public void run() {
		while(true) {
			BigInteger nextTime = time.add(BigInteger.valueOf(timeStepMs));
			Set<Task> tasks = nextExecute.get(nextTime);
			if(tasks != null) {
				for(Task t : tasks) {
					t.execute();
					schedule(t);
				}
			}
			nextExecute.remove(nextTime);
			time = nextTime;
		}
	}
	
	public static void main(String[] args) {
		Set<PublisherClient> pcs = new HashSet<PublisherClient>();
		pcs.add(new PublisherClient(null, new DataObject("First"), 1.0));
		pcs.add(new PublisherClient(null, new DataObject("Second"), 2.0));
		pcs.add(new PublisherClient(null, new DataObject("Third"), 3.0));
		Environment env = new Environment(null, pcs, null);
		Simulator sim = new Simulator(env);
		sim.run();
	}
}
