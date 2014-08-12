/**
 * 
 */
package net.rugby.foundation.core.server.factory.test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.BaseRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.RatingQuery;
import net.rugby.foundation.model.shared.Position.position;

/**
 * @author home
 *
 */
public class TestRatingQueryFactory extends BaseRatingQueryFactory implements IRatingQueryFactory {

	private Long count = 750L;
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IRatingQueryFactory#get()
	 */
	@Override
	public IRatingQuery getFromPersistentDatastore(Long id) {
		if (id == null) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Don't try to get with null. Call create() instead!");
			return null;
		} else {
			IRatingQuery rq = new RatingQuery();
			rq.setScaleComp(true);
			rq.setScaleStanding(true);
			rq.setScaleTime(true);
			rq.setId(id);
			if (id >= 700L && id <= 705L) {
				if (id == 700L) { // round 1
					rq.getCompIds().add(1L);
					rq.getRoundIds().add(6L);
				} else if (id == 701L) {  // players FROM New Zealand
					rq.getCompIds().add(1L);
					rq.getRoundIds().add(6L);
					rq.getCountryIds().add(5001L);
				} else if (id == 702L) {  // flankers
					rq.getCompIds().add(1L);
					rq.getRoundIds().add(2L);
					rq.getPositions().add(position.FLANKER);
				}  else if (id == 703L) {  // players FOR New Zealand or Australia (teams)
					rq.getCompIds().add(1L);
					rq.getRoundIds().add(6L);
					rq.getTeamIds().add(1L);
					rq.getTeamIds().add(2L);
				} else if (id == 704L) { // time series - round 1 and round 2
					rq.getRoundIds().add(6L);
					rq.getRoundIds().add(7L);
				} else if (id == 705L) { // time series - round 1 and round 2 - unscaled
					rq.getRoundIds().add(6L);
					rq.getRoundIds().add(7L);
					rq.setScaleComp(true);
					rq.setScaleStanding(true);
					rq.setScaleTime(true);
				}
			} else if (id > 7710000L && id < 7710100L) {
				rq.getCompIds().add(1L);
				rq.getRoundIds().add(7L);
				rq.getRoundIds().add(6L);
				rq.getPositions().add(position.getAt((int)((id-7710000))));
				rq.setScaleTime(false);
			} else if (id > 7710100L && id < 7710111L) {
				rq.getCompIds().add(1L);
				rq.getRoundIds().add(7L);
				rq.getRoundIds().add(6L);
				rq.getPositions().add(position.getAt((int)((id-7710100))));
			} else if (id > 7700000L && id < 7700100L) {
				rq.getCompIds().add(1L);
				rq.getRoundIds().add(6L);
				rq.setScaleTime(false);
				rq.getPositions().add(position.getAt((int)((id-7700000))));
			} else if (id > 7700100L && id < 7700111L) {
				rq.getCompIds().add(1L);
				rq.getRoundIds().add(6L);
				rq.getPositions().add(position.getAt((int)((id-7700100))));
			}
			rq.setRatingMatrixId(id / 100);
			rq.setTopTenListId(id + 10000000L);
			return rq;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IRatingQueryFactory#put(net.rugby.foundation.model.shared.IRatingQuery)
	 */
	@Override
	public IRatingQuery putToPersistentDatastore(IRatingQuery rq) {
		if (rq.getId() == null) {
			rq.setId(count++);
		}
		return rq;
	}

	@Override
	public IRatingQuery create() {
		return new RatingQuery();
	}

	@Override
	protected boolean deleteFromPersistentDatastore(IRatingQuery t) {
		return true;
	}

	@Override
	public IRatingQuery query(List<Long> compIds, List<Long> roundIds,
			List<position> posis, List<Long> countryIds, List<Long> teamIds) {
		IRatingQuery rq = new RatingQuery();
		rq.setScaleComp(false);
		rq.setScaleStanding(false);
		rq.setScaleTime(false);
		rq.setId(count++);
		rq.getCompIds().addAll(compIds);
		rq.getRoundIds().addAll(roundIds);
		rq.getPositions().addAll(posis);
		rq.getCountryIds().addAll(countryIds);
		rq.getTeamIds().addAll(teamIds);
		
		return rq;
//		if (compIds.contains(1L)) {
//			if (roundIds.contains(2L)) {
//				if (posis != null && posis.contains(position.FLANKER)) {
//					return getFromPersistentDatastore(702L);
//				} else if (countryIds != null && countryIds.contains(5001L)) {
//					return getFromPersistentDatastore(701L);
//				} else if (teamIds != null && (teamIds.contains(1L) || teamIds.contains(2L))) {
//					return getFromPersistentDatastore(703L);
//				} else {
//					if ((posis == null && countryIds == null && teamIds == null) || (posis.isEmpty() && countryIds.isEmpty() && teamIds.isEmpty())) {
//						return getFromPersistentDatastore(700L);
//					}
//				}
//			}
//		}
//		return null;
	}

	@Override
	public void deleteAll() {
		return;
		
	}

	@Override
	public List<IRatingQuery> getForMatrixFromPersistentDatastore(Long ratingMatrixId) {
		List<IRatingQuery> list = new ArrayList<IRatingQuery>();
		for (Long i=ratingMatrixId*100+1; i<ratingMatrixId*100+11; ++i ) {
			list.add(get(i));
		} 
			
		return list;
	}

}

