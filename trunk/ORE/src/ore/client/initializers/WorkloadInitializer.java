package ore.client.initializers;

import java.util.List;

import ore.client.User;
import ore.client.WebReader;

public interface WorkloadInitializer<T> extends Runnable {
	ReaderWorkload initialize(List<User<T>> users) throws Exception;
	List<WebReader> getReaders();
	int getNumObjects();
}
