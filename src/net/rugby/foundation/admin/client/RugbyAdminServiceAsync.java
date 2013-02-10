package net.rugby.foundation.admin.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

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


public interface RugbyAdminServiceAsync {
	public void fetchCompetition(String url, List<IRound> rounds, List<ITeamGroup> teams, AsyncCallback<ICompetition> cb);
	public void saveCompetition(ICompetition comp, Map<String,ITeamGroup> teams, AsyncCallback<ICompetition> cb);
	public void fetchTeams(String url, String resultType, AsyncCallback<Map<String, ITeamGroup>> cb);
	public void saveTeams(Map<String, ITeamGroup> teams, AsyncCallback<Map<String, ITeamGroup>> cb);
	public void fetchRounds(String url, Map<String, IMatchGroup> matches, AsyncCallback<List<IRound> > cb);
	public void saveRounds(List<IRound> rounds, AsyncCallback<List<IRound>> cb);
	public void fetchMatches(String url, Map<String, ITeamGroup> teams, AsyncCallback<Map<String, IMatchGroup>> cb);
	public void saveMatches(Map<String, IMatchGroup> rounds, AsyncCallback<Map<String, IMatchGroup>> cb);
	public void  getAllComps(AsyncCallback<List<ICompetition>> cb);
	public void saveWorkflowConfig(IWorkflowConfiguration wfc, AsyncCallback<IWorkflowConfiguration> asyncCallback);
	public void getOrchestrationConfiguration(AsyncCallback<Map<String, IOrchestrationConfiguration>> asyncCallback);
	public void saveOrchestrationConfiguration(	Map<String, IOrchestrationConfiguration> configs,AsyncCallback<Map<String, IOrchestrationConfiguration>> asyncCallback);
	/**
	 * @param compId
	 * @param asyncCallback
	 */
	public void getTeams(Long compId,
			AsyncCallback<List<ITeamGroup>> asyncCallback);
	/**
	 * @param compId
	 * @param asyncCallback
	 */
	public void getRounds(Long compId,
			AsyncCallback<List<IRound>> asyncCallback);
	/**
	 * @param roundId
	 * @param asyncCallback
	 */
	public void getMatches(Long roundId,
			AsyncCallback<List<IMatchGroup>> asyncCallback);
	/**
	 * @param matchId
	 * @param asyncCallback
	 */
	public void getResults(Long matchId,
			AsyncCallback<List<IMatchResult>> asyncCallback);
	/**
	 * @param teamGroup
	 * @param asyncCallback
	 */
	public void saveTeam(ITeamGroup teamGroup,
			AsyncCallback<ITeamGroup> asyncCallback);
	/**
	 * @param teamId
	 * @param asyncCallback
	 */
	public void getTeam(Long teamId, AsyncCallback<ITeamGroup> asyncCallback);
	
	public void getPlayer(Long id,  AsyncCallback<IPlayer> asyncCallback);
	public void savePlayer(IPlayer player, IAdminTask task, AsyncCallback<IPlayer> asyncCallback);
	//public void savePlayerMatchStats(IPlayerMatchStats stats, IAdminTask task, AsyncCallback<IPlayerMatchStats> asyncCallback);

	/**
	 * @param asyncCallback
	 */
	public void createAdmin(AsyncCallback<Boolean> asyncCallback);
	/**
	 * @param asyncCallback
	 */
	public void getWorkflowConfiguration(
			AsyncCallback<IWorkflowConfiguration> asyncCallback);
	
	public void sanityCheck(
			AsyncCallback<List<String>> asyncCallback);
	/**
	 * @param comp
	 * @param asyncCallback
	 */
	public void saveCompInfo(ICompetition comp,
			AsyncCallback<ICompetition> asyncCallback);
	/**
	 * @param matchId
	 * @param asyncCallback
	 */
	public void getMatch(Long matchId, AsyncCallback<IMatchGroup> asyncCallback);
	public void saveMatch(IMatchGroup match, AsyncCallback<IMatchGroup> asyncCallback);
	
	public void lockMatch(Boolean lock, IMatchGroup match, Long compId, List<String> log, AsyncCallback<List<String>> asyncCallback);
	
	public void getComp(Long compId, AsyncCallback<ICompetition> asyncCallback);
	
	public void fetchMatchScore(IMatchGroup match, Long compId, List<String> log, AsyncCallback<List<String>> asyncCallback);
	public void testMatchStats(Long matchId, AsyncCallback<List<IPlayerMatchStats>> asyncCallback);
	
	public void getPlayerMatchInfo(Long matchId, AsyncCallback<List<IPlayerMatchInfo>> asyncCallback);
	public void fetchCountryList(AsyncCallback<List<ICountry>> asyncCallback);
	public void fetchPositionList(AsyncCallback<List<position>> asyncCallback);
	public void fetchMatchStats(Long matchId, AsyncCallback<List<IPlayerMatchInfo>> asyncCallback);
	
	public void getAllOpenAdminTasks(AsyncCallback<List<IAdminTask>> asyncCallback);
	public void deleteTasks(List<IAdminTask> selectedItems,
			AsyncCallback<List<IAdminTask>> asyncCallback);
	public void getTask(Long id, AsyncCallback<IAdminTask> asyncCallback);
	public void getPlayerMatchStats(Long id, AsyncCallback<IPlayerMatchStats> asyncCallback);
	public void savePlayerMatchStats(IPlayerMatchStats pms, IAdminTask target,
			AsyncCallback<IPlayerMatchInfo> asyncCallback);
}
