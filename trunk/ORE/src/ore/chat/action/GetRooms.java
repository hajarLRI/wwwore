package ore.chat.action;

import static ore.util.JSONUtil.quote;
import static ore.util.JSONUtil.toJSONArrayStrings;
import static ore.util.JSONUtil.toJSONObject;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
import org.hibernate.Session;

public class GetRooms extends Action {

	@SuppressWarnings("unchecked")
	@Override
	protected void run(Session session, HttpServletRequest request, PrintWriter pw) {
		Query query = session.createQuery("SELECT room.roomName FROM ChatSession room");
		List results = query.list();
		pw.print(toJSONObject(	"type", quote("rooms"),
								"rooms", toJSONArrayStrings(results)
		));
	}

}
