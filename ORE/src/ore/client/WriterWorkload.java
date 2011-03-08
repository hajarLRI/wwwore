package ore.client;

import java.util.List;

public class WriterWorkload<T> implements Runnable {

	private List<WebReader> users;
	
	public WriterWorkload(List<WebReader> users) {
		this.users = users;
	}

	@Override
	public void run() {
		for(WebReader wr : users) {
			WebWriter ww = new WebWriter(wr.getUser(), wr.getMachine());
			ww.start();
		}
	}
	
}
