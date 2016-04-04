package net.rugby.foundation.admin.client;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.admin.client.place.AdminCompPlace.Filter;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.admin.shared.IBlurb;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.admin.shared.IWorkflowConfiguration;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema20130713;
import net.rugby.foundation.model.shared.UniversalRound;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface RugbyAdminServiceAsync {
	public void fetchCompetition(String url, List<IRound> rounds, List<ITeamGroup> teams, CompetitionType compType, AsyncCallback<ICompetition> cb);
	public void saveCompetition(ICompetition comp, Map<String,ITeamGroup> teams, AsyncCallback<ICompetition> cb);
	public void fetchTeams(String url, CompetitionType compType, AsyncCallback<Map<String, ITeamGroup>> cb);
	public void fetchRounds(String url, Map<String, IMatchGroup> matches, CompetitionType compType, AsyncCallback<List<IRound> > cb);
	public void fetchMatches(String url, Map<String, ITeamGroup> teams, CompetitionType compType, AsyncCallback<Map<String, IMatchGroup>> cb);
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
	 * @param saveMatches - when the displayName is changed, would you like us to go and update all this team's pending matches' displayNames?
	 * @param asyncCallback
	 */
	public void saveTeam(ITeamGroup teamGroup, boolean saveMatches, AsyncCallback<ITeamGroup> asyncCallback);
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
	
	public void getPlayerMatchInfo(Long matchId, AsyncCallback<List<IPlayerRating>> asyncCallback);
	public void fetchCountryList(AsyncCallback<List<ICountry>> asyncCallback);
	public void fetchPositionList(AsyncCallback<List<position>> asyncCallback);
	public void fetchMatchStats(Long matchId, AsyncCallback<String> asyncCallback);
	
	public void getAllOpenAdminTasks(AsyncCallback<List<IAdminTask>> asyncCallback);
	public void deleteTasks(List<IAdminTask> selectedItems,
			AsyncCallback<List<IAdminTask>> asyncCallback);
	public void getTask(Long id, AsyncCallback<IAdminTask> asyncCallback);
	public void getPlayerMatchStats(Long id, AsyncCallback<IPlayerMatchStats> asyncCallback);
	public void savePlayerMatchStats(IPlayerMatchStats pms, IAdminTask target,
			AsyncCallback<IPlayerRating> asyncCallback);
	public void repairComp(ICompetition comp,
			AsyncCallback<ICompetition> asyncCallback);
	public void fetchMatchScore(IMatchGroup matchGroup, Long currentCompId,
			List<String> log, AsyncCallback<IMatchGroup> asyncCallback);
	public void refetchPlayerMatchStats(IPlayerMatchStats pms, AsyncCallback<IPlayerMatchStats> asyncCallback);
	public void createRatingQuery(List<Long> compId,
			List<Long> roundId, List<position> posi, List<Long> countryId, List<Long> teamId,
			Long schemaId, Boolean scaleTime, Boolean scaleComp, Boolean scaleStanding, Boolean scaleMinutesPlayed, Boolean instrument, AsyncCallback<IRatingQuery> asyncCallback);
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
	public void createMatchRatingEngineSchema(AsyncCallback<ScrumMatchRatingEngineSchema> asyncCallback);
	public void saveMatchRatingEngineSchemaAsCopy(
			ScrumMatchRatingEngineSchema schema,
			AsyncCallback<ScrumMatchRatingEngineSchema> asyncCallback);
	public void deleteMatchRatingEngineSchema(
			ScrumMatchRatingEngineSchema20130713 schema,
			AsyncCallback<Boolean> asyncCallback);
	public void deleteRatingsForMatchRatingEngineSchema(
			ScrumMatchRatingEngineSchema20130713 schema,
			AsyncCallback<Boolean> asyncCallback);
	public void deleteRawScoresForMatchRatingEngineSchema(
			ScrumMatchRatingEngineSchema20130713 schema,
			AsyncCallback<Boolean> asyncCallback);
	public void setMatchRatingEngineSchemaAsDefault(
			ScrumMatchRatingEngineSchema20130713 schema,
			AsyncCallback<ScrumMatchRatingEngineSchema> asyncCallback);	
	public void getScrumSchemaList(AsyncCallback<List<ScrumMatchRatingEngineSchema>> asyncCallback);
	
	public void flushAllPipelineJobs(AsyncCallback<Boolean> asyncCallback);
	public void deleteComp(Long id, AsyncCallback<Boolean> asyncCallback);
	public void createTopTenList(TopTenSeedData tti, Map<IPlayer, String> twitterMap, AsyncCallback<TopTenSeedData> asyncCallback);
	public void createContent(Long id, String content,
			AsyncCallback<IContent> asyncCallback);
	public void getContentList(boolean onlyActive, AsyncCallback<List<IContent>> asyncCallback);
	public void getConfiguration(AsyncCallback<ICoreConfiguration> asyncCallback);
	public void getStandings(Long roundId, AsyncCallback<List<IStanding>> asyncCallback);
	public void saveStandings(Long roundId, List<IStanding> standings, AsyncCallback<List<IStanding>> asyncCallback);
	public void FetchRoundStandings(Long roundId, AsyncCallback<List<IStanding>> asyncCallback);
	public void SaveScore(Long matchId, int hS, int vS, Status status, AsyncCallback<IMatchGroup> asyncCallback);
	public void getRatingQuery(long parseLong,
			AsyncCallback<IRatingQuery> asyncCallback);
	public void getRatingQueryResults(long parseLong,
			AsyncCallback<List<IPlayerRating>> asyncCallback);
	public void deleteRatingQuery(IRatingQuery query,
			AsyncCallback<Boolean> asyncCallback);
	public void checkPipelineStatus(String id, Long matchId,
			AsyncCallback<String> asyncCallback);
	public void AddMatchToRound(IRound round, Long homeTeamId, Long visitTeamId,
			AsyncCallback<IMatchGroup> asyncCallback);
	public void cleanUp(AsyncCallback<String> asyncCallback);
	
	//RatingSeries
	public void  getAllSeriesConfigurations(Boolean active,
			AsyncCallback<List<ISeriesConfiguration>> asyncCallback);
	public void  getSeriesConfiguration(Long id,
			AsyncCallback<ISeriesConfiguration> asyncCallback);
	public void  processSeriesConfiguration(Long sConfigId,
			AsyncCallback<String> asyncCallback);
	public void  deleteSeriesConfiguration(Long sConfigId,
			AsyncCallback<Boolean> asyncCallback);
	public void  saveSeriesConfiguration(ISeriesConfiguration sConfig,
			AsyncCallback<ISeriesConfiguration> asyncCallback);
	public void getUniversalRounds(int size,
			AsyncCallback<List<UniversalRound>> asyncCallback);
	public void addVirtualComp(AsyncCallback<ICompetition> asyncCallback);
	public void rollBackSeriesConfiguration(Long id, AsyncCallback<ISeriesConfiguration> asyncCallback);
	public void rerunRatingQuery(Long id,
			AsyncCallback<IRatingQuery> asyncCallback);
	public void addRound(Long compId, int uri, String name,
			AsyncCallback<Boolean> asyncCallback);
	public void getAllBlurbs(Boolean active,
			AsyncCallback<List<IBlurb>> asyncCallback);
	public void addBlurb(String url, String linkText, String bodyText,
			AsyncCallback<List<IBlurb>> asyncCallback);
	/**
	 * 
	 * @param url - pass in a url of the form http://xxx.rugby.net/guid
	 * @param 
	 */
	public void getListNameForUrl(String url,
			AsyncCallback<String> asyncCallback);
	public void getDigestPreview(String message, List<Long> blurbIds,
			AsyncCallback<String> asyncCallback);
	public void sendDigestEmail(String message, List<Long> blurbIds,
			AsyncCallback<Integer> asyncCallback);
	public void getDigestUserList(AsyncCallback<String> asyncCallback);
	public void archive(List<Long> blurbIds,
			AsyncCallback<Integer> asyncCallback);
	public void facebook(List<Long> blurbIds,
			AsyncCallback<Integer> asyncCallback);
	public void twitter(List<Long> blurbIds,
			AsyncCallback<List<Long>> asyncCallback);
	public void saveRound(IRound r, AsyncCallback<IRound> asyncCallback);
	public void bulkUploadEmails(List<String> emailsValid, AsyncCallback<List<String>> asyncCallback);

}
