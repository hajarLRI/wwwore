package ore.model;

import java.util.Set;

public interface Notifiable {
	public void notify(DataObject dataObject);
	public Set<DataObject> getInterests();
}
