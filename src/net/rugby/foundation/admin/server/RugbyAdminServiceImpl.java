package net.rugby.foundation.admin.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;

import net.rugby.foundation.admin.client.RugbyAdminService;
import net.rugby.foundation.admin.client.place.AdminCompPlace.Filter;
import net.rugby.foundation.admin.server.factory.IAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory;
import net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory.CompetitionFetcherType;
import net.rugby.foundation.admin.server.factory.IMatchRatingEngineFactory;
import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.IPlayerMatchInfoFactory;
import net.rugby.foundation.admin.server.factory.IPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.admin.server.model.IForeignCompetitionFetcher;
import net.rugby.foundation.admin.server.model.IMatchRatingEngine;
import net.rugby.foundation.admin.server.model.IPlayerMatchStatsFetcher;
import net.rugby.foundation.admin.server.model.IResultFetcher;
import net.rugby.foundation.admin.server.model.ScrumCompetitionFetcher;
import net.rugby.foundation.admin.server.orchestration.AdminOrchestrationTargets;
import net.rugby.foundation.admin.server.orchestration.IOrchestrationConfigurationFactory;
import net.rugby.foundation.admin.server.orchestration.OrchestrationHelper;
import net.rugby.foundation.admin.server.util.CountryLoader;
import net.rugby.foundation.admin.server.workflow.IWorkflowConfigurationFactory;
import net.rugby.foundation.admin.server.workflow.matchrating.FetchTeamMatchStats;
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings;
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.IWorkflowConfiguration;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema20130713;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.game1.server.factory.IEntryFactory;
import net.rugby.foundation.game1.server.factory.IMatchEntryFactory;
import net.rugby.foundation.game1.server.factory.IRoundEntryFactory;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.game1.shared.IMatchEntry;
import net.rugby.foundation.game1.shared.IRoundEntry;
import net.rugby.foundation.game1.shared.MatchEntry;
import net.rugby.foundation.model.shared.Country;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.ITopTenUser;
import net.rugby.foundation.model.shared.ScrumPlayerMatchStats;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.IMatchResult.ResultType;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.pipeline.Job;
import com.google.appengine.tools.pipeline.JobInfo;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.OrphanedObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.appengine.tools.pipeline.impl.model.JobInstanceRecord;
import com.google.appengine.tools.pipeline.impl.model.JobRecord;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

@Singleton
public class RugbyAdminServiceImpl extends RemoteServiceServlet implements RugbyAdminService {

	//private Objectify ofy;
	private IAppUserFactory auf;
	private IOrchestrationConfigurationFactory ocf;
	private ICompetitionFactory cf;
	private IEntryFactory ef;
	private IMatchGroupFactory mf;
	private IMatchEntryFactory mef;
	private IRoundEntryFactory ref;
	private IForeignCompetitionFetcherFactory fcff;
	private IConfigurationFactory ccf;
	private ITeamGroupFactory tf;
	private IRoundFactory rf;
	private OrchestrationHelper queuer;
	private IPlayerFactory pf;
	private ITeamMatchStatsFactory tmsf;
	private IPlayerMatchStatsFactory pmsf;
	private ICountryFactory countryf;
	private IWorkflowConfigurationFactory wfcf;
	private IResultFetcherFactory srff;
	private IMatchRatingEngineFactory mref;
	private IPlayerMatchRatingFactory pmrf;
	private IAdminTaskFactory atf;
	private IPlayerMatchInfoFactory pmif;
	private IMatchResultFactory mrf;
	private IPlayerMatchStatsFetcherFactory pmsff;
	private IMatchRatingEngineSchemaFactory mresf;
	private ITopTenListFactory ttlf;

	private static final long serialVersionUID = 1L;
	public RugbyAdminServiceImpl() {

		//		ofy = DataStoreFactory.getOfy();
		queuer = new OrchestrationHelper();

	}

	@Inject
	public void setFactories(IAppUserFactory auf, IOrchestrationConfigurationFactory ocf, 
			ICompetitionFactory cf, IEntryFactory ef, 
			IMatchGroupFactory mf, IMatchEntryFactory mef, IRoundEntryFactory ref, IForeignCompetitionFetcherFactory fcff,
			IConfigurationFactory ccf, ITeamGroupFactory tf, IRoundFactory rf, IPlayerFactory pf,
			ITeamMatchStatsFactory tmsf, IPlayerMatchStatsFactory pmsf, ICountryFactory countryf,
			IWorkflowConfigurationFactory wfcf, IResultFetcherFactory srff, IMatchRatingEngineFactory mref, 
			IPlayerMatchRatingFactory pmrf, IAdminTaskFactory atf, IPlayerMatchInfoFactory pmif, IMatchResultFactory mrf, 
			IPlayerMatchStatsFetcherFactory pmsff, IMatchRatingEngineSchemaFactory mresf, ITopTenListFactory ttlf) {
		this.auf = auf;
		this.ocf = ocf;
		this.cf = cf;
		this.ef = ef;
		this.mf = mf;
		this.mef = mef;
		this.ref = ref;
		this.fcff = fcff;
		this.ccf = ccf;
		this.tf = tf;
		this.rf = rf;
		this.pf = pf;
		this.tmsf = tmsf;
		this.pmsf = pmsf;
		this.countryf = countryf;
		this.wfcf = wfcf;
		this.srff = srff;
		this.mref = mref;
		this.pmrf = pmrf;
		this.atf = atf;
		this.pmif = pmif;
		this.mrf = mrf;
		this.pmsff = pmsff;
		this.mresf = mresf;
		this.ttlf = ttlf;
	}

