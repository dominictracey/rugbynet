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
}
