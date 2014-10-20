package net.rugby.foundation.core.server.factory;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.model.shared.IRatingMatrix;

public abstract class BaseRatingMatrixFactory extends BaseCachingFactory<IRatingMatrix> implements IRatingMatrixFactory {
	
	private IRatingSeriesFactory rsf;
	protected IRatingQueryFactory rqf;
	
	public BaseRatingMatrixFactory(IRatingSeriesFactory rsf, IRatingQueryFactory rqf) {
		this.rsf = rsf;
		this.rqf = rqf;
	}
	
	protected IRatingMatrix build(IRatingMatrix t) {
		try {
			if (t.getRatingQueryIds() != null && !t.getRatingQueryIds().isEmpty()) {
				assert (t.getRatingQueries() != null);
				if (!t.getRatingQueries().isEmpty()) {
					t.getRatingQueries().clear();
				}
				for (Long rqid : t.getRatingQueryIds()){
					// this will populate down to the RatingQuery
					t.getRatingQueries().add(rqf.get(rqid));
				}
			}

			return t;
			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "build" + ex.getMessage(), ex);
			return null;
		}
	}
	
	@Override
	public IRatingMatrix put(IRatingMatrix t) {
		super.put(t);
		
		// force upstream memcache update
		if (t.getRatingGroup() != null && t.getRatingGroup().getRatingSeriesId() != null) {
			rsf.dropFromCache(t.getRatingGroup().getRatingSeriesId());
			//t.setRatingGroup(rgf.get(t.getRatingGroupId()));
		}
		
		return t;
	}
}
