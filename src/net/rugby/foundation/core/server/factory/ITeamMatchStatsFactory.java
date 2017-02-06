package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.ITeamMatchStats;

public interface ITeamMatchStatsFactory   extends ICachingFactory<ITeamMatchStats> {
	
	public abstract ITeamMatchStats getHomeStats(IMatchGroup m);

	public abstract ITeamMatchStats getVisitStats(IMatchGroup m);

	public abstract boolean deleteForMatch(IMatchGroup m);

	public abstract List<ITeamMatchStats> query(IRatingQuery rq);
	
	public abstract List<ITeamMatchStats> getForMatch(Long matchId);
}