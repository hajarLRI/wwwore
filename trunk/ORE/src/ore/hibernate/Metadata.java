package ore.hibernate;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;

import ore.api.HibernateUtil;
import ore.event.EventManager;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.property.Getter;

/** 
 * Provides easy access to the metadata contained in the hibernate entity mapping
 * files (e.g. User.hbm.xml)
 * <br/> (package-protected)
 * 
 * @see EventManager
 */
public class Metadata {
	
	private static Configuration cfg; 
	
	private static Configuration getConfiguration() {
		if(cfg == null) {
			cfg = HibernateUtil.getConfiguration();
		}
		
		return cfg;
	}
	
	public static String getSingleColumnKey(Iterator<Column> columns) {
		String columnName = null;
		int i = 0;
		while(columns.hasNext()) {
			i++;
			if(i > 1) {
				throw new UnsupportedOperationException("Mapping property with more than one column");
			}
			Column column = columns.next();
			columnName = column.getName();
		}
		if(columnName == null) {
			throw new UnsupportedOperationException("Mapping property with zero column");
		}
		return columnName;
	}
	
	@SuppressWarnings("unchecked")
	public static String getSingleColumnKey(Property prop) {
		return getSingleColumnKey(prop.getColumnIterator());
	}
	
	@SuppressWarnings("unchecked")
	public static String getSingleColumnKey(KeyValue key) {
		return getSingleColumnKey(key.getColumnIterator());
	}
	
	public static Serializable getPrimaryKeyValue(Object entity) {
		Configuration cfg = getConfiguration();
		PersistentClass pc = cfg.getClassMapping(entity.getClass().getName());
		String keyName = Metadata.getSingleColumnKey(pc.getKey());
		Property property = pc.getProperty(keyName);
		Getter getter = property.getGetter(entity.getClass());
		return (Serializable) getter.get(entity);
	}
	
	public static Object getPropertyValue(Object entity, String property) {
		Class<?> entityClass = entity.getClass();
		String getterName = "get" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
		Object retVal = null;
		try {
			java.lang.reflect.Method getter = entityClass.getMethod(getterName, null);
			retVal = getter.invoke(entity, null); 
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return retVal;
	}
	
	public static Collection getCollectionMetadata(String roleName) {
		return getConfiguration().getCollectionMapping(roleName);
	}
	
	public static String getManyToManyTableName(String roleName) {
		Collection collection = getConfiguration().getCollectionMapping(roleName);
		return collection.getCollectionTable().getName();
	}
	
	public static boolean isOneToMany(String roleName) {
		Collection collection = getConfiguration().getCollectionMapping(roleName);
		return collection.isOneToMany();
	}
	
	public static String getEntityTableName(Class<?> clazz) {
		return getConfiguration().getClassMapping(clazz.getName()).getTable().getName();
	}
}
