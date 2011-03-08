package ore.client.initializers;

import java.util.List;

import ore.client.User;

public interface WorkloadInitializer<T> extends Runnable {
	public ReaderWorkload initialize(List<User<T>> users) throws Exception;
}
