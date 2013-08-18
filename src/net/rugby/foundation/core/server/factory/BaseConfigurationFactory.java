package net.rugby.foundation.core.server.factory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import net.rugby.foundation.model.shared.ICoreConfiguration;


public abstract class BaseConfigurationFactory implements IConfigurationFactory {

	private final String memcacheKey = "CoreConfig";
	@Override
	public ICoreConfiguration get() {
		// handle the memcache in here and allow the fetch from the persistent stores to be implemented in the subclasses
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			ICoreConfiguration mr = null;

			value = (byte[])syncCache.get(memcacheKey);
			if (value == null) {
				mr = getFromPersistentDatastore();

				if (mr != null) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutput out = new ObjectOutputStream(bos);   
					out.writeObject(mr);
					byte[] yourBytes = bos.toByteArray(); 

					out.close();
					bos.close();

					syncCache.put(memcacheKey, yourBytes);
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,"** getting config (and putting in memcache)" + mr.getId() + " *** \n" + syncCache.getStatistics());

				}
			} else {

				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				mr = (ICoreConfiguration)in.readObject();

				bis.close();
				in.close();

			}
			return mr;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	abstract protected ICoreConfiguration getFromPersistentDatastore();
	
	@Override
	public ICoreConfiguration put(ICoreConfiguration config) {
		// Allow subclasses to put it in peristent data stores, then put in memcache
		try {
			config = putToPersistentDatastore(config);

			// now update the memcache version
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			syncCache.delete(memcacheKey);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(config);
			byte[] yourBytes = bos.toByteArray(); 

			out.close();
			bos.close();

			syncCache.put(memcacheKey, yourBytes);
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,"** putting config " + config.getId() + " *** \n" + syncCache.getStatistics());
					
			return config;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}	}

	abstract protected ICoreConfiguration putToPersistentDatastore(ICoreConfiguration config);

}