	@Override
	public ICompetition fetchCompetition(String url, List<IRound> rounds, List<ITeamGroup> teams) {
		IForeignCompetitionFetcher fetcher = fcff.getForeignCompetitionFetcher(url, CompetitionFetcherType.ESPNSCRUM_BASIC);

		ICompetition comp = fetcher.getCompetition(url, rounds, teams);

		// do we have this in our database?
		List<ICompetition> comps = cf.getAllComps();
		ICompetition compDB = null;
		for (ICompetition dBComp : comps) {
			if (dBComp.getLongName().equals(comp.getLongName())) {
				cf.setId(comp.getId());
				compDB = dBComp; //cf.getCompetition();//ofy.query(Competition.class).filter("longName", comp.getLongName()).get();				
			}
		}

		if (compDB != null) {
			return compDB;
		} else {
			// add the rounds for them
			ArrayList<Long> rids = new ArrayList<Long>();
			for (IRound r : rounds) {
				rids.add(r.getId());
			}
			comp.setRoundIds(rids);
			comp.setRounds(rounds);
			return comp;
		}
	}

	@Override
	public ICompetition saveCompetition(ICompetition comp, Map<String,ITeamGroup> teams) {

		cf.put(comp);

		// there should just be one...
		ICoreConfiguration c = ccf.get();

		c.addCompetition(comp.getId(), comp.getShortName());
		if (comp.getUnderway()) {
			c.addCompUnderway(comp.getId());
		} else {
			c.removeCompUnderway(comp.getId());
		}
		//c.setDefaultCompId(comp.getId());
		ccf.put(c);

		//add it to the workflow configuration as well
		//		IWorkflowConfiguration wfc = wfcf.get();
		//		boolean found = false;
		//		for (Long id : wfc.getUnderwayCompetitions()) {
		//			if (id.equals(comp.getId())) {
		//				found = true;
		//				break;
		//			}
		//		}
		//		
		//		if (!found) {
		//			wfc.getUnderwayCompetitions().add(comp.getId());
		//			wfcf.put(wfc);
		//		}

		// and a blank orchestration config
		ocf.setId(null);
		ocf.setCompId(comp.getId());
		IOrchestrationConfiguration oc = ocf.get();

		// if we didn't find one, make one
		if (oc == null) {
			ocf.setCompId(null);
			oc = ocf.get();
			assert (oc != null);
			oc.setCompID(comp.getId());
			ocf.put(oc);
		}

		return comp;
	}


	@Override
	public Map<String, ITeamGroup> fetchTeams(String url, String resultType) {
		CountryLoader cloader = new CountryLoader();
		cloader.Run(countryf);

		IForeignCompetitionFetcher fetcher = fcff.getForeignCompetitionFetcher(url, CompetitionFetcherType.ESPNSCRUM_BASIC);

		Map<String, ITeamGroup> teams = fetcher.getTeams();

		// sometimes the points table isn't up yet so we don't get anything back, this is ok.
		if (teams != null && !teams.isEmpty()) {
			// do we have this in our database? If so, replace the fetched one with the one that already has an ID
			for (ITeamGroup foundTeam: teams.values()) {
				//Query<Group> team = ofy.query(Group.class).filter("displayName", name);
				ITeamGroup t = tf.find(foundTeam);
				if (t != null) {
					teams.put(foundTeam.getDisplayName(), t);
				}				
			}
		}

		return teams;

	}


	@Override
	public Map<String, ITeamGroup> saveTeams(Map<String, ITeamGroup> teams) {

		return teams; //deprecated
		//		ofy.put(teams.values());
		//		return teams;
	}


	@Override
	public List<IRound> saveRounds(List<IRound> rounds) {
		return rounds; //deprecated
		//		ofy.put(rounds);
		//		return rounds;
	}


	@Override
	public List<IRound> fetchRounds(String url, Map<String,IMatchGroup> matches) {
		IForeignCompetitionFetcher fetcher = fcff.getForeignCompetitionFetcher(url, CompetitionFetcherType.ESPNSCRUM_BASIC);

		List<IRound>  rounds = fetcher.getRounds(url, matches);

		// do we have this in our database? If so, replace the fetched one with the one that already has an ID
		// a round in the DB is considered the same if it contains the same matches

		Map<Integer,IRound> changes = new HashMap<Integer,IRound>();

		// have to do two-pass to avoid concurrentChangeException
		for (IRound r : rounds) {
			IRound roundDB = rf.find(r);

			if (roundDB != null) {
				changes.put(rounds.indexOf(r), roundDB);
			}
		}

		// now swap in the found round for the in situ one
		for (Integer index : changes.keySet()) {
			rounds.remove((int)index);
			rounds.add(index,changes.get(index));	
		}


		return rounds;
	}


