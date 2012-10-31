package net.rugby.foundation.admin.server.model;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IRound;


public interface IResultFetcher {
	public void setComp(ICompetition comp);
	public void setRound(IRound round);
	
	public IMatchResult getResult(IMatchGroup match);
}
