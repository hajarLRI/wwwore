package ore.hibernate;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ore.event.EventManager;
import ore.util.LogMan;

import org.hibernate.HibernateException;
import org.hibernate.event.DeleteEvent;
import org.hibernate.event.DeleteEventListener;
import org.hibernate.event.DirtyCheckEvent;
import org.hibernate.event.DirtyCheckEventListener;
import org.hibernate.event.EvictEvent;
import org.hibernate.event.EvictEventListener;
import org.hibernate.event.FlushEntityEvent;
import org.hibernate.event.FlushEntityEventListener;
import org.hibernate.event.FlushEvent;
import org.hibernate.event.FlushEventListener;
import org.hibernate.event.InitializeCollectionEvent;
import org.hibernate.event.InitializeCollectionEventListener;
import org.hibernate.event.LoadEvent;
import org.hibernate.event.LoadEventListener;
import org.hibernate.event.LockEvent;
import org.hibernate.event.LockEventListener;
import org.hibernate.event.MergeEvent;
import org.hibernate.event.MergeEventListener;
import org.hibernate.event.PersistEvent;
import org.hibernate.event.PersistEventListener;
import org.hibernate.event.PostCollectionRecreateEvent;
import org.hibernate.event.PostCollectionRecreateEventListener;
import org.hibernate.event.PostCollectionRemoveEvent;
import org.hibernate.event.PostCollectionRemoveEventListener;
import org.hibernate.event.PostCollectionUpdateEvent;
import org.hibernate.event.PostCollectionUpdateEventListener;
import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostLoadEvent;
import org.hibernate.event.PostLoadEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.event.PreCollectionRecreateEvent;
import org.hibernate.event.PreCollectionRecreateEventListener;
import org.hibernate.event.PreCollectionRemoveEvent;
import org.hibernate.event.PreCollectionRemoveEventListener;
import org.hibernate.event.PreCollectionUpdateEvent;
import org.hibernate.event.PreCollectionUpdateEventListener;
import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreLoadEvent;
import org.hibernate.event.PreLoadEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import org.hibernate.event.RefreshEvent;
import org.hibernate.event.RefreshEventListener;
import org.hibernate.event.ReplicateEvent;
import org.hibernate.event.ReplicateEventListener;
import org.hibernate.event.SaveOrUpdateEvent;
import org.hibernate.event.SaveOrUpdateEventListener;

/**
 * This class intercepts all the operations which Hibernate allows to be 
 * intercepted. This is the primary hook into Hibernate. This could also be 
 * implemented with AspectJ but this listener class seems to be easier. Using AspectJ might allow
 * more fine-grained interception to improve performance, but this listener approach seems
 * to work for now. 
 */
