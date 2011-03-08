package ore.util;

import java.io.IOException;
import java.io.InputStream;

public class StreamCleaner extends Thread {
	private InputStream input;
	
	public StreamCleaner(InputStream input) {
		this.input = input;
	}

	@Override
	public void run() {
		byte[] buffer = new byte[512];
		try {
			while(input.read(buffer) != -1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
