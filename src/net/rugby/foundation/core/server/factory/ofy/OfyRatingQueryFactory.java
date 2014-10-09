package net.rugby.foundation.core.server.factory.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.inject.Inject;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.BaseRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingQuery.Status;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.RatingQuery;

public class OfyRatingQueryFactory extends BaseRatingQueryFactory implements IRatingQueryFactory {

	private IPlayerRatingFactory prf;

	@Inject 
	public OfyRatingQueryFactory(IPlayerRatingFactory pmrf) {
		this.prf = pmrf;
	}
	
	@Override
	public IRatingQuery create() {
		try {
			IRatingQuery rq = new RatingQuery();
			rq.setStatus(Status.NEW);
			return rq;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected IRatingQuery getFromPersistentDatastore(Long id) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			if (id != null) {
				IRatingQuery rq = ofy.get(new Key<RatingQuery>(RatingQuery.class,id));
				return rq;
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Don't try to get with null. Call create() instead!");
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected IRatingQuery putToPersistentDatastore(IRatingQuery rq) {
		try {
			if (rq != null) {
				Objectify ofy = DataStoreFactory.getOfy();
				ofy.put(rq);
				return rq;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected boolean deleteFromPersistentDatastore(IRatingQuery rq) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.delete(rq);
			
			// also delete all the PMRs associated with this query
			prf.deleteForQuery(rq);
			return true;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
	}

	@Deprecated
	@Override
	public IRatingQuery query(List<Long> compIds, List<Long> roundIds, List<position> posis, List<Long> countryIds, List<Long> teamIds) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			// confirm list is in ascending order
			Query<RatingQuery> qpms = ofy.query(RatingQuery.class);
			if (confirmAscending(compIds)) {
				if (compIds != null && !compIds.isEmpty()) {
					qpms.filter("compIds",compIds);
				}
			}	

			if (confirmAscendingPos(posis)) {
				if (posis != null && !posis.isEmpty()) {
					qpms = qpms.filter("positions",posis);
				}
			}

			if (confirmAscending(countryIds)) {
				if (countryIds != null && !countryIds.isEmpty()) {
					qpms = qpms.filter("countryIds", countryIds);
				}
			}

			if (confirmAscending(teamIds)) {
				if (teamIds != null && !teamIds.isEmpty()) {
					qpms = qpms.filter("teamIds", teamIds);
				}
			}

			// we should have zero or one
			return qpms.get();
			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	private boolean confirmAscendingPos(List<position> posis) {
		position curr = position.NONE;
		for (position p : posis) {
			if (p.ordinal() <= curr.ordinal()) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "An out-of-order list of positions was passed to the RatingQueryFactory");
				return false;
			}
		}
		return true;
	}

	private boolean confirmAscending(List<Long> compIds) {

		Long curr = 0L;
		for (Long cid : compIds) {
			if (cid <= curr) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "An out-of-order list of Longs was passed to the RatingQueryFactory");
				return false;
			}
			curr = cid;
		}
		return true;
	}

	@Override
	public void deleteAll() {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			QueryResultIterable<Key<RatingQuery>> keys = ofy.query(RatingQuery.class).fetchKeys();
			ofy.delete(keys);

			// note the PMRs are left orphaned

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
		}	
		
	}

	@Override
	protected List<IRatingQuery> getForMatrixFromPersistentDatastore(
			Long matrixId) {
		Objectify ofy = DataStoreFactory.getOfy();
		//@TODO confirm list is in ascending order?
		Query<RatingQuery> qpms = ofy.query(RatingQuery.class).filter("matrixId", matrixId);
		
		List<IRatingQuery> list = new ArrayList<IRatingQuery>();
		for (RatingQuery rq : qpms.list()) {
			list.add(rq);
		}
		
		return list;
	}


}