	@Override
	public Map<String, IMatchGroup> fetchMatches(String url, Map<String,ITeamGroup> teams) {
		IForeignCompetitionFetcher fetcher = new ScrumCompetitionFetcher(rf,mf, srff, tf);

		Map<String, IMatchGroup> matches = fetcher.getMatches(url, teams);

		if (matches != null) {
			for (IMatchGroup m: matches.values()) {
				IMatchGroup found = mf.find(m);
				if (found != null) {
					matches.put(found.getDisplayName(), found);
				}
			}
		}

		return matches;
	}


	@Override
	public Map<String, IMatchGroup> saveMatches(Map<String, IMatchGroup> matches) {
		return matches; //deprecated
		//		ofy.put(matches.values());
		//		return matches;
	}


	@Override
	public List<ICompetition> getComps(Filter filter) {
		List<ICompetition> compList = new ArrayList<ICompetition>();
		//		Query<Competition> qc = ofy.query(Competition.class);
		//		for (Competition c : qc) {
		//			compList.add(c);
		//		}


		//		IWorkflowConfiguration wfc = wfcf.get();
		//		
		//		for (Long compId : wfc.getUnderwayCompetitions()) {		
		//			cf.setId(compId);
		//			compList.add((Competition)cf.getCompetition());
		//		}

		if (filter == null) {
			filter = Filter.ALL;
		}

		if (filter.equals(Filter.ALL)) {
			compList = cf.getAllComps();
		} else if (filter.equals(Filter.UNDERWAY)) {
			compList = cf.getUnderwayComps();
		}
		return compList;
	}


