package net.rugby.foundation.admin.server.model;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;

public interface ITeamMatchStatsFetcher extends IJsonFetcher {
	boolean process(IMatchGroup match, ICompetition comp, Boolean home);
	ITeamMatchStats getStats();
}
