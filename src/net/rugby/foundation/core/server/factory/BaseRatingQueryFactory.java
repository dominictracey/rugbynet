package net.rugby.foundation.core.server.factory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.rugby.foundation.model.shared.IRatingQuery;

public abstract class BaseRatingQueryFactory extends BaseCachingFactory<IRatingQuery> implements IRatingQueryFactory {
	
	private final String prefix = "RQ-Mid";
	private IRatingSeriesFactory rsf;
	private IRatingGroupFactory rgf;
	private IRatingMatrixFactory rmf;
	
	public BaseRatingQueryFactory(IRatingSeriesFactory rsf, IRatingGroupFactory rgf, IRatingMatrixFactory rmf) {
		this.rsf = rsf;
		this.rgf = rgf; 
		this.rmf = rmf;
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
	
	@Override
	public IRatingQuery buildUplinksForQuery(IRatingQuery rq) {
		// need to set the upside chain
		if (rq != null && rq.getRatingMatrixId() != null && rq.getRatingMatrix() == null) {
			rq.setRatingMatrix(rmf.get(rq.getRatingMatrixId()));
			if (rq.getRatingMatrix() != null && rq.getRatingMatrix().getRatingGroupId() != null && rq.getRatingMatrix().getRatingGroup() == null) {
				rq.getRatingMatrix().setRatingGroup(rgf.get(rq.getRatingMatrix().getRatingGroupId()));
				if (rq.getRatingMatrix().getRatingGroup() != null && rq.getRatingMatrix().getRatingGroup().getRatingSeriesId() != null && rq.getRatingMatrix().getRatingGroup().getRatingSeries() == null) {
					rq.getRatingMatrix().getRatingGroup().setRatingSeries(rsf.get(rq.getRatingMatrix().getRatingGroup().getRatingSeriesId()));
				}
			}
		}
		
		return rq;
	}
}
