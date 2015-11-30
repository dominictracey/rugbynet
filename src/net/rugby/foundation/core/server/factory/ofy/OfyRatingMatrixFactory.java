package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.BaseRatingMatrixFactory;
import net.rugby.foundation.core.server.factory.IRatingMatrixFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.RatingMatrix;

public class OfyRatingMatrixFactory extends BaseRatingMatrixFactory implements IRatingMatrixFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -63026632234487370L;


	@Inject
	OfyRatingMatrixFactory(IRatingSeriesFactory rsf, IRatingQueryFactory rqf) {
		super(rsf, rqf);

	}

	@Override
	protected IRatingMatrix getFromPersistentDatastore(Long id) {
		if (id != null) {
			Objectify ofy = DataStoreFactory.getOfy();
			IRatingMatrix retval = ofy.get(new Key<RatingMatrix>(RatingMatrix.class,id));
//			for (Long rqid : retval.getRatingQueryIds()) {
//				retval.getRatingQueries().add(rqf.get(rqid));
//			}
//			
//			// how to generically solve this re-entrant code problem?
//			//retval.setRatingGroup(rgf.get(retval.getRatingGroupId()));
			
			return build(retval);
		} else
			return null;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IRatingMatrixFactory#put(net.rugby.foundation.model.shared.IRatingMatrix)
	 */
	@Override
	protected IRatingMatrix putToPersistentDatastore(IRatingMatrix g) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();

			// TODO don't need instanceof here?
			ofy.put((RatingMatrix)g);

			return g;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}
	@Override
	public boolean deleteFromPersistentDatastore(IRatingMatrix r) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			for (Long rqid : r.getRatingQueryIds()) {
				IRatingQuery rq = rqf.get(rqid);
				if (rq != null) {
					rqf.delete(rq);
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
	public IRatingMatrix create() {
		return new RatingMatrix();
	}

	@Override
	public List<IRatingMatrix> getForRatingGroup(Long ratingGroupId) {
		Objectify ofy = DataStoreFactory.getOfy();
		List<IRatingMatrix> matrices = new ArrayList<IRatingMatrix>();
		Query<RatingMatrix> qrm = ofy.query(RatingMatrix.class).filter("ratingGroupId", ratingGroupId);
		matrices.addAll(qrm.list());
		return null;
	}


}
