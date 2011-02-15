package ore.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ore.api.HibernateUtil;
import ore.cluster.ClusterManager;
import ore.subscriber.Subscriber;
import ore.subscriber.SubscriberManager;
import ore.util.HTTPServletUtil;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class DBServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static SessionFactory factory;
	
	@Override
	public void init() {
		factory = HibernateUtil.getSessionFactory(getServletContext());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Session session = factory.openSession();
		Transaction tx = session.beginTransaction();
		String operation = request.getParameter("operation");
		if((operation == null) || operation.equals("init")) {
			init(session);
			clear(session);
		}
		session.flush();
		tx.commit();
		session.close();
	}
	
	private void init(Session session) {
		for(int i=0; i <= 1000; i++) {
			SQLQuery query = session.createSQLQuery("insert into chatsession values ('" + i + "')");
			query.executeUpdate();
		}
	}
	
	private void clear(Session session) {
		SQLQuery query = session.createSQLQuery("delete from roomuser");
		query.executeUpdate();
		query = session.createSQLQuery("delete from chatmessages");
		query.executeUpdate();
	}
}
