package net.rugby.foundation.admin.client;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.admin.client.place.AdminCompPlace.Filter;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.admin.shared.IBlurb;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.admin.shared.IWorkflowConfiguration;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema20130713;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.UniversalRound;
import net.rugby.foundation.topten.model.shared.ITopTenList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
@RemoteServiceRelativePath("rugbyAdminService")
public interface RugbyAdminService extends RemoteService {
	ICompetition fetchCompetition(String url, List<IRound> rounds, List<ITeamGroup> teams, CompetitionType compType);
	ICompetition saveCompetition(ICompetition comp, Map<String,ITeamGroup> teams);
	Map<String, ITeamGroup> fetchTeams(String url, CompetitionType compType);
	List<IRound>  fetchRounds(String url, Map<String, IMatchGroup> matches, CompetitionType compType);
	Map<String, IMatchGroup> fetchMatches(String url, Map<String, ITeamGroup> teams, CompetitionType compType);
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
	
	List<IPlayerRating> getPlayerMatchInfo(Long matchId);
	List<ICountry> fetchCountryList();
	List<position> fetchPositionList();
	String fetchMatchStats(Long matchId);
	
	List<IAdminTask> getAllOpenAdminTasks();
	List<IAdminTask> deleteTasks(List<IAdminTask> selectedItems);
	IAdminTask getTask(Long id);
	
	IPlayerMatchStats getPlayerMatchStats(Long id);
	
	IPlayerRating savePlayerMatchStats(IPlayerMatchStats pms, IAdminTask target);
	ICompetition repairComp(ICompetition comp);
	IPlayerMatchStats refetchPlayerMatchStats(IPlayerMatchStats pms);
	IRatingQuery createRatingQuery(List<Long> compId, List<Long> roundId, List<position> posi, List<Long> countryId, List<Long> teamId, Long schemaId, Boolean scaleTime, Boolean scaleComp, Boolean scaleStanding, Boolean scaleMinutesPlayed, Boolean instrument);
//	List<IPlayerMatchInfo> reRateMatch(Long matchId);
	IMatchGroup SaveScore(Long matchId, int hS, int vS, Status status);
	ITeamMatchStats getTeamMatchStats(Long matchId, Long teamId);
	ITeamMatchStats refetchTeamMatchStats(ITeamMatchStats target);
	ITeamMatchStats saveTeamMatchStats(ITeamMatchStats tms, IAdminTask target);
	
	// match rating engine schema
	ScrumMatchRatingEngineSchema saveMatchRatingEngineSchema(ScrumMatchRatingEngineSchema schema);
	ScrumMatchRatingEngineSchema getMatchRatingEngineSchema(Long schemaId);
	ScrumMatchRatingEngineSchema createMatchRatingEngineSchema();
	ScrumMatchRatingEngineSchema saveMatchRatingEngineSchemaAsCopy(ScrumMatchRatingEngineSchema schema);
	Boolean deleteMatchRatingEngineSchema(ScrumMatchRatingEngineSchema20130713 schema);
	Boolean deleteRatingsForMatchRatingEngineSchema(ScrumMatchRatingEngineSchema20130713 schema);
	ScrumMatchRatingEngineSchema setMatchRatingEngineSchemaAsDefault(ScrumMatchRatingEngineSchema20130713 schema);
	boolean deleteRawScoresForMatchRatingEngineSchema(ScrumMatchRatingEngineSchema20130713 schema);
	List<ScrumMatchRatingEngineSchema> getScrumSchemaList();
	Boolean flushAllPipelineJobs();
	
	// top ten list
	TopTenSeedData createTopTenList(TopTenSeedData tti, Map<IPlayer, String> twitterMap);
	
	IContent createContent(Long id, String content);
	List<IContent> getContentList(boolean onlyActive);
	ICoreConfiguration getConfiguration();
	
	List<IStanding> getStandings(Long roundId);
	List<IStanding> saveStandings(Long roundId, List<IStanding> standings);
	List<IStanding> FetchRoundStandings(Long roundId);
	IRatingQuery getRatingQuery(long parseLong);
	List<IPlayerRating> getRatingQueryResults(long parseLong);
	//List<IPlayerRating> getTimeSeriesRatingQueryResults(long parseLong);
	Boolean deleteRatingQuery(IRatingQuery query);
	IRatingQuery rerunRatingQuery(Long id);
	String checkPipelineStatus(String id, Long matchId);
	IMatchGroup AddMatchToRound(IRound round, Long homeTeamId, Long visitTeamId);
	String cleanUp();
	
	// rating series
	List<ISeriesConfiguration> getAllSeriesConfigurations(Boolean active);
	ISeriesConfiguration getSeriesConfiguration(Long id);
	String processSeriesConfiguration(Long sConfigId);
	Boolean deleteSeriesConfiguration(Long sConfigId);
	ISeriesConfiguration saveSeriesConfiguration(ISeriesConfiguration sConfig) throws Exception;
	List<UniversalRound> getUniversalRounds(int size);
	ICompetition addVirtualComp();
	ISeriesConfiguration rollBackSeriesConfiguration(Long id);
	Boolean addRound(Long compId, int uri, String name);
	
	// promote
	List<IBlurb> getAllBlurbs(Boolean active);
	List<IBlurb> addBlurb(String url, String linkText, String bodyText);
	String getListNameForUrl(String url);
	String getDigestPreview(String message, List<Long> blurbIds);
	Integer sendDigestEmail(String message, List<Long> blurbIds);
	String getDigestUserList();
}
