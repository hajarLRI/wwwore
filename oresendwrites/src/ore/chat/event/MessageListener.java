/**
 * 
 */
package ore.chat.event;

import java.io.PrintWriter;

import ore.api.DefaultCollectionChangeListener;
import ore.api.Event;
import ore.chat.entity.ChatMessage;

public class MessageListener extends DefaultCollectionChangeListener {
	@Override
	public void elementAdded(PrintWriter pw, Event event) {
		ChatMessage message = (ChatMessage) event.getNewValue();
		pw.print(message.toJSON());
	}
}