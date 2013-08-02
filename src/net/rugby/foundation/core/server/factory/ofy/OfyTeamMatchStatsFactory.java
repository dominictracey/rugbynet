package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.ScrumTeamMatchStats;

public class OfyTeamMatchStatsFactory implements ITeamMatchStatsFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9132591058693886799L;
	//private Objectify ofy;


	public OfyTeamMatchStatsFactory() {
		//this.ofy = DataStoreFactory.getOfy();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory#get()
	 */
	@Override
	public ITeamMatchStats getById(Long id) {
		Objectify ofy = DataStoreFactory.getOfy();
		if (id != null)
			return ofy.get(new Key<ScrumTeamMatchStats>(ScrumTeamMatchStats.class,id));
		else
			return new ScrumTeamMatchStats();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory#put(net.rugby.foundation.model.shared.ITeamMatchStats)
	 */
	@Override
	public ITeamMatchStats put(ITeamMatchStats tms) {
		Objectify ofy = DataStoreFactory.getOfy();
		
		// first delete any existing entries for this team in this match
		ScrumTeamMatchStats existing = ofy.query(ScrumTeamMatchStats.class).filter("teamId", tms.getTeamId()).filter("matchId", tms.getMatchId()).get();

		if (existing != null) {
			ofy.delete(existing);
		}
		
		ofy.put(tms);
		return tms;
	}

	
	public Boolean delete(ITeamMatchStats val) {
		Objectify ofy = DataStoreFactory.getOfy();
		ofy.delete(val);
		return true;
	}

	@Override
	public ITeamMatchStats getHomeStats(IMatchGroup m) {
		Objectify ofy = DataStoreFactory.getOfy();
		ScrumTeamMatchStats s = ofy.query(ScrumTeamMatchStats.class).filter("teamId", m.getHomeTeamId()).filter("matchId", m.getId()).get();

		
		return (ITeamMatchStats)s;
	}

	@Override
	public ITeamMatchStats getVisitStats(IMatchGroup m) {
		Objectify ofy = DataStoreFactory.getOfy();
		ScrumTeamMatchStats s = ofy.query(ScrumTeamMatchStats.class).filter("teamId", m.getVisitingTeamId()).filter("matchId", m.getId()).get();

		
		return (ITeamMatchStats)s;	}

	@Override
	public boolean deleteForMatch(IMatchGroup m) {
		try {
			if (m != null) {
				Objectify ofy = DataStoreFactory.getOfy();
	
				ScrumTeamMatchStats s = ofy.query(ScrumTeamMatchStats.class).filter("teamId", m.getHomeTeamId()).filter("matchId", m.getId()).get();
	
				if (s != null) {
					ofy.delete(s);
				}
				
				s = ofy.query(ScrumTeamMatchStats.class).filter("teamId", m.getVisitingTeamId()).filter("matchId", m.getId()).get();
				
				if (s != null) {
					ofy.delete(s);
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
}
