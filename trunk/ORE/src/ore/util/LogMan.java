package ore.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a convenience wrapper to other logging frameworks
 */
public class LogMan {
	private Logger logger;
	
	private static LogMan instance;
	
	private LogMan() {
		logger = LoggerFactory.getLogger(LogMan.class);
	}
	
	public static LogMan getInstance() {
		if(instance == null) {
			instance = new LogMan();
		}
		return instance;
	}
	
	public static void info(String str) {
		//getInstance().logger.info(str);
	}
	
	public static void error(String str) {
		getInstance().logger.error(str);
	}
	
	public static void trace(String str) {
		//getInstance().logger.trace(str);
		System.out.println(str);
	}
	
	public static void info(String str, Object ... args) {
		getInstance().logger.info(str, args);
	}
	
	public static void error(String str, Object ... args) {
		getInstance().logger.error(str, args);
	}
	
	public static void trace(String str, Object ... args) {
		getInstance().logger.trace(str, args);
	}
}
