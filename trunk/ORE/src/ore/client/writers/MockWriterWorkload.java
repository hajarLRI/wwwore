package ore.client.writers;

import java.util.LinkedList;
import java.util.List;

import ore.client.WebReader;
import ore.client.initializers.WorkloadInitializer;

public class MockWriterWorkload implements WriterWorkload {

	private List<WebReader> users;
	
	@Override
	public void init(WorkloadInitializer readerWorkload) {
		this.users = readerWorkload.getReaders();
	}

	@Override
	public void run() {
		new Thread( 
				 new Runnable() { public void run() {
		List<WebWriter> writers = new LinkedList<WebWriter>();
		for(WebReader wr : users) {
			WebWriter ww = new WebWriter(wr.getUser(), wr.getMachine());
			writers.add(ww);
		}
		while(!stop) {
			for(WebWriter ww : writers) {
				ww.step(0);
			}
		}
		}}).start();
	}
	
	public void stop() {
		stop = true;
	}
	
	private boolean stop = false;

}
