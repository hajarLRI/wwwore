package ore.exception;

public class NoSuchSubscriber extends Exception {
	private String id;
	
	public NoSuchSubscriber(String id) {
		this.id = id;
	}
}
