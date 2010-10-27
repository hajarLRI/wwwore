package ore.event;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import ore.api.Event;
import ore.exception.BrokenCometException;

/**
 * Maintains registration of callbacks for database mutation events (update, delete, insert)
 * <br/> 
 * (package-protected)
 */
class TableManager {
	
	//TYPEDEF
	@SuppressWarnings("serial")
	private static class InsertListenerList extends ConcurrentLinkedQueue<InsertListener> {}
	@SuppressWarnings("serial")
	private static class DeleteListenerList extends ConcurrentLinkedQueue<DeleteListener> {}
	@SuppressWarnings("serial")
	private static class UpdateListenerList extends ConcurrentLinkedQueue<UpdateListener> {}
	@SuppressWarnings("serial")
	private static class InsertMap extends HashMap<String, InsertListenerList> {}
	@SuppressWarnings("serial")
	private static class DeleteMap extends HashMap<String, DeleteListenerList> {}
	@SuppressWarnings("serial")
	private static class UpdateMap extends HashMap<String, UpdateListenerList> {}
	//END TYPEDEF
	
	TableManager() {}
	
	private InsertMap insertMap = new InsertMap();
	private DeleteMap deleteMap = new DeleteMap();
	private UpdateMap updateMap = new UpdateMap();
	
	public void addInsertListener(String tableName, InsertListener listener) {
		InsertListenerList list = insertMap.get(tableName);
		if(list == null) {
			list = new InsertListenerList();
			insertMap.put(tableName, list);
		}
		list.add(listener);
		listener.setOwner(list);
	}
	
	public void addDeleteListener(String tableName, DeleteListener listener) {
		DeleteListenerList list = deleteMap.get(tableName);
		if(list == null) {
			list = new DeleteListenerList();
			deleteMap.put(tableName, list);
		}
		list.add(listener);
		listener.setOwner(list);
	}
	
	public void addUpdateListener(String tableName, UpdateListener listener) {
		UpdateListenerList list = updateMap.get(tableName);
		if(list == null) {
			list = new UpdateListenerList();
			updateMap.put(tableName, list);
		}
		list.add(listener);
		listener.setOwner(list);
	}

	void insert(String tableName, Event event) {
		InsertListenerList list = insertMap.get(tableName);
		if(list != null) {
			InsertListenerList broken = new InsertListenerList();
			for(InsertListener listener : list) {
				try {
					listener.insert(event);
				} catch(BrokenCometException be) {
					broken.add(listener);
				}
			}
			for(InsertListener listener : broken) {
				list.remove(listener);
			}
		}
	}
	
	void delete(String tableName, Event event) {
		DeleteListenerList list = deleteMap.get(tableName);
		if(list != null) {
			DeleteListenerList broken = new DeleteListenerList();
			for(DeleteListener listener : list) {
				try {
					listener.delete(event);
				} catch(BrokenCometException be) {
					broken.add(listener);
				}
			}
			for(DeleteListener listener : broken) {
				list.remove(listener);
			}
		}
	}
	
	void update(String tableName, Event event) {
		UpdateListenerList list = updateMap.get(tableName);
		if(list != null) {
			UpdateListenerList broken = new UpdateListenerList();
			for(UpdateListener listener : list) {
				try {
					listener.update(event);
				} catch(BrokenCometException be) {
					broken.add(listener);
				}
			}
			for(UpdateListener listener : broken) {
				list.remove(listener);
			}
		}
	}

}
