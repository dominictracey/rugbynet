package net.rugby.foundation.core.server.factory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.rugby.foundation.model.shared.IStanding;

public abstract class BaseStandingFactory extends BaseCachingFactory<IStanding> implements IStandingFactory {
	
	private final String prefix = "STAND-";

	
	public BaseStandingFactory() {

	}
	
	@Override
	public List<IStanding> getLatestForComp(Long compId)
	{
		try {
			List<IStanding> list = null;
	
			list = getList(getCacheId(compId));
			

			if (list == null) {
				list = getLatestForCompFromPersistentDatastore(compId);

				if (list != null) {
					putList(getCacheId(compId), list);
				} else {
					return null;
				}
			} 
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getForMatrix" + ex.getMessage(), ex);
			return null;
		}

	}

	protected abstract List<IStanding> getLatestForCompFromPersistentDatastore(Long compId);
	
	private String getCacheId(Long id) {
		return prefix + id.toString();
	}
	
	@Override
	public IStanding put(IStanding t) {
		super.put(t);
		
		// invalidate cached version
		if (t != null && t.getRound() != null) {
			Long compId = t.getRound().getCompId();
			dropFromCache(getCacheId(compId));
		}
		
		return t;
	}
	
}
