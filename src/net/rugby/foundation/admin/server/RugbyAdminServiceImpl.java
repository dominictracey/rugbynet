package net.rugby.foundation.admin.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;

import net.rugby.foundation.admin.client.RugbyAdminService;
import net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory;
import net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory.CompetitionFetcherType;
import net.rugby.foundation.admin.server.model.IForeignCompetitionFetcher;
import net.rugby.foundation.admin.server.model.ScrumCompetitionFetcher;
import net.rugby.foundation.admin.server.orchestration.AdminOrchestrationTargets;
import net.rugby.foundation.admin.server.orchestration.IOrchestrationConfigurationFactory;
import net.rugby.foundation.admin.server.orchestration.OrchestrationHelper;
import net.rugby.foundation.admin.server.workflow.IWorkflowConfigurationFactory;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.IWorkflowConfiguration;
import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.game1.server.factory.IEntryFactory;
import net.rugby.foundation.game1.server.factory.IMatchEntryFactory;
import net.rugby.foundation.game1.server.factory.IRoundEntryFactory;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.game1.shared.IMatchEntry;
import net.rugby.foundation.game1.shared.IRoundEntry;
import net.rugby.foundation.game1.shared.MatchEntry;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.OrphanedObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class RugbyAdminServiceImpl extends RemoteServiceServlet implements RugbyAdminService{

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
	private static final long serialVersionUID = 1L;
	public RugbyAdminServiceImpl() {

//		ofy = DataStoreFactory.getOfy();
		queuer = new OrchestrationHelper();

	}

	@Inject
	public void setFactories(IAppUserFactory auf, IOrchestrationConfigurationFactory ocf, 
			ICompetitionFactory cf, IEntryFactory ef, 
			IMatchGroupFactory mf, IMatchEntryFactory mef, IRoundEntryFactory ref, IForeignCompetitionFetcherFactory fcff,
			IConfigurationFactory ccf, ITeamGroupFactory tf, IRoundFactory rf, IPlayerFactory pf) {
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
		
//		rf.setFactories(cf, mf);
//		mf.setFactories(rf, tf);
		
		fcff.setFactories(rf, mf);
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
		c.addCompUnderway(comp.getId());
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
		IForeignCompetitionFetcher fetcher = fcff.getForeignCompetitionFetcher(url, CompetitionFetcherType.ESPNSCRUM_BASIC);

		Map<String, ITeamGroup> teams = fetcher.getTeams();

		// do we have this in our database? If so, replace the fetched one with the one that already has an ID
		for (ITeamGroup foundTeam: teams.values()) {
			//Query<Group> team = ofy.query(Group.class).filter("displayName", name);
			ITeamGroup t = tf.find(foundTeam);
			if (t != null) {
				teams.put(foundTeam.getDisplayName(), t);
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
		IForeignCompetitionFetcher fetcher = new ScrumCompetitionFetcher(rf,mf);

		Map<String, IMatchGroup> matches = fetcher.getMatches(url, teams);

		//		for (MatchGroup mg : matches.values()) {
		//			// matches are considered equal if they have the same teams and date
		//			Query<Group> gq = ofy.query(Group.class).filter("displayName",mg.getDisplayName());
		//			if (gq.count() > 0) {
		//				if (((IMatchGroup)gq.get()).getDate().equals(mg.getDate()) ) {
		//					matches.put(mg.getDisplayName(), (MatchGroup)gq.get());
		//				}
		//			}
		//		}
	
		for (IMatchGroup m: matches.values()) {
			IMatchGroup found = mf.find(m);
			if (found != null) {
				matches.put(found.getDisplayName(), found);
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
	public List<ICompetition> getAllComps() {
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

		compList = cf.getAllComps();
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
		return null; //deprecated
//		ArrayList<MatchGroup> ms = new ArrayList<MatchGroup>();
//		IRound r = ofy.get(new Key<Round>(Round.class,roundId));
//		if (r != null) {
//			for (Long mid : r.getMatchIDs()) {
//				ms.add(ofy.get(new Key<MatchGroup>(MatchGroup.class,mid)));
//			}
//		}
//		return ms;
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
			admin.setEmailAddress("d1@d1.com");
			admin.setNickname("d1");
			admin.setSuperadmin(true);
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

		return null; //wfcf.get();
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
		List<Long> compIds = null; //wfcf.get().getUnderwayCompetitions();
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
	public List<String> fetchMatchScore(IMatchGroup match, Long compId, List<String> log) {
		try {
			ICompetition comp = getComp(compId);

			queuer.SpawnMatchOrchestration(AdminOrchestrationActions.MatchActions.FETCH, AdminOrchestrationTargets.Targets.MATCH, match, comp, log);
				
			return log;
		} catch (Throwable e) {
			Logger.getLogger("Admin").log(Level.SEVERE, "fetchScore: " + e.getMessage());
			return null;
		}
	}

	@Override
	public IPlayer getPlayer(Long id) {
		return pf.getById(id);
	}

	@Override
	public IPlayer savePlayer(IPlayer player, String promisedHandle) {
		player = pf.put(player);
		if (!promisedHandle.isEmpty()) {
			PipelineService service = PipelineServiceFactory.newPipelineService();
			try {
				service.submitPromisedValue(promisedHandle, player);
			} catch (NoSuchObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OrphanedObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return player;
	}

}
