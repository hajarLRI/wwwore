package ore.client.executors;

import ore.client.Config;
import ore.client.initializers.ReaderWorkload;

public class RandomExecutor implements WorkloadExecutor {

	@Override
	public void execute(ReaderWorkload workload) throws Exception {
		Thread.sleep(1000);
		Config.redirectOK = "no";
		while(true) {
			Thread.sleep(1000);
			workload.changeAtRandom();
		}
	}

}
