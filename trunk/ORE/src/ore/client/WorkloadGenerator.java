package ore.client;

import java.util.List;

public interface WorkloadGenerator<T> {
	List<User<T>> generate(int clients, int itemsPerUser, double overlap);
}
