package ore.chat.servlet;

import static ore.chat.action.Action.CHANGE_NAME;
import static ore.chat.action.Action.CHAT;
import static ore.chat.action.Action.CHECK_ROOMS;
import static ore.chat.action.Action.JOIN;
import static ore.chat.action.Action.LEAVE;
import static ore.chat.action.Action.NEW_ROOM;
import static ore.chat.action.Action.OPERATION;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ore.api.HibernateUtil;
import ore.chat.action.Action;
import ore.chat.action.ChangeName;
import ore.chat.action.Chat;
import ore.chat.action.GetRooms;
import ore.chat.action.Join;
import ore.chat.action.Leave;
import ore.chat.action.NewRoom;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class ChatServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static SessionFactory factory;
	private Map<String, Action> actions = new HashMap<String, Action>();
	private ReentrantLock lock = new ReentrantLock();
	private Condition finished = lock.newCondition();
	private boolean init = false;
	
	public static SessionFactory getHibernateSessionFactory() {
		return factory;
	}
	
	@Override
	public synchronized void init() {
		lock.lock();
		factory = HibernateUtil.getSessionFactory(getServletContext());
		actions.put(NEW_ROOM, new NewRoom());
		actions.put(CHAT, new Chat());
		actions.put(JOIN, new Join());
		actions.put(CHECK_ROOMS, new GetRooms());
		actions.put(LEAVE, new Leave());
		actions.put(CHANGE_NAME, new ChangeName());

		Session session = factory.openSession();

		Transaction tx = session.beginTransaction();
		SQLQuery query = session.createSQLQuery("delete from roomuser");
		query.executeUpdate();
		query = session.createSQLQuery("delete from chatmessages");
		query.executeUpdate();
		session.flush();
		tx.commit();
		session.close();
		init = true;
		finished.signalAll();
		lock.unlock();
	}
	   
	protected  void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		while(!init) {
			lock.lock();
			try {
				finished.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lock.unlock();
		}
		String operation = getParameter(OPERATION, request);
		Session session = factory.openSession();
		Transaction tx = session.beginTransaction();
		response.setContentType("application/json");
		/*String login = request.getParameter("login");
		if(login != null) {
			String userName = getParameter("userName", request);
			HttpSession httpSession = request.getSession();
			httpSession.setMaxInactiveInterval(30);
			httpSession.setAttribute("userName", userName);
		}*/
		Action action = actions.get(operation);
		PrintWriter pw = response.getWriter();
		if(action != null) {
			action.service(session, request, pw);
		}
		tx.commit();
		session.flush();
		session.clear();
		session.close();
	}
	
	private String getParameter(String p, HttpServletRequest request) {
		String value = request.getParameter(p);
		if(value == null) {
			throw new RuntimeException("Required parameter '"+ p +"'");
		}
		return value;
	}
}
