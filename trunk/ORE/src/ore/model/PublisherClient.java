package ore.model;

public class PublisherClient implements Task {
	private Server server;
	private DataObject object;
	private double frequency = 1.0;
	
	public PublisherClient(Server server, DataObject object, double frequency) {
		this.server = server;
		this.object = object;
		this.frequency = frequency;
	}

	@Override
	public double getFrequency() {
		return frequency;
	}

	@Override
	public void execute() {
		System.out.println("Execute: " + object.getName());
		//server.publishTo(object);
	}
}
