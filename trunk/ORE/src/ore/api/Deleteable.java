package ore.api;

import java.util.Collection;

public interface Deleteable {
	public void setOwner(Collection c);
	public void delete();
}
