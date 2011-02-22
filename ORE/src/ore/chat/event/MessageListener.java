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
	public String elementAdded(Event event) {
		ChatMessage message = (ChatMessage) event.getNewValue();
		return message.toJSON();
	}
}