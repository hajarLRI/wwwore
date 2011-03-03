package ore.client;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ore.util.MathUtil;

public class ReaderWorkload<T> implements Runnable {
	List<User<T>> users;
	LinkedList<WebReader> runners = new LinkedList<WebReader>();
	
	public ReaderWorkload(List<User<T>> users) {
		this.users = users;
	}
	
	public void redirectAll(String ip, String port) throws Exception {
		for(WebReader r : runners) {
			r.direct(ip, port);
			Thread.sleep(1000);
		}
	}
	
	public void stop() throws Exception {
		for(WebReader r : runners) {
			r.stop();
		}
	}
	
	public void stopAtRandom() throws Exception {
		if(runners.size() == 0) {
			return;
		}
		double r = Math.random();
		double scaled = r * runners.size();
		int index = (int) scaled;
		WebReader wr = runners.get(index);
		wr.stop();
		runners.remove(wr);
	}
	
	public void stopOldest() throws Exception {
		WebReader wr = runners.get(0);
		wr.stop();
		runners.remove(wr);
	}
	
	public synchronized void change(int index) throws Exception {
		WebReader wr = runners.get(index);
		wr.stop();
		runners.remove(wr);
		User u = wr.getUser();
		wr = new WebReader(Machine.getRandomMachine(), u);
		runners.add(wr);
		wr.init();
		Thread t = new Thread(wr);
		t.start();
	}
	
	public synchronized void changeOldest() throws Exception {
		change(0);
	}
	
	public synchronized void changeAtRandom() throws Exception {
		change(MathUtil.randomInt(0, Config.readers));
	}
	
	protected void setup() {
		int m = users.size();
		int n = Machine.machines.size();
		int i = 0;
		int chunkSize = (int) Math.round((m * Config.R)/n + 1);
		System.out.println("Chunksize: " + chunkSize);
		System.out.println("Writers: " + (int) Math.ceil(Config.readers * (Config.itemsPerUser * (1-Config.overlap))));
		Iterator<Machine> it = Machine.machines.iterator();
		Machine machine = it.next();
		for(User user : users) {
			WebReader runner = new WebReader(machine, user);
			System.out.println("User " + user.getID() + ", assigned to machine " + machine.getUrlPrefix());
			runners.add(runner);
			try {
				runner.init();
			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
			if(i == (chunkSize)) {
				if(it.hasNext()) {
					machine = it.next();
				} else {
					it = Machine.machines.iterator();
					machine = it.next();
				}
				i = 0;
			}
		}
	}
	
	public void run() {
		setup();
		System.out.println("Readers joined");
		for(WebReader current : runners) {
			Thread thread = new Thread(current);
			thread.start();
		}
	}

}
