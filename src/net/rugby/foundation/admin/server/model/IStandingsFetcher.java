package net.rugby.foundation.admin.server.model;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRound;

public interface IStandingsFetcher {
	void setRound(IRound r);
	void setComp(ICompetition c);
	boolean getStandings();
}
