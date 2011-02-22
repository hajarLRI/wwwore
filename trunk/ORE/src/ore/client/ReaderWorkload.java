package ore.client;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ReaderWorkload implements Runnable {
	List<User> users;
	
	public ReaderWorkload(List<User> users) {
		this.users = users;
	}
	
	public void run() {
		List<WebReader> runners = new LinkedList<WebReader>();
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
		System.out.println("Readers joined");
		for(WebReader current : runners) {
			Thread thread = new Thread(current);
			thread.start();
		}
	}

}
