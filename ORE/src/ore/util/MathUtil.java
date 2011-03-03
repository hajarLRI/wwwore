package ore.util;

public class MathUtil {

	public static int randomInt(int low, int high) {
		int range = high-low;
		int tmp = (int) (Math.random() * range);
		return tmp + low;
	}
}
