package net.rugby.foundation.admin.server.model;

import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamGroup;

public interface IStandingsFetcher {
	void setRound(IRound r);
	void setComp(ICompetition c);
	IStanding getStandingForTeam(ITeamGroup t);
	void setUc(IUrlCacher uc);
	void setUrl(String url);
}