@SuppressWarnings("serial")
public class HibernateListener implements 	DeleteEventListener, 
											LoadEventListener,
											DirtyCheckEventListener,
											EvictEventListener,
											FlushEntityEventListener,
											FlushEventListener,
											InitializeCollectionEventListener,
											LockEventListener,
											MergeEventListener,
											PersistEventListener,
											PostCollectionRecreateEventListener,
											PostCollectionRemoveEventListener,
											PostCollectionUpdateEventListener,
											PostDeleteEventListener,
											PostInsertEventListener,
											PostLoadEventListener,
											PostUpdateEventListener,
											PreCollectionRecreateEventListener,
											PreCollectionRemoveEventListener,
											PreCollectionUpdateEventListener,
											PreDeleteEventListener,
											PreInsertEventListener,
											PreLoadEventListener,
											PreUpdateEventListener,
											RefreshEventListener,
											ReplicateEventListener,
											SaveOrUpdateEventListener
											{

	@Override
	public void onLoad(LoadEvent arg0, LoadType arg1) throws HibernateException {
		//LogMan.trace("LOAD: " + arg0.getEntityId().toString() + " of type " + arg0.getEntityClassName());
	}

	@Override
	public void onDelete(DeleteEvent arg0) throws HibernateException {
		//LogMan.trace("DELETE: " + arg0.getEntityName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onDelete(DeleteEvent arg0, Set arg1) throws HibernateException {
		//LogMan.trace("DELETE: " + arg0.getEntityName());
	}

	@Override
	public void onDirtyCheck(DirtyCheckEvent arg0) throws HibernateException {
		//LogMan.trace("DIRTY_CHECK: " + arg0.isDirty());
	}

	@Override
	public void onEvict(EvictEvent arg0) throws HibernateException {
		//LogMan.trace("EVICT");
	}

	@Override
	public void onFlushEntity(FlushEntityEvent arg0) throws HibernateException {
		//LogMan.trace("FLUSH_ENTITY");
	}

	@Override
	public void onFlush(FlushEvent arg0) throws HibernateException {
		//LogMan.trace("FLUSH");
	}

	@Override
	public void onInitializeCollection(InitializeCollectionEvent arg0)
			throws HibernateException {
		//LogMan.trace("INIT_COLLECTION");
	}

	@Override
	public void onLock(LockEvent arg0) throws HibernateException {
		//LogMan.trace("LOCK");
	}

	@Override
	public void onMerge(MergeEvent arg0) throws HibernateException {
		//LogMan.trace("MERGE");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onMerge(MergeEvent arg0, Map arg1) throws HibernateException {
		//LogMan.trace("MERGE");
	}

	@Override
	public void onPersist(PersistEvent arg0) throws HibernateException {
		//LogMan.trace("PERSIST");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onPersist(PersistEvent arg0, Map arg1)
			throws HibernateException {
		//LogMan.trace("PERSIST");
	}

	@Override
	public void onPostRecreateCollection(PostCollectionRecreateEvent arg0) {
		//LogMan.trace("POST_RECREATE_COLLECTION");
	}

	@Override
	public void onPostRemoveCollection(PostCollectionRemoveEvent arg0) {
		//LogMan.trace("POST_REMOVE_COLLECTION");
	}

	@Override
	public void onPostUpdateCollection(PostCollectionUpdateEvent arg0) {
		//LogMan.trace("POST_UPDATE_COLLECTION");	
	}

	@Override
	public void onPostDelete(PostDeleteEvent arg0) {
		//LogMan.trace("POST_DELETE");
	}

	@Override
	public void onPostInsert(PostInsertEvent arg0) {
		LogMan.trace("POST_INSERT: " + arg0.getId().toString() + " of type " + arg0.getEntity().getClass().getSimpleName());
	}

	@Override
	public void onPostLoad(PostLoadEvent arg0) {
		LogMan.trace("POST_LOAD: " + arg0.getId() + " of type " + arg0.getEntity().getClass().getSimpleName());
	}

	/**
	 * Allows interception of mutations to entity properties. These are then
	 * translated into mutations of database table columns by the {@link ore.event.EventManager} 
	 */
	@Override
	public void onPostUpdate(PostUpdateEvent arg0) {
		try {
			LogMan.trace("POST_UPDATE");
			int[] dirts = null; 
			try {
				dirts = arg0.getPersister().findDirty(arg0.getState(), arg0.getOldState(), arg0.getEntity(), arg0.getSession());
			} catch(Exception e) {
				System.out.println("Find dirty Exception");
			}
			if(dirts != null) {
				for(int i=0;i < dirts.length;i++) {
					String propertyName = arg0.getPersister().getPropertyNames()[dirts[i]];
					EventManager.getInstance().entityPropertyChanged(propertyName, arg0.getEntity(), arg0.getOldState()[dirts[i]], arg0.getState()[dirts[i]]);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPreRecreateCollection(PreCollectionRecreateEvent arg0) {
		LogMan.trace("PRE_RECREATE_COLLECTION");
	}

	@Override
	public void onPreRemoveCollection(PreCollectionRemoveEvent arg0) {
		LogMan.trace("PRE_REMOVE_COLLECTION");
	}

	/**
	 * Allows interception of mutations to collection properties. These are then
	 * translated into mutations of many-to-many tables or one-to-many tables by the {@link ore.event.EventManager} 
	 * It uses an inefficent approach of comparing the old elements of the collection to the
	 * new elements of the collection, in order to determine what has been added or removed from 
	 * the collection. Hibernate does not seem to provide a fine-grained way to directly determine what is
	 * added or removed. 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onPreUpdateCollection(PreCollectionUpdateEvent arg0) {
		try {
			LogMan.trace("PRE_UPDATE_COLLECTION");
			Object snapShot = arg0.getCollection().getStoredSnapshot();
			Object value = arg0.getCollection().getValue();
			if((snapShot instanceof Map) && (value instanceof Set)) {
				Set shot = ((Map)snapShot).keySet();
				Set copy = new HashSet();
				copy.addAll((Set)value);
				copy.removeAll(shot);
				for(Object newValue : copy) {
					String[] split = arg0.getCollection().getRole().split("\\.");
					String propertyName = split[split.length-1];
					EventManager.getInstance().collectionElementAdded("", arg0.getAffectedOwnerOrNull(), propertyName, newValue);
				}

				shot = (Set) value;
				copy = new HashSet();
				copy.addAll(((Map)snapShot).keySet());
				copy.removeAll(shot);
				for(Object newValue : copy) {
					String[] split = arg0.getCollection().getRole().split("\\.");
					String propertyName = split[split.length-1];
					EventManager.getInstance().collectionElementRemoved(arg0.getAffectedOwnerOrNull(), propertyName, newValue);
				}
			} else if((snapShot instanceof List) && (value instanceof List)) {
				List shot = (List) snapShot;
				List copy = new LinkedList();
				copy.addAll((List) value);
				copy.removeAll(shot);
				for(Object newValue : copy) {
					String[] split = arg0.getCollection().getRole().split("\\.");
					String propertyName = split[split.length-1];
					EventManager.getInstance().collectionElementAdded("", arg0.getAffectedOwnerOrNull(), propertyName, newValue);
				}

				shot = (List) value;
				copy = new LinkedList();
				copy.addAll((List) snapShot);
				copy.removeAll(shot);
				for(Object newValue : copy) {
					String[] split = arg0.getCollection().getRole().split("\\.");
					String propertyName = split[split.length-1];
					EventManager.getInstance().collectionElementRemoved(arg0.getAffectedOwnerOrNull(), propertyName, newValue);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onPreDelete(PreDeleteEvent arg0) {
		LogMan.trace("PRE_DELETE");
		return true;
	}

	@Override
	public boolean onPreInsert(PreInsertEvent arg0) {
		LogMan.trace("PRE_INSERT");
		return true;
	}

	@Override
	public void onPreLoad(PreLoadEvent arg0) {
		LogMan.trace("PRE_LOAD");
	}

	@Override
	public boolean onPreUpdate(PreUpdateEvent arg0) {
		LogMan.trace("PRE_UPDATE");
		return true;
	}

	@Override
	public void onRefresh(RefreshEvent arg0) throws HibernateException {
		LogMan.trace("REFRESH");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onRefresh(RefreshEvent arg0, Map arg1)
			throws HibernateException {
		LogMan.trace("REFRESH");
	}

	@Override
	public void onReplicate(ReplicateEvent arg0) throws HibernateException {
		LogMan.trace("REPLICATE");
	}

	@Override
	public void onSaveOrUpdate(SaveOrUpdateEvent arg0)
			throws HibernateException {
		LogMan.trace("SAVE_OR_UPDATE: " + arg0.getEntityName());
	}

}
