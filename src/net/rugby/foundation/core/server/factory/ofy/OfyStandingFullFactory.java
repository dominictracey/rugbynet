package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.BaseStandingFullFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IStandingFullFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.IStandingFull;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.Standing;
import net.rugby.foundation.model.shared.StandingFull;

public class OfyStandingFullFactory extends BaseStandingFullFactory implements IStandingFullFactory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3011818629485838725L;
	private IRoundFactory rf;
	private ITeamGroupFactory tf;
	private ICompetitionFactory cf;

	@Inject
	public OfyStandingFullFactory(IRoundFactory rf, ITeamGroupFactory tf, ICompetitionFactory cf) {
		this.rf = rf;
		this.tf = tf;
		this.cf = cf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IStandingFactory#put(net.rugby.foundation.model.shared.IStanding)
	 */
	@Override
	public IStandingFull putToPersistentDatastore(IStandingFull standing) {
		try {
			if (standing == null) {
				return null;
			}
			Objectify ofy = DataStoreFactory.getOfy();
			
			// make sure we only have one per team per round - delete any pre-existing records before saving this one
			Query<StandingFull> qs = ofy.query(StandingFull.class).filter("roundId", standing.getRoundId()).filter("teamId", standing.getTeamId());
			if (qs.count() > 0) {
				Iterator<StandingFull> it = qs.iterator();
				while (it.hasNext()) {
					StandingFull s = it.next();
					if (s.getId() != standing.getId()) {
						deleteFromPersistentDatastore(s);
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE, "Deleted duplicate standing object for team " + standing.getTeam().getDisplayName() + " in round " + standing.getRound().getName());						
					}
				}
			}
			
			ofy.put(standing);
			return standing;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	protected IStandingFull getFromPersistentDatastore(Long id) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			if (id != null) {
				IStandingFull t = ofy.get(new Key<StandingFull>(StandingFull.class,id));
				t.setRound(rf.get(t.getRoundId()));
				t.setTeam(tf.get(t.getTeamId()));
				return t;
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
	public IStandingFull create() {
		try {
			IStandingFull p = new StandingFull();
			return p;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}


	@Override
	protected boolean deleteFromPersistentDatastore(IStandingFull t) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.delete(t);
			return true;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
	}

	@Override
	public List<IStandingFull> getForRound(IRound r) {
		List<IStandingFull> list = new ArrayList<IStandingFull>();
		Objectify ofy = DataStoreFactory.getOfy();
		Query<StandingFull> qs = ofy.query(StandingFull.class).filter("roundId", r.getId()).order("standing");
		if (qs.count() != 0) {
			for (StandingFull s : qs) {
				s.setRound(rf.get(s.getRoundId()));
				s.setTeam(tf.get(s.getTeamId()));
				list.add(s);
			}
		} else { // give default to everyone
			Map<Long,ITeamGroup> teamMap = new HashMap<Long, ITeamGroup>();
			for (IMatchGroup m: r.getMatches()) {
				if (!teamMap.containsKey(m.getHomeTeamId())) {
					teamMap.put(m.getHomeTeamId(), m.getHomeTeam());
					list.add(createDefault(r,m.getHomeTeam()));
				} 
				if (!teamMap.containsKey(m.getVisitingTeamId())) {
					teamMap.put(m.getVisitingTeamId(), m.getVisitingTeam());
					list.add(createDefault(r,m.getVisitingTeam()));
				}
			}
		}
		return list;
	}
	
	private IStandingFull createDefault(IRound r, ITeamGroup t) {
		IStandingFull s = create();
		s.setRound(r);
		s.setRoundId(r.getId());
		s.setTeam(t);
		s.setTeamId(t.getId());
		s.setStanding(2);
		s.setWins(0);
		s.setLosses(0);
		s.setGamesPlayed(0);
		
		return s;
	}

	@Override
	public List<IStandingFull> getLatestForCompFromPersistentDatastore(Long compId) {
		try {
			List<IStandingFull> list = new ArrayList<IStandingFull>();
			ICompetition c = cf.get(compId);
			IRound r = c.getNextRound();
			Long rid = null;
			int ind = c.getRounds().size()-1;
			Objectify ofy = DataStoreFactory.getOfy();
			Query<StandingFull> qs = null;
			do{
				r = c.getRounds().get(ind);
				qs = ofy.query(StandingFull.class).filter("roundId", r.getId()).order("standing");
				if(ind > -1){
					ind -= 1;
				}
			} while(qs.count() == 0 && ind != -1);
			
//			if(r == null) {
//				r = c.getRounds().get(c.getRounds().size()-1);
//			}
			
//			rid = r == null ? 0L : r.getId();
//			if (qs.count() == 0 || r == null) {
//				r = c.getPrevRound();
//				if (r != null) {
//					qs = ofy.query(StandingFull.class).filter("roundId", r.getId()).order("standing");
//				}
//			}			
			if (qs.count() != 0) {
				for (StandingFull s : qs) {
					list.add(s);
				}
			}
			
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}
}
