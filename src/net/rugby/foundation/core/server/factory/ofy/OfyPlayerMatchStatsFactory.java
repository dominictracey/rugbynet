package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.BasePlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IRawScoreFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ScrumPlayerMatchStats;

/**
 * 
 * @author DPT
 * @copyright RUGBY.NET LLC
 * 
 * Memcache strategy: We store PMS objects as themselves with their ids as keys. 
 * We also store a list of PMS ids (List<Long>) under the key "PMSIdsForMatch<matchid>".
 * This can then be used to pull the PMS objects from the memcache (or DB).
 * One implication is that when a PMS gets (re-)written, the match that it is culled from should
 * have its PMS id list dropped from the cache.
 *
 */
public class OfyPlayerMatchStatsFactory extends BasePlayerMatchStatsFactory implements IPlayerMatchStatsFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -175156134974074733L;

	private IRawScoreFactory rsf;

	private IRoundFactory rf;


	@Inject
	public OfyPlayerMatchStatsFactory(IRoundFactory rf, IRawScoreFactory rsf) {
		this.rf = rf;
		this.rsf = rsf;
	}

	@Override
	protected IPlayerMatchStats getFromPersistentDatastore(Long id) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			if (id != null)
				return ofy.get(new Key<ScrumPlayerMatchStats>(ScrumPlayerMatchStats.class,id));
			else
				return new ScrumPlayerMatchStats();
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getFromDB" + ex.getMessage(), ex);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory#put(net.rugby.foundation.model.shared.IPlayerMatchStats)
	 */
	@Override
	protected IPlayerMatchStats putToPersistentDatastore(IPlayerMatchStats pms) {
		try {
			if (pms == null) {
				return new ScrumPlayerMatchStats();
			}

			Objectify ofy = DataStoreFactory.getOfy();

			// only one per player per match
			ScrumPlayerMatchStats existing = ofy.query(ScrumPlayerMatchStats.class).filter("playerId", pms.getPlayerId()).filter("matchId", pms.getMatchId()).get();

			if (existing != null) {
				ofy.delete(existing);
			}

			ofy.put(pms);
			
			// clear the memcache for this pms's match
			super.flushByMatchId(pms.getMatchId());
			
			// @TODO need to also invalidate the memcache when ratingQueries have this PMS...
			// 	in reality, the PlayerRatings are wrong a well so everything needs to go there.
			
			return pms;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "put" + ex.getMessage(), ex);
			return null;
		}
	}

	public boolean deleteFromPersistentDatastore(IPlayerMatchStats val) {
		try {
			if (val != null) {
				// delete any dependencies first
				rsf.deleteForPMSid(val.getId());
				
				Objectify ofy = DataStoreFactory.getOfy();
				ofy.delete(val);
				
				return true;
			} else {
				return false;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "delete" + ex.getMessage(), ex);
			return false;
		}
	}


	@Override
	public boolean deleteForMatch(IMatchGroup m) {
		try {
			if (m != null) {
				Objectify ofy = DataStoreFactory.getOfy();

				List<IPlayerMatchStats> c = getByMatchId(m.getId());

				if (c != null) {
					ofy.delete(c);
				}

			} else {
				return false; // null match
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in delete: " + ex.getLocalizedMessage());
			return false;
		}
		return true;
	}

	@Override
	public IPlayerMatchStats create() {
		try {
			return new ScrumPlayerMatchStats();
			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in create: " + ex.getLocalizedMessage());
			return null;
		}
		
	}

	@Override
	protected List<IPlayerMatchStats> getFromPersistentDatastoreByMatchId(Long id) {
		try {
			List<IPlayerMatchStats> list = new ArrayList<IPlayerMatchStats>();
			
			Objectify ofy = DataStoreFactory.getOfy();
			// @REX should we just check the match - don't we need to also cross-check against comp as well?
			Query<ScrumPlayerMatchStats> qg = ofy.query(ScrumPlayerMatchStats.class).filter("matchId", id).order("teamId").order("slot");;

			Iterator<ScrumPlayerMatchStats> it = qg.list().iterator();
			while (it.hasNext()) {
				list.add((IPlayerMatchStats)it.next());
			}
			
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in getFromPersistentDatastoreByMatchId: " + ex.getLocalizedMessage());
			return null;
		}
	}
	
	@Override
	protected List<IPlayerMatchStats> queryFromPersistentDatastore(IRatingQuery rq) {
		Objectify ofy = DataStoreFactory.getOfy();

		// @REX case of just specifying the comp and not the rounds (want all rounds)
		List<Long> matchIds = new ArrayList<Long>();
		for (Long rid : rq.getRoundIds()) {
			IRound r = rf.get(rid);
			for (Long mid : r.getMatchIDs()) {
				matchIds.add(mid);
			}
		}
		
		Query<ScrumPlayerMatchStats> qpms = ofy.query(ScrumPlayerMatchStats.class).filter("matchId in",matchIds);

		if (rq.getPositions() != null && !rq.getPositions().isEmpty()) {
			qpms = qpms.filter("pos in",rq.getPositions());
		}


		if (rq.getCountryIds() != null && !rq.getCountryIds().isEmpty() && !rq.getCountryIds().contains(5000L)) {
			qpms = qpms.filter("countryId in", rq.getCountryIds());
		}


		if (rq.getTeamIds() != null && !rq.getTeamIds().isEmpty() && !rq.getTeamIds().contains(-1L)) {
			qpms = qpms.filter("teamId in", rq.getTeamIds());
		}

		List<ScrumPlayerMatchStats> qlist = Lists.reverse(qpms.list());
		List<IPlayerMatchStats> list = new ArrayList<IPlayerMatchStats>();

		for (ScrumPlayerMatchStats spms : qlist) {
			list.add(spms);
		}

		return Lists.reverse(list);
	}
}
