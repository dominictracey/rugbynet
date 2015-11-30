package net.rugby.foundation.core.server.factory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import net.rugby.foundation.model.shared.IHasId;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.ITeamGroup;

public abstract class BaseTeamFactory extends BaseCachingFactory<ITeamGroup> implements ITeamGroupFactory {
	
	private final String prefix = this.getClass().toString();
	
	public HashMap<Long, String> getTeamLogoStyleMap() {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			HashMap<Long, String> mr = null;

			value = (byte[])syncCache.get(getCacheId());
			if (value == null) {
				mr = getTeamLogoStyleMapFromPersistentDatastore();

				if (mr != null) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutput out = new ObjectOutputStream(bos);   
					out.writeObject(mr);
					byte[] yourBytes = bos.toByteArray(); 

					out.close();
					bos.close();

					syncCache.put(getCacheId(), yourBytes);

				}
			} else {

				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in = new ObjectInputStream(bis);
				Object obj = in.readObject();
				if (obj != null) {  
					mr = (HashMap<Long, String>)obj;
				}

				bis.close();
				in.close();

			}
			return mr;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"getTeamLogoStyleMap", ex);
			return null;
		}

	}
	
	
	protected abstract HashMap<Long, String> getTeamLogoStyleMapFromPersistentDatastore();
	
	private String getCacheId() {
		return prefix + "LogoStyleMap";
	}
}
