package ore.client.generators;

import java.util.List;

import ore.client.User;

public interface WorkloadGenerator<T> {
	List<User<T>> generate() throws Exception;
}
