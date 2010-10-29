package ore.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Utility class for commonly used Servlet operations
 */
public class HTTPServletUtil {
	
	public static String getCookie(String name, HttpServletRequest req) {
		if(req.getCookies() == null) {
			return null;
		}
		for(Cookie cookie : req.getCookies()) {
			String cookieName = cookie.getName();
			if(cookieName.equals(name)) {
				return cookie.getValue();
			}
		}
		return null;
	}
	
	public static String getRequiredParameter(HttpServletRequest request, String p) {
		String value = request.getParameter(p);
		if(value == null) {
			throw new RuntimeException("Required parameter '"+ p +"'");
		}
		return value;
	}
}
