package net.rugby.foundation.core.server.factory;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.model.shared.IPlayer;

public abstract class BasePlayerFactory extends BaseCachingFactory<IPlayer> implements IPlayerFactory {
	
	private final String prefix = this.getClass().toString();
	
	public IPlayer getByScrumId(Long id) {
		try {
			IPlayer p = null;
	
			p = getItem(getScrumCacheId(id));
			

			if (p == null) {
				p = getFromPersistentDatastoreByScrumId(id);

				if (p != null) {
					putItem(getScrumCacheId(id), p);
				}	else {
					return null;
				}
			} 
			return p;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "GetByScrumId" + ex.getMessage(), ex);
			return null;
		}

	}
	
	
	protected abstract IPlayer getFromPersistentDatastoreByScrumId(Long id);
	
	private String getScrumCacheId(Long id) {
		return prefix + id.toString();
	}
}
