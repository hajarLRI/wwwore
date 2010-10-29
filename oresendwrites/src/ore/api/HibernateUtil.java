package ore.api;
import java.io.InputStream;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.w3c.dom.Document;

/**
 * This class sets up the Hibernate metadata and sessionFactory the first
 * time {@link HibernateUtil.getSessionFactory} is called.
 */
public class HibernateUtil {

	private static SessionFactory sessionFactory;
    private static Configuration cfg;
    
    public static Configuration getConfiguration() {
    	return cfg;
    }
  
    public static SessionFactory getSessionFactory(ServletContext ctx) {
        try {
        	if(sessionFactory == null) {
        		Configuration c = new Configuration();
            	cfg = c;
            	InputStream is = ctx.getResourceAsStream("/WEB-INF/hibernate.cfg.xml");
            	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            	factory.setValidating(false);
            	Document document = factory.newDocumentBuilder().parse(is);
            	c.configure(document);
            	sessionFactory = c.buildSessionFactory();
        	}
            return sessionFactory;
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }

    }

}

