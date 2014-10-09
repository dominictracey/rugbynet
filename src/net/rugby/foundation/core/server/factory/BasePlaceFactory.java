package net.rugby.foundation.core.server.factory;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.admin.server.util.Hashids;


public abstract class BasePlaceFactory extends BaseCachingFactory<IServerPlace> implements IPlaceFactory {

	private Hashids hashids = null;
	private final String SALT = "The Rugby Net Championship";
	@Override
	public IServerPlace getForGuid(String guid) {
		try {
			IServerPlace place = null;
	
			place = getItem(getGuidCacheId(guid));			

			if (place == null) {
				//place = getForGuidFromPersistentDatastore(guid);

				if (hashids == null) {
					hashids = new Hashids(SALT);
				}
				long[] ids = hashids.decode(guid);
				if (ids.length > 0) {
					place = get(ids[0]);
				}
				
				if (place != null) {
					putItem(getGuidCacheId(guid), place);
				}	else {
					return null;
				}
			} 
			return place;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getForGuid" + ex.getMessage(), ex);
			return null;
		}
	}
	
	private String getGuidCacheId(String guid) {
		return guidPrefix + guid;
	}

	private final String guidPrefix = "spfGUID-";
	
	
	protected abstract IServerPlace getForGuidFromPersistentDatastore(String guid);

	@Override
	public IServerPlace getForName(String name) {
		try {
			IServerPlace place = null;
	
			place = getItem(getNameCacheId(name));			

			if (place == null) {
				place = getForNameFromPersistentDatastore(name);

				if (place != null) {
					putItem(getNameCacheId(name), place);
				}	else {
					return null;
				}
			} 
			return place;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getForName" + ex.getMessage(), ex);
			return null;
		}
	}
	
	private final String namePrefix = "spfNAME-";

	private String getNameCacheId(String name) {
		return namePrefix + name;
	}

	protected abstract IServerPlace getForNameFromPersistentDatastore(String name);

	protected String generate(Long id) {
		try {
			
			if (hashids == null) {
				hashids = new Hashids(SALT);
			}
			String hash = hashids.encode(id);
			return hash;
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "generate" + e.getMessage(), e);
			return null;
		}	
	}
}
