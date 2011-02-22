package ore.cluster;

public class Key {
	private String className;
	private String id;
	private String property;
	
	public Key(String className, String id, String property) {
		this.className = className;
		this.id = id;
		this.property = property;
	}
	
	public int hashCode() {
		int result = 13;
		result = result*17 + className.hashCode();
		result = result*17 + id.hashCode();
		result = result*17 + property.hashCode();
		return result;
	}
	
	public boolean equals(Object o) {
		if(o instanceof Key) {
			Key other = (Key) o;
			return this.className.equals(other.className) && this.id.equals(other.id) && this.property.equals(other.property);
		} else {
			return false;
		}
	}
	
	public static Key parse(String s) {
		String[] parts = s.split("__");
		Key k = new Key(parts[0], parts[1], parts[2]);
		return k;
	}
	
	public String toString() {
		return className + "__" + id + "__" + property;
	}
}
