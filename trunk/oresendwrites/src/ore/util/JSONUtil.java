package ore.util;

import java.util.Map;

/**
 * Utility class to convert Java objects and arrays into JSON
 */
public class JSONUtil {

	public static String toJSONArray(Iterable<? extends JSONable> iterable) {
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		boolean empty = true;
		for(JSONable jsonable : iterable) {
			sb.append(jsonable.toJSON());
			sb.append(',');
			empty = false;
		}
		if(!empty) {
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append(']');
		return sb.toString();
	}
	
	public static String toJSONArray(String ... args) {
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		boolean empty = true;
		for(String str : args) {
			sb.append(str);
			sb.append(',');
			empty = false;
		}
		if(!empty) {
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append(']');
		return sb.toString();
	}
	
	public static String toJSONArrayStrings(Iterable<? extends Object> iterable) {
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		boolean empty = true;
		for(Object obj : iterable) {
			if(obj instanceof String) {
				sb.append('"' + obj.toString() + '"');
			} else {
				sb.append(obj.toString());
			}
			sb.append(',');
			empty = false;
		}
		if(!empty) {
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append(']');
		return sb.toString();
	}
	
	public static String toJSONObject(Map<String, ?> map) {
		StringBuffer sb = new StringBuffer();
		sb.append('{');
		for(Map.Entry<String, ?> entry : map.entrySet()) {
			sb.append('"' + entry.getKey() + '"');
			sb.append(':');
			Object value = entry.getValue();
			if(value instanceof JSONable) {
				sb.append(((JSONable) value).toJSON());
			} else {
				sb.append(value.toString());
			}
			sb.append(',');
		}
		if(map.size() > 0) {
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append('}');
		return sb.toString();
	}
	
	public static String toJSONObject(String ... args) {
		if((args.length % 2) != 0) {
			throw new IllegalArgumentException("Even number of arguments required");
		}
		StringBuffer sb = new StringBuffer();
		sb.append('{');
		for(int i=0; i < args.length; i+= 2) {
			sb.append('"' + args[i] + '"');
			sb.append(':');
			sb.append(args[i+1]);
			sb.append(',');
		}
		if(args.length > 1) {
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append('}');
		return sb.toString();
	}
	
	public static String quote(String str) {
		return '"' + str + '"';
	}
}