	@Override
	public IWorkflowConfiguration saveWorkflowConfig(IWorkflowConfiguration wfc) {
		// there may only be one...
		//		wfcf.put(wfc);
		//
		//		return wfc;
		return null;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#getOrchestrationConfiguration()
	 */
	@Override
	public Map<String, IOrchestrationConfiguration> getOrchestrationConfiguration() {
		Map<String, IOrchestrationConfiguration> newMap = new HashMap<String, IOrchestrationConfiguration>();

		List<IOrchestrationConfiguration> list = ocf.getAll();


		for (IOrchestrationConfiguration oc : list) {
			cf.setId(oc.getCompID());
			newMap.put(cf.getCompetition().getLongName(), oc);
		}

		//		Query<Competition> qc = ofy.query(Competition.class);
		//		for (ICompetition c: qc) {
		//			OrchestrationConfiguration newOc = new OrchestrationConfiguration();
		//			newOc.setCompID(c.getId());
		//			newMap.put(c.getLongName(), newOc);
		//		}
		//		
		//		Query<OrchestrationConfiguration> qoc = ofy.query(OrchestrationConfiguration.class);
		//				
		//		for (OrchestrationConfiguration oc : qoc) {
		//			Key<Competition> kc = new Key<Competition>(Competition.class, oc.getCompID());
		//			ICompetition c = ofy.get(kc);
		//			if (c != null)
		//				newMap.put(c.getLongName(),oc);		
		//		}

		return newMap;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#saveOrchestrationConfiguration(java.util.Map)
	 */
	@Override
	public Map<String, IOrchestrationConfiguration> saveOrchestrationConfiguration(
			Map<String, IOrchestrationConfiguration> configs) {
		// there may only be one set...
		//		Query<OrchestrationConfiguration> qoc = ofy.query(OrchestrationConfiguration.class);
		//		if (qoc.count() > 0)
		//			ofy.delete(qoc);
		//		
		//		ofy.put(configs.values());

		for (IOrchestrationConfiguration oc : configs.values()) {
			ocf.put(oc);
		}
		return configs;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#getTeams(long)
	 */
	@Override
	public List<ITeamGroup> getTeams(Long compId) {
		return null; //deprecated
		//		Query<CompetitionTeam> qct = ofy.query(CompetitionTeam.class).filter("competitionID", compId);
		//		ArrayList<TeamGroup> tgs = new ArrayList<TeamGroup>();
		//
		//		for (ICompetitionTeam ct : qct) {
		//			Key<TeamGroup> ktg = new Key<TeamGroup>(TeamGroup.class,ct.getTeamID());
		//			tgs.add(ofy.get(ktg));
		//		}
		//
		//		return tgs;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#getRounds(long)
	 */
	@Override
	public List<IRound> getRounds(Long compId) {
		return null; //deprecated
		//		ArrayList<Round> rs = new ArrayList<Round>();
		//		Key<Competition> kc = new Key<Competition>(Competition.class, compId);
		//		ICompetition c = ofy.get(kc);
		//		for (Long rid : c.getRoundIds()) {
		//			rs.add(0,ofy.get(new Key<Round>(Round.class,rid)));
		//		}
		//		return rs;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#getRounds(java.lang.Long)
	 */
	@Override
	public List<IMatchGroup> getMatches(Long roundId) {

		List<IMatchGroup> ms = mf.getMatchesForRound(roundId);

		return ms;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#getResults(long)
	 */
	@Override
	public List<IMatchResult> getResults(Long matchId) {
		return null; //deprecated
		//		ArrayList<MatchResult> mrs = new ArrayList<MatchResult>();
		//		Query<MatchResult> qmr = ofy.query(MatchResult.class).filter("matchID",matchId);
		//		for (MatchResult mr : qmr) {
		//			mrs.add(mr);
		//		}
		//		return mrs;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#saveTeam(net.rugby.foundation.model.shared.TeamGroup)
	 */
	@Override
	public ITeamGroup saveTeam(ITeamGroup teamGroup) {
		tf.put(teamGroup);
		return teamGroup;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#getTeam(java.lang.Long)
	 */
	@Override
	public ITeamGroup getTeam(Long teamId) {
		// to allow editing
		tf.setId(teamId);
		return tf.getTeam();

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#createAdmin()
	 */
	@Override
	public Boolean createAdmin() {
		try {
			auf.setId(null);
			IAppUser admin = auf.get();

			admin.setActive(true);
			admin.setAdmin(true);
			admin.setEmailAddress("dominic.tracey@gmail.com");
			admin.setNickname("d1");
			admin.setSuperadmin(true);
			if (admin instanceof ITopTenUser) {
				((ITopTenUser)admin).setTopTenContentContributor(true);
				((ITopTenUser)admin).setTopTenContentEditor(true);
			}
			String pwhash = DigestUtils.md5Hex("asdasd");
			admin.setPwHash(pwhash);
			auf.put(admin);
		} catch (Throwable e) {
			Logger.getLogger("Admin").log(Level.SEVERE, "CreateAdmin: " + e.getMessage());
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#getWorkflowConfiguration()
	 */
	@Override
	public IWorkflowConfiguration getWorkflowConfiguration() {

		return wfcf.get();
	}

	@Override
	public List<String> sanityCheck()
	{
		List<String> changes = new ArrayList<String>();


		//		// does every comp have a CC?
		//		List<ICompetition> comps = cf.getAllComps();
		//		for (ICompetition comp: comps) {
		//			chf.setId(comp.getCompClubhouseId());
		//			IClubhouse ch = chf.get();
		//			if (ch == null) {
		//				chf.setId(null);
		//				ch = chf.get();
		//				ch.setOwnerID(-99L);
		//				ch.setName("Competition Clubhouse for " + comp.getLongName());
		//				ch.setPublicClubhouse(false);
		//				ch.setActive(true);
		//				chf.put(ch);
		//				comp.setCompClubhouseId(ch.getId());
		//				cf.put(comp);
		//				changes.add("Added competition clubhouse for " + comp.getLongName());
		//			}
		//		}

		// for every entry, if for the first round of the competition, they don't have a picklist,
		// create one with picks for all the winning teams.
		List<Long> compIds = wfcf.get().getUnderwayCompetitions();
		for (Long compId: compIds) {
			cf.setId(compId);
			ICompetition comp = cf.getCompetition();
			IRound r = comp.getPrevRound();
			if (r != null) {
				ef.setRoundAndComp(null, comp);
				List<IEntry> entries = ef.getEntries();
				for (IEntry e: entries) {
					IRoundEntry re = e.getRoundEntries().get(r.getId());
					if (re == null) {  // @REX shouldn't the factory or entity do this?!
						re = ref.getNew();

						// last match of the round is the tiebreaker
						re.setTieBreakerMatchId(r.getMatches().get(r.getMatches().size()-1).getId()); 

						re.setRoundId(r.getId());

						e.getRoundEntries().put(r.getId(),re);	
					}
					if (re != null) {
						Map<Long,IMatchEntry> pickMap = re.getMatchPickMap();

						if (pickMap == null || pickMap.isEmpty()) {
							// need to fix
							e.getRoundEntries().get(r.getId()).setMatchPickMap(new HashMap<Long,IMatchEntry>());

							for (IMatchGroup m: r.getMatches()) {
								if (!m.getLocked()) {
									IMatchEntry me = new MatchEntry();
									me.setMatchId(m.getId());

									if (m.getStatus() == Status.FINAL_HOME_WIN) {
										me.setTeamPicked(m.getHomeTeam());
										me.setTeamPickedId(m.getHomeTeamId());
									} else {
										me.setTeamPicked(m.getVisitingTeam());
										me.setTeamPickedId(m.getVisitingTeamId());
									}

									me = mef.put(me);
									e.getRoundEntries().get(r.getId()).getMatchPickMap().put(m.getId(), me);

								} else {
									changes.add("Couldn't add round 1 pick list to entry " + e.getName() + " -- match " + m.getDisplayName() + " (" + m.getId()+ ") is locked.");
								}
							}

							ef.put(e);
							changes.add("Added round 1 pick list to entry " + e.getName());
						}
					}
				}
			}
		}

		return changes;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#saveCompInfo(net.rugby.foundation.model.shared.ICompetition)
	 */
	@Override
	public ICompetition saveCompInfo(ICompetition comp) {
		try {
			comp = cf.put(comp);
			
			ICoreConfiguration cc = ccf.get();
			// add or remove from CoreConfiguration map depending on the Underway flag
			if (comp.getUnderway()) {
				cc.addCompUnderway(comp.getId());
			} else {
				cc.removeCompUnderway(comp.getId());
			}
			
			ccf.put(cc);

			return comp;
		} catch (Throwable e) {
			Logger.getLogger("Admin").log(Level.SEVERE, "saveCompInfo: " + e.getMessage());
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#saveMatch(net.rugby.foundation.model.shared.IMatchGroup)
	 */
	@Override
	public IMatchGroup saveMatch(IMatchGroup matchGroup) {
		try {
			mf.put(matchGroup);
			return matchGroup;
		} catch (Throwable e) {
			Logger.getLogger("Admin").log(Level.SEVERE, "saveMatch: " + e.getMessage());
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#getMatch(java.lang.Long)
	 */
	@Override
	public IMatchGroup getMatch(Long matchId) {
		try {
			mf.setId(matchId);
			return mf.getGame();
		} catch (Throwable e) {
			Logger.getLogger("Admin").log(Level.SEVERE, "saveMatch: " + e.getMessage());
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#lockMatch(java.lang.Boolean, net.rugby.foundation.model.shared.IMatchGroup)
	 */
	@Override
	public List<String> lockMatch(Boolean lock, IMatchGroup match, Long compId, List<String> log) {
		try {
			ICompetition comp = getComp(compId);

			if (lock)
				queuer.SpawnMatchOrchestration(AdminOrchestrationActions.MatchActions.LOCK, AdminOrchestrationTargets.Targets.MATCH, match, comp, log);
			else 
				queuer.SpawnMatchOrchestration(AdminOrchestrationActions.MatchActions.UNLOCK, AdminOrchestrationTargets.Targets.MATCH, match, comp, log);

			return log;
		} catch (Throwable e) {
			Logger.getLogger("Admin").log(Level.SEVERE, "lockMatch: " + e.getMessage());
			return null;
		}
	}

	@Override
	public ICompetition getComp(Long compId) {
		try {

			cf.setId(compId);
			return cf.getCompetition();

		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#lockMatch(java.lang.Boolean, net.rugby.foundation.model.shared.IMatchGroup)
	 */
	@Override
	public IMatchGroup fetchMatchScore(IMatchGroup match, Long compId, List<String> log) {
		try {
			//ICompetition comp = getComp(compId);

			//queuer.SpawnMatchOrchestration(AdminOrchestrationActions.MatchActions.FETCH, AdminOrchestrationTargets.Targets.MATCH, match, comp, log);
			IResultFetcher fetcher = srff.getResultFetcher(compId, null, ResultType.MATCHES);
			IMatchResult result = fetcher.getResult(match);

			return match;
		} catch (Throwable e) {
			Logger.getLogger("Admin").log(Level.SEVERE, "fetchScore: " + e.getMessage(), e);
			return null;
		}
	}

	@Override
	public IPlayer getPlayer(Long id) {
		return pf.getById(id);
	}

	@Override
	public IPlayer savePlayer(IPlayer player, IAdminTask task) {
		player = pf.put(player);
		if (task != null && task.getAction().equals(IAdminTask.Action.EDITPLAYER)) {
			if (task.getPromise() != null) {
				PipelineService service = PipelineServiceFactory.newPipelineService();
				try {
					service.submitPromisedValue(task.getPromise(), player);
				} catch (NoSuchObjectException e) {
					e.printStackTrace();
				} catch (OrphanedObjectException e) {
					e.printStackTrace();
				}
			}

			atf.complete(task);
		}
		return player;
	}

	@Override
	public List<IPlayerMatchStats> testMatchStats(Long matchId) {

		//		PipelineService service = PipelineServiceFactory.newPipelineService();
		//		mf.setId(matchId);
		//		IMatchGroup match = mf.getGame();
		//
		//		CountryLoader cloader = new CountryLoader();
		//		cloader.Run(countryf);
		//
		//		if (match == null) {
		//
		//
		//
		//			for (Long id = 9001L; id<9003L; ++id) {
		//				tf.setId(null);
		//				ITeamGroup t = tf.getTeam();
		//				if (id == 9001) {
		//					t.setAbbr("NZL");
		//					t.setShortName("All Blacks");
		//					((IGroup)t).setDisplayName("New Zealand");
		//					t.setColor("#000000");
		//				} else if (id == 9002) {
		//					t.setAbbr("AUS");
		//					t.setShortName("Wallabies");
		//					((IGroup)t).setDisplayName("Australia");
		//					t.setColor("#f0af00");
		//				}
		//				((TeamGroup)t).setId(id);
		//				((IGroup)t).setGroupType(GroupType.TEAM);
		//
		//				tf.put(t);
		//			}
		//
		//
		//			Calendar cal = new GregorianCalendar();
		//			cal.setTime(new Date());
		//			mf.setId(null);
		//			IMatchGroup g = mf.getGame();
		//			((MatchGroup)g).setId(300L);
		//			((IGroup)g).setGroupType(GroupType.MATCH);
		//
		//			g.setHomeTeamId(9001L);
		//			g.setVisitingTeamId(9002L);
		//			g.setLocked(true);
		//			g.setForeignId(93503L);
		//			g.setForeignUrl("http://www.espnscrum.com/scrum/rugby/current/match/93503.html?view=scorecard");
		//			cal.set(2011, 10, 16);
		//			g.setStatus(Status.COMPLETE_AWAITING_RESULTS);
		//			tf.setId(g.getHomeTeamId());
		//			g.setHomeTeam(tf.getTeam());
		//			tf.setId(g.getVisitingTeamId());
		//			g.setVisitingTeam(tf.getTeam());
		//			g.setDisplayName();	
		//
		//			g.setDate(cal.getTime());
		//
		//			mf.put(g);
		//			match = g;
		//		}
		//
		//		String pipelineId = "";
		//		try {
		//
		//			pipelineId = service.startNewPipeline(new GenerateMatchRatings(), match, new JobSetting.MaxAttempts(1));
		//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "pipelineId: " + pipelineId);
		//
		//			while (true) {
		//				Thread.sleep(2000);
		//				JobInfo jobInfo = service.getJobInfo(pipelineId);
		//				switch (jobInfo.getJobState()) {
		//				case COMPLETED_SUCCESSFULLY:
		//					//service.deletePipelineRecords(pipelineId);
		//					return (List<IPlayerMatchStats>) jobInfo.getOutput();
		//				case RUNNING:
		//					break;
		//				case STOPPED_BY_ERROR:
		//					throw new RuntimeException("Job stopped " + jobInfo.getError());
		//				case STOPPED_BY_REQUEST:
		//					throw new RuntimeException("Job stopped by request.");
		//				}
		//			}
		//		} catch (Exception e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//			return null;
		//		}

		return (List<IPlayerMatchStats>) pmsf.getByMatchId(matchId);
	}

	@Override
	public List<IPlayerMatchInfo> getPlayerMatchInfo(Long matchId) {
		IMatchRatingEngineSchema schema = mresf.getDefault();
		if (schema != null && matchId != null) {
			return pmif.getForMatch(matchId, schema); 
		} else {
			return null;
		}
	}

	@Override
	public List<ICountry> fetchCountryList() {
		List<ICountry> list = new ArrayList<ICountry>();
		Iterator<Country> it = countryf.getAll();

		if (it == null || !it.hasNext()) {
			CountryLoader cloader = new CountryLoader();
			cloader.Run(countryf);
			it = countryf.getAll();
		}

		while (it.hasNext()) {
			Country c = it.next();
			list.add(c);
		}
		return list;
	}

	@Override
	public List<position> fetchPositionList() {
		List<position> list = new ArrayList<position>();

		for (position p : position.values()) {
			list.add(p);
		}

		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IPlayerMatchInfo> fetchMatchStats(Long matchId) {
		Country c = new Country(5000L, "None", "NONE", "---", "Unassigned");
		countryf.put(c);

		PipelineService service = PipelineServiceFactory.newPipelineService();
		mf.setId(matchId);
		IMatchGroup match = mf.getGame();

		String pipelineId = "";
		try {

			// first check if this match already has a pipeline going and kill it if it does
			if (match.getFetchMatchStatsPipelineId() != null && !match.getFetchMatchStatsPipelineId().isEmpty()) {
				// delete adminTasks first
				List<? extends IAdminTask> tasks = atf.getForPipeline(match.getFetchMatchStatsPipelineId());
				atf.delete((List<IAdminTask>) tasks);

				// now the pipeline records
				service.deletePipelineRecords(match.getFetchMatchStatsPipelineId(), true, false);
				match.setFetchMatchStatsPipelineId(null);

			}

			//pipelineId = service.startNewPipeline(new GenerateMatchRatings(pf, tmsf, pmsf, countryf, mref, pmrf), match, new JobSetting.MaxAttempts(1));
			pipelineId = service.startNewPipeline(new GenerateMatchRatings(), match, new JobSetting.MaxAttempts(3));
			match.setFetchMatchStatsPipelineId(pipelineId);
			mf.put(match);

			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "pipelineId: " + pipelineId);

			while (true) {
				Thread.sleep(2000);
				JobInfo jobInfo = service.getJobInfo(pipelineId);
				switch (jobInfo.getJobState()) {
				case COMPLETED_SUCCESSFULLY:
					service.deletePipelineRecords(pipelineId);
					return getPlayerMatchInfo(matchId); // (List<IPlayerMatchStats>) jobInfo.getOutput();
				case RUNNING:
					break;
				case STOPPED_BY_ERROR:
					throw new RuntimeException("Job stopped " + jobInfo.getError());
				case STOPPED_BY_REQUEST:
					throw new RuntimeException("Job stopped by request.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public IPlayerMatchStats getPlayerMatchStats(Long id) {
		return pmsf.getById(id);
	}

	@Override
	public IPlayerMatchInfo savePlayerMatchStats(IPlayerMatchStats stats, IAdminTask task) {
		pmsf.put(stats);
		if (task != null  && task.getAction().equals(IAdminTask.Action.EDITPLAYERMATCHSTATS)) {
			if (task.getPromise() != null) {
				PipelineService service = PipelineServiceFactory.newPipelineService();
				try {
					service.submitPromisedValue(task.getPromise(), stats);
				} catch (NoSuchObjectException e) {
					e.printStackTrace();
				} catch (OrphanedObjectException e) {
					e.printStackTrace();
				}
			}
			atf.complete(task);
		}
		return pmif.getForPlayerMatchStats(stats.getId(),null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IAdminTask> getAllOpenAdminTasks() {
		return (List<IAdminTask>) atf.getAllOpen();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IAdminTask> deleteTasks(List<IAdminTask> selectedItems) {
		return (List<IAdminTask>) atf.delete(selectedItems);
	}

	@Override
	public IAdminTask getTask(Long id) {
		return atf.get(id);
	}

	@Override
	public ICompetition repairComp(ICompetition comp) {
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Service call equested repair comp " + comp.getLongName());

		return cf.repair(comp);
	}

	@Override
	public IPlayerMatchStats refetchPlayerMatchStats(IPlayerMatchStats pms) {
		IPlayer player = pf.getById(pms.getPlayerId());
		mf.setId(pms.getMatchId());
		IMatchGroup match = mf.getGame();

		Home_or_Visitor side = Home_or_Visitor.HOME;
		if (pms.getTeamId().equals(match.getVisitingTeamId())) {
			side = Home_or_Visitor.VISITOR;
		}

		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(player, match, side, pms.getSlot(), match.getForeignUrl()+"?view=scorecard");
		if (!fetcher.process()) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Problem getting player match stats for " + player.getDisplayName() + " in match " + match.getDisplayName() + " : " + fetcher.getErrorMessage());			
		}

		return fetcher.getStats();
	}

	@Override
	public List<IPlayerMatchInfo> aggregatePlayerMatchRatings(Long compId,
			Long roundId, position posi, Long countryId, Long teamId) {
		return pmif.query(compId, roundId, posi, countryId, teamId, null);
	}

	@Override
	public List<IPlayerMatchInfo> reRateMatch(Long matchId) {
		IMatchRatingEngineSchema mres = mresf.getDefault();
		assert (mres != null);
		IMatchRatingEngine mre = mref.get(mres);
		assert (mre != null);
		mf.setId(matchId);
		IMatchGroup m = mf.getGame();

		List<IPlayerMatchStats> stats = pmsf.getByMatchId(matchId);
		List<IPlayerMatchStats> homePlayerStats =  new ArrayList<IPlayerMatchStats>();
		List<IPlayerMatchStats> visitorPlayerStats =  new ArrayList<IPlayerMatchStats>();
		for (IPlayerMatchStats s : stats) {
			if (s.getTeamId().equals(m.getHomeTeamId())) {
				homePlayerStats.add(s);
			} else {
				visitorPlayerStats.add(s);
			}
		}

		mre.setPlayerStats(homePlayerStats, visitorPlayerStats);

		// now the team stats
		ITeamMatchStats hStats = tmsf.getHomeStats(m);
		ITeamMatchStats vStats = tmsf.getVisitStats(m);

		mre.setTeamStats(hStats, vStats);		

		mre.generate(mres, m);

		return pmif.getForMatch(matchId, mres);
	}

	@Override
	public ITeamMatchStats getTeamMatchStats(Long matchId, Long teamId) {
		mf.setId(matchId);
		IMatchGroup m = mf.getGame();

		if (m != null) {
			if (m.getHomeTeamId().equals(teamId)) {
				return tmsf.getHomeStats(m);
			} else {
				return tmsf.getVisitStats(m);
			}
		} else {
			return null;  // couldn't find match
		}
	}

	/**
	 * Trying a different approach here, just try to re-run a new Pipeline with just the fetching task in it.
	 */
	@Override
	public ITeamMatchStats refetchTeamMatchStats(ITeamMatchStats tms) {
		tf.setId(tms.getTeamId());
		ITeamGroup team = tf.getTeam();


		Country c = new Country(5000L, "None", "NONE", "---", "Unassigned");
		countryf.put(c);

		PipelineService service = PipelineServiceFactory.newPipelineService();
		mf.setId(tms.getMatchId());
		IMatchGroup match = mf.getGame();

		Home_or_Visitor hov = Home_or_Visitor.VISITOR;
		if (match.getHomeTeamId().equals(team.getId())) {
			hov = Home_or_Visitor.HOME;
		}

		String pipelineId = "";
		try {

			//pipelineId = service.startNewPipeline(new GenerateMatchRatings(pf, tmsf, pmsf, countryf, mref, pmrf), match, new JobSetting.MaxAttempts(1));
			pipelineId = service.startNewPipeline(new FetchTeamMatchStats(), team, match, hov, match.getForeignUrl(), new JobSetting.MaxAttempts(3));
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "pipelineId: " + pipelineId);

			while (true) {
				Thread.sleep(2000);
				JobInfo jobInfo = service.getJobInfo(pipelineId);
				switch (jobInfo.getJobState()) {
				case COMPLETED_SUCCESSFULLY:
					service.deletePipelineRecords(pipelineId);
					return getTeamMatchStats(match.getId(),tms.getTeamId()); // (List<IPlayerMatchStats>) jobInfo.getOutput();
				case RUNNING:
					break;
				case STOPPED_BY_ERROR:
					throw new RuntimeException("Job stopped " + jobInfo.getError());
				case STOPPED_BY_REQUEST:
					throw new RuntimeException("Job stopped by request.");
				case WAITING_TO_RETRY:
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ITeamMatchStats saveTeamMatchStats(ITeamMatchStats tms,
			IAdminTask task) {
		tmsf.put(tms);
		if (task != null  && task.getAction().equals(IAdminTask.Action.EDITTEAMMATCHSTATS)) {
			if (task.getPromise() != null) {
				PipelineService service = PipelineServiceFactory.newPipelineService();
				try {
					service.submitPromisedValue(task.getPromise(), tms);
				} catch (NoSuchObjectException e) {
					e.printStackTrace();
				} catch (OrphanedObjectException e) {
					e.printStackTrace();
				}
			}
			atf.complete(task);
		}
		return tms;
	}

	@Override
	public ScrumMatchRatingEngineSchema saveMatchRatingEngineSchema(
			ScrumMatchRatingEngineSchema schema) {
		mresf.put(schema);
		return schema;
	}

	@Override
	public ScrumMatchRatingEngineSchema getMatchRatingEngineSchema(Long schemaId) {
		IMatchRatingEngineSchema schema = mresf.getById(schemaId);
		if (schema instanceof ScrumMatchRatingEngineSchema) {
			return (ScrumMatchRatingEngineSchema) schema;
		} else {
			return null;
		}
	}

	@Override
	public ScrumMatchRatingEngineSchema saveMatchRatingEngineSchemaAsCopy(
			ScrumMatchRatingEngineSchema schema) {
		schema.setId(null);
		schema.setIsDefault(false);
		mresf.put(schema);
		return schema;
	}

	@Override
	public Boolean deleteMatchRatingEngineSchema(
			ScrumMatchRatingEngineSchema20130713 schema) {
		return mresf.delete(schema);
	}

	@Override
	public Boolean deleteRatingsForMatchRatingEngineSchema(
			ScrumMatchRatingEngineSchema20130713 schema) {
		pmrf.deleteForSchema(schema);
		return null;
	}

	@Override
	public ScrumMatchRatingEngineSchema setMatchRatingEngineSchemaAsDefault(
			ScrumMatchRatingEngineSchema20130713 schema) {
		IMatchRatingEngineSchema s2 = mresf.setAsDefault(schema);
		if (s2 instanceof ScrumMatchRatingEngineSchema)
			return (ScrumMatchRatingEngineSchema) s2;
		else 
			return null; // not sure what this would be
	}

	@Override
	public List<ScrumMatchRatingEngineSchema> getScrumSchemaList() {
		return mresf.getScrumList();
	}

	@Override
	public Boolean flushAllPipelineJobs() {
		try {
			PipelineService service = PipelineServiceFactory.newPipelineService();

			List<? extends IMatchGroup> list = mf.getMatchesWithPipelines();
			Iterator<? extends IMatchGroup> it = list.iterator();
			while (it.hasNext()) {
				IMatchGroup m = it.next();
				String id = m.getFetchMatchStatsPipelineId();
				m.setFetchMatchStatsPipelineId(null);
				mf.put(m);
				try {
					service.deletePipelineRecords(id,true,false);
				} catch (NoSuchObjectException nsox) {
					// it's ok, just was a dangling reference in the match record
				}

			}

			return true;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "flushAllPipelineJobs " + ex.getLocalizedMessage());		
			return false;
		}
	}

	@Override
	public Boolean deleteComp(Long id) {
		try {
			return cf.delete(id);
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "deleteComp " + ex.getLocalizedMessage());		
			return false;
		}
	}

	@Override
	public TopTenSeedData createTopTenList(TopTenSeedData tti) {
		try {
			ttlf.create(tti);
			return tti;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "createTopTenList " + ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public Boolean setCompAsDefault(Long compId) {
		try {
			ICoreConfiguration cc = ccf.get();
			cc.setDefaultCompId(compId);
			cc = ccf.put(cc);
			if (cc != null && cc.getDefaultCompId() != null) {
				return cc.getDefaultCompId().equals(compId);
			} else {
				return false;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "createTopTenList " + ex.getMessage(), ex);		
			return false;
		}
	}



}
