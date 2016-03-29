package net.rugby.foundation.admin.server.model;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;

public interface IForeignCompetitionFetcher {
	public ICompetition getCompetition(String homePage, List<IRound> rounds, List<ITeamGroup> teams);
	public Map<String, ITeamGroup> getTeams();
	public void setURL(String url);
	public List<IRound> getRounds(String url, Map<String, IMatchGroup> matches);
	public Map<String, IMatchGroup> getMatches(String url, Map<String, ITeamGroup> teams);
	/**
	 * Takes a match and goes to look for it in the fixtures table. It will update:
	 * 		date - adjusts the kickoff time if it has changed
	 * 		scrumID - sets the link to espn's match id
	 * 		workflowStatus - sets to linked if the scrumId is changed
	 * @param match - the match to be updated
	 * @return true if changes were made and successfully saved, false otherwise.
	 */
	public Boolean updateMatch(IMatchGroup match);
}
