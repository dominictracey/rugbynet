package net.rugby.foundation.core.server.factory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.model.shared.IRatingQuery;

public abstract class BaseRatingQueryFactory extends BaseCachingFactory<IRatingQuery> implements IRatingQueryFactory {
	
	private final String prefix = "RQ-Mid";
	private IRatingSeriesFactory rsf;
	
	public BaseRatingQueryFactory(IRatingSeriesFactory rsf) {
		this.rsf = rsf;
	}
	
	
	@Override
	public List<IRatingQuery> getForMatrix(Long matrixId)
	{
		try {
			List<IRatingQuery> list = null;
	
			list = getList(getCacheId(matrixId));
			

			if (list == null) {
				list = getForMatrixFromPersistentDatastore(matrixId);

				if (list != null) {
					putList(getCacheId(matrixId), list);
				}	else {
					return null;
				}
			} 
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getForMatrix" + ex.getMessage(), ex);
			return null;
		}

	}

	protected abstract List<IRatingQuery> getForMatrixFromPersistentDatastore(Long matrixId);
	
	private String getCacheId(Long id) {
		return prefix + id.toString();
	}
	
	@Override
	public IRatingQuery put(IRatingQuery t) {
		super.put(t);
		
		// force upstream memcache update
		if (t.getRatingMatrix() != null && t.getRatingMatrix().getRatingGroup() != null && t.getRatingMatrix().getRatingGroup().getRatingSeriesId() != null) {
			rsf.dropFromCache( t.getRatingMatrix().getRatingGroup().getRatingSeriesId());
			//t.setRatingMatrix(rmf.get(t.getRatingMatrixId()));
		}
		
		return t;
	}
}
