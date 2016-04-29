package net.rugby.foundation.admin.server.model;

import java.util.Map;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;


public interface IResultFetcher {
	public void setComp(ICompetition comp);
	public void setRound(IRound round);
	
	/**
	 * 
	 * @param match
	 * @return the match result or null if not found
	 */
	public IMatchResult getResult(IMatchGroup match);
	
	/**
	 * Parses the ?template=results page for all completed matches
	 * return - HashMap key: displayName, value: IMatchGroup
	 */
	public Map<String, IMatchGroup> getMatches(String url, Map<String, ITeamGroup> teams);

	/**
	 * 
	 * @param match
	 * @return true if the match has a listing in the espn result view
	 */
	public Boolean isAvailable(IMatchGroup match);
}
