package net.rugby.foundation.core.server.factory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.model.shared.IHasId;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;


public abstract class BaseCachingFactory<T extends IHasId> implements ICachingFactory<T> {

	@Override
	public T get(Long id) {
		// handle the memcache in here and allow the fetch from the persistent stores to be implemented in the subclasses
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			T mr = null;

			value = (byte[])syncCache.get(id);
			if (value == null) {
				mr = getFromPersistentDatastore(id);

				if (mr != null) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutput out = new ObjectOutputStream(bos);   
					out.writeObject(mr);
					byte[] yourBytes = bos.toByteArray(); 

					out.close();
					bos.close();

					if (yourBytes.length < 1048000) {
						syncCache.put(id, yourBytes);
					} else {
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"** attempt to store oversize object: " + mr.getId() + " of type " + mr.getClass().getCanonicalName() + " *** \n" + syncCache.getStatistics());
					}
				}
			} else {

				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				Object obj = in.readObject();
				if (obj instanceof IHasId) {  // can't do 'obj instanceof T' *sadfase*
					mr = (T)obj;
				}

				bis.close();
				in.close();

			}
			return mr;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	abstract protected T getFromPersistentDatastore(Long id);

	@Override
	public T put(T t) {
		// Allow subclasses to put it in peristent data stores, then put in memcache
		try {
			t = putToPersistentDatastore(t);

			// now update the memcache version
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			if (syncCache.contains(t.getId())) {
				syncCache.delete(t.getId());
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(t);
			byte[] yourBytes = bos.toByteArray(); 

			out.close();
			bos.close();

			syncCache.put(t.getId(), yourBytes);
			//Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,"** putting object " + t.getId() + " *** \n" + syncCache.getStatistics());

			return t;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}	

	}

	abstract protected T putToPersistentDatastore(T t);

	@Override
	public boolean delete(T t) {
		try {
			deleteFromMemcache(t);
			return deleteFromPersistentDatastore(t);
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}	
	}

	protected void deleteFromMemcache(T t) {
		try {
			if (t != null) {
				MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
				syncCache.delete(t.getId());
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	protected void deleteItemFromMemcache(String s) {
		try {
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			syncCache.delete(s);
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	abstract protected boolean deleteFromPersistentDatastore(T t);

	/**
	 * 
	 * @param key - memcache key to look for
	 * @return the list of items stored for the subclass
	 */
	protected List<T> getList(String key) {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			List<T> mr = null;

			value = (byte[])syncCache.get(key);
			if (value == null) {
				return null;
			} else {

				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				Object obj = in.readObject();

				//				if (typeLiteral.equals(obj.getClass())) {  // can't do 'obj instanceof List<T>' *sadfase*
				mr = (List<T>)obj;


				bis.close();
				in.close();

			}
			return mr;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * 
	 * @param key - memcache key to store (consider using this.getClass().toString() + "descriptor")
	 * @param list - List<T> to store or null to clear entry
	 * @return true if successful storing
	 */
	protected boolean putList(String key, List<T> list) {
		try {
			Serializable sList = (Serializable) list;

			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			syncCache.delete(key);
			if (sList != null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(sList);
				byte[] yourBytes = bos.toByteArray(); 

				out.close();
				bos.close();

				syncCache.put(key, yourBytes);
				//Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,"** putting list at " + key + " *** \n" + syncCache.getStatistics());
			}
			return true;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
	}

	public T getItem(String divKey) {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			T mr = null;

			value = (byte[])syncCache.get(divKey);
			if (value == null) {
				return null;
			} else {

				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				Object obj = in.readObject();

				//				if (typeLiteral.equals(obj.getClass())) {  // can't do 'obj instanceof T' *sadfase*
				mr = (T)obj;


				bis.close();
				in.close();

			}
			return mr;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}

	}

	/**
	 * 
	 * @param divKey - key to store 
	 * @param content - value to store or null to delete key
	 * @return false on exception caught
	 */
	public boolean putItem(Object divKey, T content) {
		try {
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			syncCache.delete(divKey);
			if (content != null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(content);
				byte[] yourBytes = bos.toByteArray(); 

				out.close();
				bos.close();

				syncCache.put(divKey, yourBytes);
				//Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,"** putting list at " + divKey + " *** \n" + syncCache.getStatistics());
			}
			return true;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}	
	}

	/**
	 * 
	 */
	public void dropFromCache(Long id) {
		try {
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			//T t = get(id);
			if (syncCache.contains(id)) {
				syncCache.delete(id);
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
		}	
	}
	
	/**
	 * 
	 */
	public void dropFromCache(String id) {
		try {
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();

			if (syncCache.contains(id)) {
				syncCache.delete(id);
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
		}	
	}
	
}