package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.core.server.factory.BaseRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingMatrixFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.RatingGroup;

public class OfyRatingGroupFactory extends BaseRatingGroupFactory implements IRatingGroupFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -63026632234487370L;


	@Inject
	OfyRatingGroupFactory(IRatingMatrixFactory rmf, IUniversalRoundFactory urf, IRatingSeriesFactory rsf) {
		super(rsf, rmf, urf);
	}

	@Override
	protected IRatingGroup getFromPersistentDatastore(Long id) {
		if (id != null) {
			Objectify ofy = DataStoreFactory.getOfy();
			IRatingGroup retval = ofy.get(new Key<RatingGroup>(RatingGroup.class,id));
//			for (Long rmid : retval.getRatingMatrixIds()) {
//				IRatingMatrix rm = rmf.get(rmid);
//				rm.setRatingGroup(retval);
//				retval.getRatingMatrices().add(rm);
//			}
//			retval.setUniversalRound(urf.get(retval.getUniversalRoundOrdinal()));
//			retval.setLabel(retval.getUniversalRound().abbr);
			return build(retval);
		} else
			return null;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IRatingGroupFactory#put(net.rugby.foundation.model.shared.IRatingGroup)
	 */
	@Override
	protected IRatingGroup putToPersistentDatastore(IRatingGroup g) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();

			ofy.put((RatingGroup)g);

			return g;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}
	@Override
	public boolean deleteFromPersistentDatastore(IRatingGroup r) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			for (Long rid : r.getRatingMatrixIds()) {
				IRatingMatrix rm = rmf.get(rid);
				if (rm != null) {
					rmf.delete(rm);
				}
			}
			
			ofy.delete(r);
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in delete: " + ex.getLocalizedMessage());
			return false;
		}
		return true;
	}

	@Override
	public IRatingGroup create() {
		return new RatingGroup();
	}


}
