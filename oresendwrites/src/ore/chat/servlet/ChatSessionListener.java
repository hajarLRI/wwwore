package ore.chat.servlet;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import ore.chat.entity.User;
import ore.util.LogMan;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class ChatSessionListener implements HttpSessionListener {
	
	public void sessionCreated(HttpSessionEvent se) {
		LogMan.info("Session created");
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		Session session = ChatServlet.getHibernateSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String userName = (String) se.getSession().getAttribute("userName");
		LogMan.info("Session timed out: " + userName);
		User user = (User) session.get(User.class, userName);
		user.getRooms().clear();
		session.saveOrUpdate(user);
		tx.commit();
		session.close();
	}
}