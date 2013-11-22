package net.rugby.foundation.admin.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import net.rugby.foundation.admin.client.place.AdminCompPlace.Filter;
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.IWorkflowConfiguration;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema20130713;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.Position.position;


public interface RugbyAdminServiceAsync {
	public void fetchCompetition(String url, List<IRound> rounds, List<ITeamGroup> teams, AsyncCallback<ICompetition> cb);
	public void saveCompetition(ICompetition comp, Map<String,ITeamGroup> teams, AsyncCallback<ICompetition> cb);
	public void fetchTeams(String url, String resultType, AsyncCallback<Map<String, ITeamGroup>> cb);
	public void fetchRounds(String url, Map<String, IMatchGroup> matches, AsyncCallback<List<IRound> > cb);
	public void fetchMatches(String url, Map<String, ITeamGroup> teams, AsyncCallback<Map<String, IMatchGroup>> cb);
	public void  getComps(Filter filter, AsyncCallback<List<ICompetition>> cb);
	public void saveWorkflowConfig(IWorkflowConfiguration wfc, AsyncCallback<IWorkflowConfiguration> asyncCallback);
	public void getOrchestrationConfiguration(AsyncCallback<Map<String, IOrchestrationConfiguration>> asyncCallback);
	public void saveOrchestrationConfiguration(	Map<String, IOrchestrationConfiguration> configs,AsyncCallback<Map<String, IOrchestrationConfiguration>> asyncCallback);

	/**
	 * @param roundId
	 * @param asyncCallback
	 */
	public void getMatches(Long roundId,
			AsyncCallback<List<IMatchGroup>> asyncCallback);

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
	public void setCompAsDefault(Long compId, AsyncCallback<Boolean> asyncCallback);
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
	public void repairComp(ICompetition comp,
			AsyncCallback<ICompetition> asyncCallback);
	public void fetchMatchScore(IMatchGroup matchGroup, Long currentCompId,
			List<String> log, AsyncCallback<IMatchGroup> asyncCallback);
	public void refetchPlayerMatchStats(IPlayerMatchStats pms, AsyncCallback<IPlayerMatchStats> asyncCallback);
	public void aggregatePlayerMatchRatings(Long compId, Long roundId,
			position posi, Long countryId, Long teamId,
			AsyncCallback<List<IPlayerMatchInfo>> asyncCallback);
	public void reRateMatch(Long id, AsyncCallback<List<IPlayerMatchInfo>> asyncCallback);
	public void  getTeamMatchStats(Long matchId, Long teamId, AsyncCallback<ITeamMatchStats> asyncCallback);
	public void refetchTeamMatchStats(ITeamMatchStats target,
			AsyncCallback<ITeamMatchStats> asyncCallback);
	public void saveTeamMatchStats(ITeamMatchStats tms, IAdminTask target,
			AsyncCallback<ITeamMatchStats> asyncCallback);
	public void saveMatchRatingEngineSchema(
			ScrumMatchRatingEngineSchema schema,
			AsyncCallback<ScrumMatchRatingEngineSchema> asyncCallback);
	public void getMatchRatingEngineSchema(Long schemaId,
			AsyncCallback<ScrumMatchRatingEngineSchema> asyncCallback);
	public void saveMatchRatingEngineSchemaAsCopy(
			ScrumMatchRatingEngineSchema schema,
			AsyncCallback<ScrumMatchRatingEngineSchema> asyncCallback);
	public void deleteMatchRatingEngineSchema(
			ScrumMatchRatingEngineSchema20130713 schema,
			AsyncCallback<Boolean> asyncCallback);
	public void deleteRatingsForMatchRatingEngineSchema(
			ScrumMatchRatingEngineSchema20130713 schema,
			AsyncCallback<Boolean> asyncCallback);
	public void setMatchRatingEngineSchemaAsDefault(
			ScrumMatchRatingEngineSchema20130713 schema,
			AsyncCallback<ScrumMatchRatingEngineSchema> asyncCallback);	
	public void getScrumSchemaList(AsyncCallback<List<ScrumMatchRatingEngineSchema>> asyncCallback);
	
	public void flushAllPipelineJobs(AsyncCallback<Boolean> asyncCallback);
	public void deleteComp(Long id, AsyncCallback<Boolean> asyncCallback);
	public void createTopTenList(TopTenSeedData tti, AsyncCallback<TopTenSeedData> asyncCallback);
	public void createContent(Long id, String content,
			AsyncCallback<IContent> asyncCallback);
	public void getContentList(boolean onlyActive, AsyncCallback<List<IContent>> asyncCallback);
	public void getConfiguration(AsyncCallback<ICoreConfiguration> asyncCallback);
	public void getStandings(Long roundId, AsyncCallback<List<IStanding>> asyncCallback);
	public void saveStandings(Long roundId, List<IStanding> standings, AsyncCallback<List<IStanding>> asyncCallback);
	public void FetchRoundStandings(Long roundId, AsyncCallback<List<IStanding>> asyncCallback);
}
