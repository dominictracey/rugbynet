package net.rugby.foundation.admin.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import net.rugby.foundation.admin.client.RugbyAdminService;
import net.rugby.foundation.admin.client.place.AdminCompPlace.Filter;
import net.rugby.foundation.admin.server.factory.IAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory;
import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.IPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.admin.server.factory.IStandingsFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.admin.server.model.IForeignCompetitionFetcher;
import net.rugby.foundation.admin.server.model.IPlayerMatchStatsFetcher;
import net.rugby.foundation.admin.server.model.IResultFetcher;
import net.rugby.foundation.admin.server.model.IStandingsFetcher;
import net.rugby.foundation.admin.server.orchestration.AdminOrchestrationTargets;
import net.rugby.foundation.admin.server.orchestration.IOrchestrationConfigurationFactory;
import net.rugby.foundation.admin.server.orchestration.OrchestrationHelper;
import net.rugby.foundation.admin.server.util.CountryLoader;
import net.rugby.foundation.admin.server.workflow.IWorkflowConfigurationFactory;
import net.rugby.foundation.admin.server.workflow.matchrating.FetchTeamMatchStats;
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings;
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.RatingActions;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.IRatingEngineSchema;
import net.rugby.foundation.admin.shared.IWorkflowConfiguration;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema20130713;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.core.server.factory.ICachingFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IContentFactory;
import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.game1.server.factory.IEntryFactory;
import net.rugby.foundation.game1.server.factory.IMatchEntryFactory;
import net.rugby.foundation.game1.server.factory.IRoundEntryFactory;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.game1.shared.IMatchEntry;
import net.rugby.foundation.game1.shared.IRoundEntry;
import net.rugby.foundation.game1.shared.MatchEntry;
import net.rugby.foundation.model.shared.CoreConfiguration.Environment;
import net.rugby.foundation.model.shared.Country;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IMatchResult.ResultType;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ISimpleScoreMatchResult;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.ITopTenUser;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Builder;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.pipeline.JobInfo;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.OrphanedObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class RugbyAdminServiceImpl extends RemoteServiceServlet implements RugbyAdminService {

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
	private IAdminTaskFactory atf;
	private IMatchResultFactory mrf;
	private IPlayerMatchStatsFetcherFactory pmsff;
	private IMatchRatingEngineSchemaFactory mresf;
	private ITopTenListFactory ttlf;
	private ICachingFactory<IContent> ctf;
	private IStandingFactory sf;
	private IStandingsFetcherFactory sff;
	private IUrlCacher uc;
	private IRatingQueryFactory rqf;
	private IPlayerRatingFactory prf;

	private static final long serialVersionUID = 1L;
	public RugbyAdminServiceImpl() {
		try {
			queuer = new OrchestrationHelper();

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
		}
	}

	@Inject
	public void setFactories(IAppUserFactory auf, IOrchestrationConfigurationFactory ocf, 
			ICompetitionFactory cf, IEntryFactory ef, 
			IMatchGroupFactory mf, IMatchEntryFactory mef, IRoundEntryFactory ref, IForeignCompetitionFetcherFactory fcff,
			IConfigurationFactory ccf, ITeamGroupFactory tf, IRoundFactory rf, IPlayerFactory pf,
			ITeamMatchStatsFactory tmsf, IPlayerMatchStatsFactory pmsf, ICountryFactory countryf,
			IWorkflowConfigurationFactory wfcf, IResultFetcherFactory srff,  
			IAdminTaskFactory atf, IMatchResultFactory mrf, 
			IPlayerMatchStatsFetcherFactory pmsff, IMatchRatingEngineSchemaFactory mresf, ITopTenListFactory ttlf, 
			ICachingFactory<IContent> ctf, IStandingFactory sf, IStandingsFetcherFactory sff,
			IUrlCacher uc, IRatingQueryFactory rqf, IPlayerRatingFactory prf) {
		try {
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
			this.atf = atf;
			this.mrf = mrf;
			this.pmsff = pmsff;
			this.mresf = mresf;
			this.ttlf = ttlf;
			this.ctf = ctf;
			this.sf = sf;
			this.sff = sff;
			this.uc = uc;
			this.rqf = rqf;
			this.prf = prf;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
		}
	}

	private boolean checkAdmin() {
		try {
			if (UserServiceFactory.getUserService().isUserAdmin()) {
				return true;
			} else {
				return false;
			}
			//			IAppUser u = getAppUser();
			//			if (u != null && u.isAdmin()) {
			//				return true;
			//			} else {
			//				String user = "a not logged on user ";
			//				if (u != null) {
			//					user = "The user " + u.getEmailAddress() + " (" + u.getId() + ")";
			//				}
			//				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, user + " is trying to access admin functions");
			//				return false;
			//			}
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return false;
		}
	}



	//	private IAppUser getAppUser()
	//	{
	//		try {
	////			UserService service =UserServiceFactory.getUserService();
	////			User u = service.getCurrentUser();
	//			
	//			// do they have a session going?
	//			HttpServletRequest request = this.getThreadLocalRequest();
	//			HttpSession session = request.getSession(false);
	//			if (session != null) {
	//				LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
	//				if (loginInfo.isLoggedIn()) {
	//					auf.setEmail(loginInfo.getEmailAddress());
	//					return auf.get();
	//				}
	//			}
	//			return null;
	//		} catch (Throwable e) {
	//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
	//			return null;
	//		}
	//	}

	@Override
	public ICompetition fetchCompetition(String url, List<IRound> rounds, List<ITeamGroup> teams, CompetitionType compType) {
		try {
			if (checkAdmin()) {
				IForeignCompetitionFetcher fetcher = fcff.getForeignCompetitionFetcher(url, compType);

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
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}



	@Override
	public ICompetition saveCompetition(ICompetition comp, Map<String,ITeamGroup> teams) {
		try {
			if (checkAdmin()) {
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
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}


	@Override
	public Map<String, ITeamGroup> fetchTeams(String url, CompetitionType compType) {
		try {
			if (checkAdmin()) {

				CountryLoader cloader = new CountryLoader();
				cloader.Run(countryf);

				IForeignCompetitionFetcher fetcher = fcff.getForeignCompetitionFetcher(url, compType);

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
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	//
	//	@Override
	//	public Map<String, ITeamGroup> saveTeams(Map<String, ITeamGroup> teams) {
	//		try {
	//			if (checkAdmin()) {
	//		return teams; //deprecated
	//		//		ofy.put(teams.values());
	//		//		return teams;
	//	}

	//
	//	@Override
	//	public List<IRound> saveRounds(List<IRound> rounds) {
	//		return rounds; //deprecated
	//		//		ofy.put(rounds);
	//		//		return rounds;
	//	}


	@Override
	public List<IRound> fetchRounds(String url, Map<String,IMatchGroup> matches, CompetitionType compType) {
		try {
			if (checkAdmin()) {
				IForeignCompetitionFetcher fetcher = fcff.getForeignCompetitionFetcher(url, compType);

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
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}


	@Override
	public Map<String, IMatchGroup> fetchMatches(String url, Map<String,ITeamGroup> teams, CompetitionType compType) {
		try {
			if (checkAdmin()) {
				//				IForeignCompetitionFetcher fetcher = new ScrumCompetitionFetcher(rf,mf, srff, tf);
				IForeignCompetitionFetcher fetcher = fcff.getForeignCompetitionFetcher(url, compType);
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
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	//
	//	@Override
	//	public Map<String, IMatchGroup> saveMatches(Map<String, IMatchGroup> matches) {
	//		return matches; //deprecated
	//		//		ofy.put(matches.values());
	//		//		return matches;
	//	}


	@Override
	public List<ICompetition> getComps(Filter filter) {
		try {
			if (checkAdmin()) {
				List<ICompetition> compList = new ArrayList<ICompetition>();

				if (filter == null) {
					filter = Filter.ALL;
				}

				if (filter.equals(Filter.ALL)) {
					compList = cf.getAllComps();
				} else if (filter.equals(Filter.UNDERWAY)) {
					compList = cf.getUnderwayComps();
				}
				return compList;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}


	@Override
	public IWorkflowConfiguration saveWorkflowConfig(IWorkflowConfiguration wfc) {
		try {
			if (checkAdmin()) {
				return null;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#getOrchestrationConfiguration()
	 */
	@Override
	public Map<String, IOrchestrationConfiguration> getOrchestrationConfiguration() {
		Map<String, IOrchestrationConfiguration> newMap = new HashMap<String, IOrchestrationConfiguration>();
		try {
			if (checkAdmin()) {
				List<IOrchestrationConfiguration> list = ocf.getAll();


				for (IOrchestrationConfiguration oc : list) {
					cf.setId(oc.getCompID());
					newMap.put(cf.getCompetition().getLongName(), oc);
				}

				return newMap;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#saveOrchestrationConfiguration(java.util.Map)
	 */
	@Override
	public Map<String, IOrchestrationConfiguration> saveOrchestrationConfiguration(
			Map<String, IOrchestrationConfiguration> configs) {
		try {
			if (checkAdmin()) {
				for (IOrchestrationConfiguration oc : configs.values()) {
					ocf.put(oc);
				}
				return configs;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#getRounds(java.lang.Long)
	 */
	@Override
	public List<IMatchGroup> getMatches(Long roundId) {
		try {
			if (checkAdmin()) {
				List<IMatchGroup> ms = mf.getMatchesForRound(roundId);

				return ms;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#saveTeam(net.rugby.foundation.model.shared.TeamGroup)
	 */
	@Override
	public ITeamGroup saveTeam(ITeamGroup teamGroup) {
		try {
			if (checkAdmin()) {
				tf.put(teamGroup);
				return teamGroup;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#getTeam(java.lang.Long)
	 */
	@Override
	public ITeamGroup getTeam(Long teamId) {
		try {
			if (checkAdmin()) {
				// to allow editing
				return tf.get(teamId);
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#createAdmin()
	 */
	@Override
	public Boolean createAdmin() {
		try {
			if (checkAdmin()) {
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

				return true;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#getWorkflowConfiguration()
	 */
	@Override
	public IWorkflowConfiguration getWorkflowConfiguration() {
		try {
			if (checkAdmin()) {
				return wfcf.get();
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public List<String> sanityCheck()
	{
		try {
			if (checkAdmin()) {
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
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#saveCompInfo(net.rugby.foundation.model.shared.ICompetition)
	 */
	@Override
	public ICompetition saveCompInfo(ICompetition comp) {
		try {
			if (checkAdmin()) {

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
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#saveMatch(net.rugby.foundation.model.shared.IMatchGroup)
	 */
	@Override
	public IMatchGroup saveMatch(IMatchGroup matchGroup) {
		try {
			if (checkAdmin()) {
				mf.put(matchGroup);
				return matchGroup;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#getMatch(java.lang.Long)
	 */
	@Override
	public IMatchGroup getMatch(Long matchId) {
		try {
			if (checkAdmin()) {
				return mf.get(matchId);
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#lockMatch(java.lang.Boolean, net.rugby.foundation.model.shared.IMatchGroup)
	 */
	@Override
	public List<String> lockMatch(Boolean lock, IMatchGroup match, Long compId, List<String> log) {
		try {
			if (checkAdmin()) {
				ICompetition comp = getComp(compId);

				if (lock)
					queuer.SpawnMatchOrchestration(AdminOrchestrationActions.MatchActions.LOCK, AdminOrchestrationTargets.Targets.MATCH, match, comp, log);
				else 
					queuer.SpawnMatchOrchestration(AdminOrchestrationActions.MatchActions.UNLOCK, AdminOrchestrationTargets.Targets.MATCH, match, comp, log);

				return log;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public ICompetition getComp(Long compId) {
		try {
			if (checkAdmin()) {
				cf.setId(compId);
				return cf.getCompetition();

			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.client.RugbyAdminService#lockMatch(java.lang.Boolean, net.rugby.foundation.model.shared.IMatchGroup)
	 */
	@Override
	public IMatchGroup fetchMatchScore(IMatchGroup match, Long compId, List<String> log) {
		try {
			if (checkAdmin()) {
				//ICompetition comp = getComp(compId);

				//queuer.SpawnMatchOrchestration(AdminOrchestrationActions.MatchActions.FETCH, AdminOrchestrationTargets.Targets.MATCH, match, comp, log);
				IResultFetcher fetcher = srff.getResultFetcher(compId, null, ResultType.MATCHES);
				fetcher.getResult(match);

				return match;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public IPlayer getPlayer(Long id) {
		try {
			if (checkAdmin()) {
				return pf.get(id);
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public IPlayer savePlayer(IPlayer player, IAdminTask task) {
		try {
			if (checkAdmin()) {
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
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public List<IPlayerMatchStats> testMatchStats(Long matchId) {
		try {
			if (checkAdmin()) {
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
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public List<IPlayerRating> getPlayerMatchInfo(Long matchId) {
		try {
			if (checkAdmin()) {
				IRatingEngineSchema schema = mresf.getDefault();
				if (schema != null && matchId != null) {
					return prf.getForMatch(matchId, schema); 
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public List<ICountry> fetchCountryList() {
		try {
			if (checkAdmin()) {
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
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public List<position> fetchPositionList() {
		try {
			if (checkAdmin()) {
				List<position> list = new ArrayList<position>();

				for (position p : position.values()) {
					list.add(p);
				}

				return list;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String fetchMatchStats(Long matchId) {
		try {
			if (checkAdmin()) {
				Country c = new Country(5000L, "None", "NONE", "---", "Unassigned");
				countryf.put(c);

				PipelineService service = PipelineServiceFactory.newPipelineService();

				IMatchGroup match = mf.get(matchId);

				String pipelineId = "";

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

				//				while (true) {
				//					Thread.sleep(2000);

				//				}

				return pipelineId;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}

	}

	@Override
	public IPlayerMatchStats getPlayerMatchStats(Long id) {
		try {
			if (checkAdmin()) {
				return pmsf.getById(id);
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public IPlayerRating savePlayerMatchStats(IPlayerMatchStats stats, IAdminTask task) {
		try {
			if (checkAdmin()) {
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
				IPlayerRating pr = prf.create();
				pr.addMatchStats(stats);
				return pr;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IAdminTask> getAllOpenAdminTasks() {
		try {
			if (checkAdmin()) {
				return (List<IAdminTask>) atf.getAllOpen();
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IAdminTask> deleteTasks(List<IAdminTask> selectedItems) {
		try {
			if (checkAdmin()) {
				return (List<IAdminTask>) atf.delete(selectedItems);
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public IAdminTask getTask(Long id) {
		try {
			if (checkAdmin()) {
				return atf.get(id);
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public ICompetition repairComp(ICompetition comp) {
		try {
			if (checkAdmin()) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Service call equested repair comp " + comp.getLongName());

				return cf.repair(comp);
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public IPlayerMatchStats refetchPlayerMatchStats(IPlayerMatchStats pms) {
		try {
			if (checkAdmin()) {
				IPlayer player = pf.get(pms.getPlayerId());

				IMatchGroup match = mf.get(pms.getMatchId());

				Home_or_Visitor side = Home_or_Visitor.HOME;
				if (pms.getTeamId().equals(match.getVisitingTeamId())) {
					side = Home_or_Visitor.VISITOR;
				}

				IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(player, match, side, pms.getSlot(), match.getForeignUrl()+"?view=scorecard");
				if (!fetcher.process()) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Problem getting player match stats for " + player.getDisplayName() + " in match " + match.getDisplayName() + " : " + fetcher.getErrorMessage());			
				}

				return fetcher.getStats();
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public IRatingQuery createRatingQuery(List<Long> compIds,
			List<Long> roundIds, List<position> posis, List<Long> countryIds, List<Long> teamIds,
			Boolean scaleTime, Boolean scaleComp, Boolean scaleStanding) {
		try {
			if (checkAdmin()) {

				// put the query in the database
				IRatingQuery rq = rqf.create();
				rq.getCompIds().addAll(compIds);
				rq.getRoundIds().addAll(roundIds);
				rq.getPositions().addAll(posis);
				rq.getCountryIds().addAll(countryIds);
				rq.getTeamIds().addAll(teamIds);
				rq.setScaleTime(scaleTime);
				rq.setScaleComp(scaleComp);
				rq.setScaleStanding(scaleStanding);
				
				// check that we have all the comps for the rounds specified, in multi-comp queries this info is missing
				for (Long rid : rq.getRoundIds()) {
					rf.setId(rid);
					IRound r = rf.getRound();
					if (r != null) {
						if (!rq.getCompIds().contains(r.getCompId())) {
							rq.getCompIds().add(r.getCompId());
						}
					}
				}
				rq = rqf.put(rq);

				// now trigger the backend processing task
				Queue queue = QueueFactory.getDefaultQueue();
				TaskOptions to = Builder.withUrl("/admin/orchestration/IRatingQuery").
						param(AdminOrchestrationActions.RatingActions.getKey(), RatingActions.GENERATE.toString()).
						param(AdminOrchestrationTargets.Targets.getKey(), AdminOrchestrationTargets.Targets.RATING.toString()).
						param("id",rq.getId().toString()).
						param("extraKey", "0L");

				queue.add(to);	

				return rq;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}


	@Override
	public ITeamMatchStats getTeamMatchStats(Long matchId, Long teamId) {
		try {
			if (checkAdmin()) {
				IMatchGroup m = mf.get(matchId);

				if (m != null) {
					if (m.getHomeTeamId().equals(teamId)) {
						return tmsf.getHomeStats(m);
					} else {
						return tmsf.getVisitStats(m);
					}
				} else {
					return null;  // couldn't find match
				}
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	/**
	 * Trying a different approach here, just try to re-run a new Pipeline with just the fetching task in it.
	 */
	@Override
	public ITeamMatchStats refetchTeamMatchStats(ITeamMatchStats tms) {
		try {
			if (checkAdmin()) {
				ITeamGroup team = tf.get(tms.getTeamId());


				Country c = new Country(5000L, "None", "NONE", "---", "Unassigned");
				countryf.put(c);

				PipelineService service = PipelineServiceFactory.newPipelineService();

				IMatchGroup match = mf.get(tms.getMatchId());

				Home_or_Visitor hov = Home_or_Visitor.VISITOR;
				if (match.getHomeTeamId().equals(team.getId())) {
					hov = Home_or_Visitor.HOME;
				}

				String pipelineId = "";

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
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public ITeamMatchStats saveTeamMatchStats(ITeamMatchStats tms, IAdminTask task) {
		try {
			if (checkAdmin()) {
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
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public ScrumMatchRatingEngineSchema saveMatchRatingEngineSchema(ScrumMatchRatingEngineSchema schema) {
		try {
			if (checkAdmin()) {
				mresf.put(schema);
				return schema;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public ScrumMatchRatingEngineSchema getMatchRatingEngineSchema(Long schemaId) {
		try {
			if (checkAdmin()) {
				IRatingEngineSchema schema = mresf.getById(schemaId);
				if (schema instanceof ScrumMatchRatingEngineSchema) {
					return (ScrumMatchRatingEngineSchema) schema;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public ScrumMatchRatingEngineSchema saveMatchRatingEngineSchemaAsCopy(ScrumMatchRatingEngineSchema schema) {
		try {
			if (checkAdmin()) {
				schema.setId(null);
				schema.setIsDefault(false);
				mresf.put(schema);
				return schema;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public Boolean deleteMatchRatingEngineSchema(ScrumMatchRatingEngineSchema20130713 schema) {
		try {
			if (checkAdmin()) {
				return mresf.delete(schema);
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public Boolean deleteRatingsForMatchRatingEngineSchema(ScrumMatchRatingEngineSchema20130713 schema) {
		try {
			if (checkAdmin()) {
				prf.deleteForSchema(schema);
				return null;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public ScrumMatchRatingEngineSchema setMatchRatingEngineSchemaAsDefault(ScrumMatchRatingEngineSchema20130713 schema) {
		try {
			if (checkAdmin()) {
				IRatingEngineSchema s2 = mresf.setAsDefault(schema);
				if (s2 instanceof ScrumMatchRatingEngineSchema)
					return (ScrumMatchRatingEngineSchema) s2;
				else 
					return null; // not sure what this would be
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public List<ScrumMatchRatingEngineSchema> getScrumSchemaList() {
		try {
			if (checkAdmin()) {
				return mresf.getScrumList();
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public Boolean flushAllPipelineJobs() {
		try {
			if (checkAdmin()) {
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
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public Boolean deleteComp(Long id) {
		try {
			if (checkAdmin()) {
				return cf.delete(id);
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public TopTenSeedData createTopTenList(TopTenSeedData tti) {
		try {
			if (checkAdmin()) {
				ttlf.create(tti);
				return tti;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public Boolean setCompAsDefault(Long compId) {
		try {
			if (checkAdmin()) {
				ICoreConfiguration cc = ccf.get();
				cc.setDefaultCompId(compId);
				cc = ccf.put(cc);
				if (cc != null && cc.getDefaultCompId() != null) {
					return cc.getDefaultCompId().equals(compId);
				} else {
					return false;
				}
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public IContent createContent(Long id, String content) {
		try {
			if (checkAdmin()) {
				IContent c = ctf.create();
				if (id != null) {
					c.setId(id);
					ctf.put(c);
				}

				if (content != null) {
					c.setBody(content);
					ctf.put(c);
				}

				return c;

			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public List<IContent> getContentList(boolean onlyActive) {
		try {
			if (checkAdmin()) {
				List<IContent> lc = null;
				if (ctf instanceof IContentFactory) {
					lc = ((IContentFactory)ctf).getAll(onlyActive);
				}
				return lc;

			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public ICoreConfiguration getConfiguration() {
		try {
			ICoreConfiguration conf = ccf.get();
			// check that environment is set
			if (conf.getEnvironment() == null) {
				HttpServletRequest req = this.getThreadLocalRequest();
				if (req.getServerName().contains("127.0.0.1")) {
					conf.setEnvironment(Environment.LOCAL);
				} else if (req.getServerName().contains("dev")) {
					conf.setEnvironment(Environment.DEV);
				} else if (req.getServerName().contains("beta")) {
					conf.setEnvironment(Environment.BETA);
				} else if (req.getServerName().contains("www")) {
					conf.setEnvironment(Environment.PROD);
				} else {
					throw new RuntimeException("Could not determine environment for configuration");
				}
				conf = ccf.put(conf);
			}
			return conf;
		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}		
	}

	@Override
	public List<IStanding> getStandings(Long roundId) {
		try {
			if (checkAdmin()) {
				rf.setId(roundId);
				return sf.getForRound(rf.getRound());
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public List<IStanding> saveStandings(Long roundId, List<IStanding> standings) {
		try {
			if (checkAdmin()) {
				for (IStanding s: standings) {
					sf.put(s);
				}
				rf.setId(roundId);
				return sf.getForRound(rf.getRound());
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public List<IStanding> FetchRoundStandings(Long roundId) {
		try {
			if (checkAdmin()) {
				if (roundId != null) {
					rf.setId(roundId);
					IRound r = rf.getRound();
					if (r != null) {
						IStandingsFetcher fetcher = sff.getFetcher(r);
						if (fetcher != null) {
							if (r.getCompId() != null) {
								cf.setId(r.getCompId());
								ICompetition c = cf.getCompetition();
								if (c != null) {
									fetcher.setComp(c);
									fetcher.setRound(r);
									fetcher.setUc(uc);
									fetcher.setUrl(c.getForeignURL()+"?template=pointstable");
									List<IStanding> standings = new ArrayList<IStanding>();
									Iterator<ITeamGroup> it = c.getTeams().iterator();
									while (it.hasNext()) {
										standings.add(fetcher.getStandingForTeam(it.next()));
									}
									return standings;
								}
							}
						}
					}
				}
			}

			return null;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public IMatchGroup SaveScore(Long matchId, int hS, int vS, Status status) {
		try {
			if (checkAdmin()) {

				ISimpleScoreMatchResult mr = (ISimpleScoreMatchResult) mrf.create();
				mr.setHomeScore(hS);
				mr.setVisitScore(vS);
				((IMatchResult)mr).setStatus(status);
				((IMatchResult)mr).setMatchID(matchId);
				((IMatchResult)mr).setSource(UserServiceFactory.getUserService().getCurrentUser().getEmail());
				((IMatchResult)mr).setType(ResultType.SIMPLE_SCORE);
				((IMatchResult)mr).setRecordedDate(new Date());
				mrf.put((IMatchResult) mr);

				// link the result to the match
				IMatchGroup m = mf.get(matchId);
				m.setSimpleScoreMatchResult(mr);
				m.setSimpleScoreMatchResultId(((IMatchResult)mr).getId());
				mf.put(m);

				return m;
			}

			return null;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}		
	}

	@Override
	public IRatingQuery getRatingQuery(long rqId) {
		try {
			if (checkAdmin()) {
				return rqf.get(rqId);
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}	
	}

	@Override
	public List<IPlayerRating> getRatingQueryResults(long rqId) {
		try {
			if (checkAdmin()) {
				IRatingQuery rq = rqf.get(rqId);
				if (rq.getStatus() == IRatingQuery.Status.COMPLETE) {
					return prf.query(rq);
				}
				return null;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}	
	}

	
	@Override
	public Boolean deleteRatingQuery(IRatingQuery query) {
		try {
			if (checkAdmin()) {
				return rqf.delete(query);
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public String checkPipelineStatus(String id, Long matchId) {
		try {
			if (checkAdmin()) {
				PipelineService service = PipelineServiceFactory.newPipelineService();

				JobInfo jobInfo = service.getJobInfo(id);
				switch (jobInfo.getJobState()) {
				case COMPLETED_SUCCESSFULLY:
					service.deletePipelineRecords(id);
					IMatchGroup m = mf.get(matchId);
					m.setFetchMatchStatsPipelineId(null);
					mf.put(m);
					return "COMPLETED"; // (List<IPlayerMatchStats>) jobInfo.getOutput();
				case RUNNING:
					break;
				case STOPPED_BY_ERROR:
					throw new RuntimeException("Job stopped " + jobInfo.getError());
				case STOPPED_BY_REQUEST:
					throw new RuntimeException("Job stopped by request.");
				default:
					return "RUNNING";
				}
				return "WOT?";
			}
			return null;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return "COMPLETED";
		}
	}



}
