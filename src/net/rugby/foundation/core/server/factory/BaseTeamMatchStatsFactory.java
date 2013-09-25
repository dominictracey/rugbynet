package net.rugby.foundation.core.server.factory;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.ScrumTeamMatchStats;

public abstract class BaseTeamMatchStatsFactory extends BaseCachingFactory<ITeamMatchStats> implements ITeamMatchStatsFactory {
	
	protected IMatchGroupFactory mf;

	@Inject
	protected void setFactories(IMatchGroupFactory mf) {
		this.mf = mf;
	}
	
	private final String prefix = this.getClass().toString();

	public ITeamMatchStats getHomeStats(IMatchGroup m) {
		return getTeamStats(m,Home_or_Visitor.HOME);
	}

	public ITeamMatchStats getVisitStats(IMatchGroup m) {
		return getTeamStats(m,Home_or_Visitor.VISITOR);

	}

	private ITeamMatchStats getTeamStats(IMatchGroup m, Home_or_Visitor hov) {
		try {
			ITeamMatchStats p = null;
	
			p = getItem(getScrumCacheId(m.getId(),hov));
			
			if (p == null) {
				p = getFromPersistentDatastoreByMatchId(m.getId(),hov);

				if (p != null) {
					putItem(getScrumCacheId(m.getId(),hov), p);
				}	else {
					return null;
				}
			} 
			return p;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getTeamStats" + ex.getMessage(), ex);
			return null;
		}	
		
	}
	
	public boolean deleteForMatch(IMatchGroup m) {
		delete(getFromPersistentDatastoreByMatchId(m.getId(), Home_or_Visitor.HOME));
		delete(getFromPersistentDatastoreByMatchId(m.getId(), Home_or_Visitor.VISITOR));
		return true;
	}
	
	protected abstract ITeamMatchStats getFromPersistentDatastoreByMatchId(Long mid, Home_or_Visitor home);
	
	private String getScrumCacheId(Long id, Home_or_Visitor home) {
		return prefix + id.toString() + home.toString();
	}
	
	@Override
	public ITeamMatchStats create() {
		ITeamMatchStats tms = new ScrumTeamMatchStats();
		tms.setCreated(new Date());
		return tms;
	}
}
