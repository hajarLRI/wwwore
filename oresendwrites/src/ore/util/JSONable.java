package ore.util;

/**
 * Classes can implement this interface so they can be serialized into JSON by
 * the {@link ore.util.JSONUtil}
 */
public interface JSONable {
	public String toJSON();
}
