package net.rugby.foundation.admin.client;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.admin.client.place.AdminCompPlace.Filter;
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
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.Position.position;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
@RemoteServiceRelativePath("rugbyAdminService")
public interface RugbyAdminService extends RemoteService {
	ICompetition fetchCompetition(String url, List<IRound> rounds, List<ITeamGroup> teams);
	ICompetition saveCompetition(ICompetition comp, Map<String,ITeamGroup> teams);
	Map<String, ITeamGroup> fetchTeams(String url, String resultType);
	List<IRound>  fetchRounds(String url, Map<String, IMatchGroup> matches);
	Map<String, IMatchGroup> fetchMatches(String url, Map<String, ITeamGroup> teams);
	List<ICompetition> getComps(Filter filter);
	IWorkflowConfiguration saveWorkflowConfig(IWorkflowConfiguration wfc);
	Map<String, IOrchestrationConfiguration> getOrchestrationConfiguration();
	Map<String, IOrchestrationConfiguration> saveOrchestrationConfiguration(	Map<String, IOrchestrationConfiguration> configs);

	List<IMatchGroup> getMatches(Long roundId);;
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
	Boolean setCompAsDefault(Long compId);
	Boolean deleteComp(Long id);
	
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
	List<IPlayerMatchInfo> aggregatePlayerMatchRatings(Long compId, Long roundId, position posi, Long countryId, Long teamId);
	List<IPlayerMatchInfo> reRateMatch(Long matchId);

	ITeamMatchStats getTeamMatchStats(Long matchId, Long teamId);
	ITeamMatchStats refetchTeamMatchStats(ITeamMatchStats target);
	ITeamMatchStats saveTeamMatchStats(ITeamMatchStats tms, IAdminTask target);
	
	// match rating engine schema
	ScrumMatchRatingEngineSchema saveMatchRatingEngineSchema(ScrumMatchRatingEngineSchema schema);
	ScrumMatchRatingEngineSchema getMatchRatingEngineSchema(Long schemaId);
	ScrumMatchRatingEngineSchema saveMatchRatingEngineSchemaAsCopy(ScrumMatchRatingEngineSchema schema);
	Boolean deleteMatchRatingEngineSchema(ScrumMatchRatingEngineSchema20130713 schema);
	Boolean deleteRatingsForMatchRatingEngineSchema(ScrumMatchRatingEngineSchema20130713 schema);
	ScrumMatchRatingEngineSchema setMatchRatingEngineSchemaAsDefault(ScrumMatchRatingEngineSchema20130713 schema);
	List<ScrumMatchRatingEngineSchema> getScrumSchemaList();
	Boolean flushAllPipelineJobs();
	
	// top ten list
	TopTenSeedData createTopTenList(TopTenSeedData tti);
	
	IContent createContent(Long id, String content);
	List<IContent> getContentList(boolean onlyActive);
	ICoreConfiguration getConfiguration();
	
	List<IStanding> getStandings(Long roundId);
	List<IStanding> saveStandings(Long roundId, List<IStanding> standings);
	List<IStanding> FetchRoundStandings(Long roundId);
}
