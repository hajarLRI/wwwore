package ore.model;

import java.util.HashSet;
import java.util.Set;

public class User {
	private Set<DataObject> interests = new HashSet<DataObject>();
	
	public Set<DataObject> getInterests() {
		return interests;
	}
}
