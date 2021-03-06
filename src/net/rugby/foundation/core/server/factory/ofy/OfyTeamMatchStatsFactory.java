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

import net.rugby.foundation.admin.server.workflow.fetchstats.FetchMatchStats.Home_or_Visitor;
import net.rugby.foundation.core.server.factory.BaseTeamMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.ScrumTeamMatchStats;

public class OfyTeamMatchStatsFactory extends BaseTeamMatchStatsFactory implements ITeamMatchStatsFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9132591058693886799L;
	private IRoundFactory rf;

	@Inject
	public OfyTeamMatchStatsFactory(IRoundFactory rf, IMatchGroupFactory mf) {
		this.rf = rf;
		this.mf = mf;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory#get()
	 */
	@Override
	public ITeamMatchStats getFromPersistentDatastore(Long id) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			if (id != null)
				return ofy.get(new Key<ScrumTeamMatchStats>(ScrumTeamMatchStats.class,id));
			else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Don't call get with null to create a new TMS. Call create()");
			}
				return null;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory#put(net.rugby.foundation.model.shared.ITeamMatchStats)
	 */
	@Override
	public ITeamMatchStats putToPersistentDatastore(ITeamMatchStats tms) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();

			// first delete any existing entries for this team in this match
			ScrumTeamMatchStats existing = ofy.query(ScrumTeamMatchStats.class).filter("teamId", tms.getTeamId()).filter("matchId", tms.getMatchId()).get();

			if (existing != null) {
				// remove from cache too
				super.flushByMatchId(tms.getMatchId());
				ofy.delete(existing);
			}

			ofy.put(tms);
			return tms;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}


	@Override
	public boolean deleteFromPersistentDatastore(ITeamMatchStats t) {
		try {
			if (t != null) {
				Objectify ofy = DataStoreFactory.getOfy();
				ofy.delete(t);
				return true;
			} else {
				return false;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
	}



	@Override
	protected ITeamMatchStats getFromPersistentDatastoreByMatchId(Long mid,	Home_or_Visitor home) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			IMatchGroup m = mf.get(mid);
			ScrumTeamMatchStats s = null;
			if (home == Home_or_Visitor.VISITOR) {
				s = ofy.query(ScrumTeamMatchStats.class).filter("teamId", m.getVisitingTeamId()).filter("matchId", m.getId()).get();
			} else {
				s = ofy.query(ScrumTeamMatchStats.class).filter("teamId", m.getHomeTeamId()).filter("matchId", m.getId()).get();
			} 

			return (ITeamMatchStats)s;	
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in delete: " + ex.getLocalizedMessage());
			return null;
		}
	}

	@Override
	protected List<ITeamMatchStats> getListFromPersistentDatastoreByMatchId(Long mid) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			IMatchGroup m = mf.get(mid);
			List<ITeamMatchStats> retval = new ArrayList<ITeamMatchStats>();

			List<ScrumTeamMatchStats> qs = ofy.query(ScrumTeamMatchStats.class).filter("matchId", m.getId()).list();
			for (ScrumTeamMatchStats tms: qs) {
				retval.add(tms);
			}
			
			return retval;	
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in getListFromPersistentDatastoreByMatchId: " + ex.getLocalizedMessage());
			return null;
		}
	}

}
