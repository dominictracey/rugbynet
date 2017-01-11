package net.rugby.foundation.core.server.factory;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.rugby.foundation.model.shared.IVenue;

public abstract class BaseVenueFactory extends BaseCachingFactory<IVenue> implements IVenueFactory {

	private final String prefix = this.getClass().toString();
	
	public IVenue getByVenueName(String venueName){
		try {
			IVenue v = null;
			
			v = getItem(getCacheVenueName(venueName));
			
			if(v == null){
				v = getFromPersistentDatastoreByVenueName(venueName);
				
				if(v != null){
					putItem(getCacheVenueName(venueName), v);
				}
				else{
					return null;
				}
			}
			return v;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "GetByVenueName" + e.getMessage(), e);
			return null;
		}
	}
	
	protected abstract IVenue getFromPersistentDatastoreByVenueName(String venueName);
	
	protected String getCacheId(Long id) {
		return prefix + id.toString();
	}
	protected String getCacheVenueName(String venueName) {
		return prefix + venueName;
	}
}
