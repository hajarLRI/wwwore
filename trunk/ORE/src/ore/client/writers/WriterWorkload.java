package ore.client.writers;

import ore.client.initializers.WorkloadInitializer;

public interface WriterWorkload extends Runnable {
	void init(WorkloadInitializer readerWorkload);
}
