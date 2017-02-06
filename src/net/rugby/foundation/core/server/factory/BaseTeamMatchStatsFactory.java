package net.rugby.foundation.core.server.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.workflow.fetchstats.FetchMatchStats.Home_or_Visitor;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.ScrumTeamMatchStats;

public abstract class BaseTeamMatchStatsFactory extends BaseCachingFactory<ITeamMatchStats> implements ITeamMatchStatsFactory {
	
	protected IMatchGroupFactory mf;
	private IRoundFactory rf;

	@Inject
	protected void setFactories(IMatchGroupFactory mf, IRoundFactory rf) {
		this.mf = mf;
		this.rf = rf;
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
		if (home != null) {
			return prefix + id.toString() + home.toString();
		} else {
			return prefix + id.toString();
		}
	}
	
	@Override
	public ITeamMatchStats create() {
		ITeamMatchStats tms = new ScrumTeamMatchStats();
		tms.setCreated(new Date());
		return tms;
	}
	

	@Override
	public List<ITeamMatchStats> query(IRatingQuery rq) {
		try {
			// @REX case of just specifying the comp and not the rounds (want all rounds)
			List<ITeamMatchStats> tmss = new ArrayList<ITeamMatchStats>();
			for (Long rid : rq.getRoundIds()) {
				IRound r = rf.get(rid);
				for (IMatchGroup m : r.getMatches()) {
					List<ITeamMatchStats> tms = getForMatch(m.getId());
					tmss.addAll(tms);				
				}
			}
			
			return tmss;
			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in query: " + ex.getLocalizedMessage());
			return null;
		}
	}
	
	protected void flushByMatchId(Long matchId)
	{
		try {
			super.deleteItemFromMemcache(getScrumCacheId(matchId,Home_or_Visitor.HOME));
			super.deleteItemFromMemcache(getScrumCacheId(matchId,Home_or_Visitor.VISITOR));
			super.dropFromCache(getScrumCacheId(matchId,null));
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "FlushByMatchId" + ex.getMessage(), ex);
		}
	}
	
	protected abstract List<ITeamMatchStats> getListFromPersistentDatastoreByMatchId(Long mid);
		
	@Override
	public List<ITeamMatchStats> getForMatch(Long matchId) {
		try {
			List<ITeamMatchStats> tmss = null;
			
			tmss = getList(getScrumCacheId(matchId,null));
			
			if (tmss == null) {
				tmss = getListFromPersistentDatastoreByMatchId(matchId);

				if (tmss != null) {
					putList(getScrumCacheId(matchId,null), tmss);
				} else {
					return null;
				}
			} 
			return tmss;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getForMatch" + ex.getMessage(), ex);
			return null;
		}	
	}

}
