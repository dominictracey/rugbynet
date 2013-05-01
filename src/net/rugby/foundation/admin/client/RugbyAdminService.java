package net.rugby.foundation.admin.client;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.admin.client.place.AdminCompPlace.Filter;
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.IWorkflowConfiguration;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.Position.position;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
@RemoteServiceRelativePath("rugbyAdminService")
public interface RugbyAdminService extends RemoteService {
	ICompetition fetchCompetition(String url, List<IRound> rounds, List<ITeamGroup> teams);
	ICompetition saveCompetition(ICompetition comp, Map<String,ITeamGroup> teams);
	Map<String, ITeamGroup> fetchTeams(String url, String resultType);
	Map<String, ITeamGroup> saveTeams(Map<String, ITeamGroup> teams);
	List<IRound>  fetchRounds(String url, Map<String, IMatchGroup> matches);
	List<IRound> saveRounds(List<IRound> rounds);
	Map<String, IMatchGroup> fetchMatches(String url, Map<String, ITeamGroup> teams);
	Map<String, IMatchGroup> saveMatches(Map<String, IMatchGroup> matches);
	List<ICompetition> getComps(Filter filter);
	IWorkflowConfiguration saveWorkflowConfig(IWorkflowConfiguration wfc);
	Map<String, IOrchestrationConfiguration> getOrchestrationConfiguration();
	Map<String, IOrchestrationConfiguration> saveOrchestrationConfiguration(	Map<String, IOrchestrationConfiguration> configs);
	List<ITeamGroup> getTeams(Long compId);
	List<IRound> getRounds(Long compId);
	List<IMatchGroup> getMatches(Long roundId);
	List<IMatchResult> getResults(Long matchId);
	ITeamGroup saveTeam(ITeamGroup teamGroup);
	ITeamGroup getTeam(Long teamId);
	IPlayer getPlayer(Long id);
	IPlayer savePlayer(IPlayer player, IAdminTask task);
	//IPlayerMatchStats savePlayerMatchStats(IPlayerMatchStats stats, IAdminTask task);
	IMatchGroup saveMatch(IMatchGroup matchGroup);
	IMatchGroup getMatch(Long matchId);
	Boolean createAdmin();
	IWorkflowConfiguration getWorkflowConfiguration();
	/**
	 * @return This is a clean-up job with a variety of things in it that can be run in a scheduled manner
	 */
	List<String> sanityCheck();
	/**
	 * Save just the comp objects attributes
	 * @param comp
	 * @return
	 */
	ICompetition saveCompInfo(ICompetition comp);
	/**
	 * 
	 */
	List<String> lockMatch(Boolean lock, IMatchGroup match, Long compId, List<String> log);
	/**
	 * @param compId
	 * @return
	 */
	ICompetition getComp(Long compId);
	/**
	 * @param match
	 * @param compId
	 * @param log
	 * @return
	 */
	IMatchGroup fetchMatchScore(IMatchGroup match, Long compId, List<String> log);
	
	List<IPlayerMatchStats> testMatchStats(Long matchId);
	
	List<IPlayerMatchInfo> getPlayerMatchInfo(Long matchId);
	List<ICountry> fetchCountryList();
	List<position> fetchPositionList();
	List<IPlayerMatchInfo> fetchMatchStats(Long matchId);
	
	List<IAdminTask> getAllOpenAdminTasks();
	List<IAdminTask> deleteTasks(List<IAdminTask> selectedItems);
	IAdminTask getTask(Long id);
	
	IPlayerMatchStats getPlayerMatchStats(Long id);
	
	IPlayerMatchInfo savePlayerMatchStats(IPlayerMatchStats pms, IAdminTask target);
	ICompetition repairComp(ICompetition comp);
	IPlayerMatchStats refetchPlayerMatchStats(IPlayerMatchStats pms);
}
