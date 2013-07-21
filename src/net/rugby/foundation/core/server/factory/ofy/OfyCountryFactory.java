package net.rugby.foundation.core.server.factory.ofy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.model.shared.Country;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.ICountry;
import java.io.Serializable;

public class OfyCountryFactory implements ICountryFactory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7571451805671318324L;
	//private final Objectify ofy;
	private static String memCachePrefix="C001-";
	public OfyCountryFactory() {
		//this.ofy = DataStoreFactory.getOfy();
	}
	
	@Override
	public ICountry getById(Long id) {
		try {
			
			if (id == null) {
				return new Country(); // put(null) actually saves it, which we don't want really?
			}
			
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			ICountry c = null;
	
			value = (byte[])syncCache.get(memCachePrefix + id);
			if (value == null) {
				c = getFromDB(id);

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(c);
				byte[] yourBytes = bos.toByteArray(); 
	
				out.close();
				bos.close();
	
				syncCache.put(memCachePrefix + id, yourBytes);
			} else {
	
				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				c = (ICountry)in.readObject();
	
				bis.close();
				in.close();
	
			}
			return c;
	
		} catch (Throwable ex) {
			Logger.getLogger("Core Service OfyCountryFactory.getById").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
		
	}

	@Override
	public ICountry getByName(String name) {
		try {
			
			if (name == null) {
				return new Country(); // put(null) actually saves it, which we don't want really?
			}
			
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			ICountry c = null;
	
			value = (byte[])syncCache.get(memCachePrefix + name);
			if (value == null) {
				c = getFromDB(name);

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(c);
				byte[] yourBytes = bos.toByteArray(); 
	
				out.close();
				bos.close();
	
				syncCache.put(memCachePrefix + name, yourBytes);
			} else {
	
				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				c = (ICountry)in.readObject();
	
				bis.close();
				in.close();
	
			}
			return c;
	
		} catch (Throwable ex) {
			Logger.getLogger("Core Service OfyCountryFactory.getById").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public ICountry put(Country country) {
		try {
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(country);
	
			byte[] yourBytes = bos.toByteArray(); 
	
			out.close();
			bos.close();
			
			syncCache.put(memCachePrefix + country.getName(), yourBytes);
			syncCache.put(memCachePrefix + country.getId(), yourBytes);
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.put(country);
			return country;
		} catch (Throwable ex) {
			Logger.getLogger("Core Service OfyCountryFactory.getById").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}
	
	protected ICountry getFromDB(Long id) {
		if (id == null) {
			return new Country();
		}
		Objectify ofy = DataStoreFactory.getOfy();
		return ofy.get(new Key<Country>(Country.class,id));
	}
	
	protected ICountry getFromDB(String name) {
		if (name != null) {
			Objectify ofy = DataStoreFactory.getOfy();
			Query<Country> qsp = ofy.query(Country.class).filter("name", name);
			return qsp.get();
		} else {
			return new Country();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Country> getAll() {
		try {
			
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			Iterable<Country> c = null;
	
			value = (byte[])syncCache.get(memCachePrefix + "ALL");
			if (value == null) {
				c = getAllFromDB();
				
				List<Country> list = new ArrayList<Country>();
				Iterator<Country> it = c.iterator();
				while (it.hasNext()) {
					list.add(it.next());
				}

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(list);
				byte[] yourBytes = bos.toByteArray(); 
	
				out.close();
				bos.close();
	
				syncCache.put(memCachePrefix + "ALL", yourBytes);
			} else {
	
				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				c = (List<Country>)in.readObject();
	
				bis.close();
				in.close();
	
			}
			return c.iterator();
	
		} catch (Throwable ex) {
			Logger.getLogger("Core Service OfyCountryFactory.getById").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}

	}

	
	private QueryResultIterable<Country> getAllFromDB() {
		Objectify ofy = DataStoreFactory.getOfy();
		Query<Country> qsp = ofy.query(Country.class);
		if (qsp.count() > 0) {
			return qsp.fetch();
		} else {
			return null;
		}
	}

}
