package ore.chat.action;

import static ore.util.JSONUtil.quote;
import static ore.util.JSONUtil.toJSONArrayStrings;
import static ore.util.JSONUtil.toJSONObject;

import java.util.List;

import org.hibernate.Query;

public class GetRooms extends Action {

	@SuppressWarnings("unchecked")
	@Override
	protected void run() {
		Query query = session.createQuery("SELECT room.roomName FROM ChatSession room");
		List results = query.list();
		pw.print(toJSONObject(	"type", quote("rooms"),
								"rooms", toJSONArrayStrings(results)
		));
	}

}
