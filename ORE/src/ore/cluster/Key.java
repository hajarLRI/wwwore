package ore.cluster;

import java.util.HashMap;
import java.util.Map;

public class Key {
	private String className;
	private String id;
	private String property;
	
	private static Map<String, Map<String, Map<String, Key>>> instances = new HashMap<String, Map<String, Map<String, Key>>>();
	
	public static Key create(String className, String id, String property) {
		Map<String, Map<String, Key>> fromClassName = instances.get(className);
		if(fromClassName == null) {
			fromClassName = new HashMap<String, Map<String, Key>>();
			instances.put(className, fromClassName);
		}
		Map<String, Key> fromId = fromClassName.get(id);
		if(fromId == null) {
			fromId = new HashMap<String, Key>();
			fromClassName.put(id, fromId);
		}
		Key k = fromId.get(property);
		if(k == null) {
			k = new Key(className, id, property);
			fromId.put(property, k);
		}
		return k;
	}
	
	private Key(String className, String id, String property) {
		this.className = className;
		this.id = id;
		this.property = property;
	}
	
	public static Key parse(String s) {
		String[] parts = s.split("__");
		Key k = create(parts[0], parts[1], parts[2]);
		return k;
	}
	
	public String getClassName() {
		return className;
	}

	public String getId() {
		return id;
	}

	public String getProperty() {
		return property;
	}

	public String toString() {
		return className + "__" + id + "__" + property;
	}
}
