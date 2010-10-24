package ore.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import ore.util.HTTPServletUtil;

public class CookieFilter implements Filter {

	private static ThreadLocal<String> sessionID = new ThreadLocal<String>();
	
	public static String getSessionID() {
		return sessionID.get();
	}
	
	/**
	 * noop
	 */
	@Override
	public void destroy() {}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		if(req instanceof HttpServletRequest) {
			HttpServletRequest httpReq = (HttpServletRequest) req;
			String sessionID = HTTPServletUtil.getCookie("sessionID", httpReq);
			CookieFilter.sessionID.set(sessionID);
		}
		chain.doFilter(req, resp);
	
	}

	/**
	 * noop
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {}

}
