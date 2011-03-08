package ore.client.executors;

import ore.client.initializers.ReaderWorkload;

public interface WorkloadExecutor {
	public void execute(ReaderWorkload workload) throws Exception;
}
