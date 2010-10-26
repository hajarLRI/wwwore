package ore.chat.action;

import static ore.util.HTTPServletUtil.getRequiredParameter;

import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;

import ore.api.ORE;
import ore.chat.entity.ChatSession;
import ore.chat.entity.User;
import ore.chat.event.MessageListener;
import ore.chat.event.UsersListener;

public class ChangeName extends Action {

	@Override
	protected void run(Session session, HttpServletRequest request, PrintWriter pw) {
		try {
			String userName = getRequiredParameter(request, OLD_NAME);	
			String newName = getRequiredParameter(request, NEW_NAME);	
			User user = (User) session.createQuery("from User WHERE userName='" + userName + "'").uniqueResult();
			if(user == null) {
				return;
			}
			user.setUserName(newName);
			session.saveOrUpdate(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
