package net.rugby.foundation.core.server.factory;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingSeries;

public abstract class BaseRatingGroupFactory extends BaseCachingFactory<IRatingGroup> implements IRatingGroupFactory {
	
	protected IRatingSeriesFactory rsf;
	protected IRatingMatrixFactory rmf;
	protected IUniversalRoundFactory urf;
	
	public BaseRatingGroupFactory(IRatingSeriesFactory rsf, IRatingMatrixFactory rmf, IUniversalRoundFactory urf) {
		this.rsf = rsf;
		this.rmf = rmf;
		this.urf = urf;
	}
	
	protected IRatingGroup build(IRatingGroup t) {
		try {
			if (t.getRatingMatrixIds() != null && !t.getRatingMatrixIds().isEmpty()) {
				assert (t.getRatingMatrices() != null);
				if (!t.getRatingMatrices().isEmpty()) {
					t.getRatingMatrices().clear();
				}
				for (Long rgid : t.getRatingMatrixIds()){
					// this will populate down to the RatingQuery
					t.getRatingMatrices().add(rmf.get(rgid));
				}
			}
			
			t.setUniversalRound(urf.get(t.getUniversalRoundOrdinal()));
			t.setLabel(t.getUniversalRound().abbr);
			return t;
			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "build" + ex.getMessage(), ex);
			return null;
		}
	}
	
	@Override
	public IRatingGroup put(IRatingGroup t) {
		super.put(t);
		
		// force upstream memcache update
		if (t.getRatingSeriesId() != null) {
			rsf.dropFromCache(t.getRatingSeriesId());
			IRatingSeries s = rsf.get(t.getRatingSeriesId());
			//t.setRatingSeries(rsf.build(s));
		}
		
		return t;
	}
}
