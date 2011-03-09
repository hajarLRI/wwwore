package ore.client.writers;

import java.util.List;

import ore.client.WebReader;
import ore.client.initializers.WorkloadInitializer;

public class WriterPerUser<T> implements WriterWorkload {

	private List<WebReader> users;
	
	@Override
	public void init(WorkloadInitializer readerWorkload) {
		this.users = readerWorkload.getReaders();
	}

	@Override
	public void run() {
		for(WebReader wr : users) {
			WebWriter ww = new WebWriter(wr.getUser(), wr.getMachine());
			ww.start();
		}
	}
	
}
