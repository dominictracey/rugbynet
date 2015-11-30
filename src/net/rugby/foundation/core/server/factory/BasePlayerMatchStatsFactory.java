package net.rugby.foundation.core.server.factory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IRatingQuery;

public abstract class BasePlayerMatchStatsFactory extends BaseCachingFactory<IPlayerMatchStats> implements IPlayerMatchStatsFactory {
	
	private final String prefix = "PMS-Mid";
	private final String rQprefix = "PMS-RQid";
	@Override
	public List<IPlayerMatchStats> getByMatchId(Long matchId)
	{
		try {
			List<IPlayerMatchStats> list = null;
	
			list = getList(getCacheId(matchId));
			

			if (list == null) {
				list = getFromPersistentDatastoreByMatchId(matchId);

				if (list != null) {
					putList(getCacheId(matchId), list);
				}	else {
					return null;
				}
			} 
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "GetByMatchId" + ex.getMessage(), ex);
			return null;
		}

	}
	
	protected void flushByMatchId(Long matchId)
	{
		try {
			super.deleteItemFromMemcache(getCacheId(matchId));
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "FlushByMatchId" + ex.getMessage(), ex);
		}
	}
	
	@Override
	public List<IPlayerMatchStats> query(IRatingQuery rq)
	{
		try {
			List<IPlayerMatchStats> list = null;
	
			list = getList(getRQCacheId(rq.getId()));
			

			if (list == null) {
				list = queryFromPersistentDatastore(rq);

				if (list != null) {
					putList(getRQCacheId(rq.getId()), list);
				}	else {
					return null;
				}
			} 
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "GetByMatchId" + ex.getMessage(), ex);
			return null;
		}

	}
	
	private String getRQCacheId(Long id) {
		return rQprefix + id.toString();
	}

	protected abstract List<IPlayerMatchStats> getFromPersistentDatastoreByMatchId(Long id);
	protected abstract List<IPlayerMatchStats> queryFromPersistentDatastore(IRatingQuery rq);
	
	private String getCacheId(Long id) {
		return prefix + id.toString();
	}
}
