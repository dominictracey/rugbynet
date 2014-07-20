package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IRatingQuery;

public interface IPlayerMatchStatsFactory  extends ICachingFactory<IPlayerMatchStats> {

	public abstract List<IPlayerMatchStats> getByMatchId(Long matchId);
	public abstract boolean deleteForMatch(IMatchGroup m);
	public abstract List<IPlayerMatchStats> query(IRatingQuery q);

}