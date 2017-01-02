package net.rugby.foundation.admin.server;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
import net.rugby.foundation.admin.server.factory.IBlurbFactory;
import net.rugby.foundation.admin.server.factory.IDigestEmailFactory;
import net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory;
import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.IPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.server.factory.IStandingsFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.ILineupFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.admin.server.model.IForeignCompetitionFetcher;
import net.rugby.foundation.admin.server.model.ILineupFetcher;
import net.rugby.foundation.admin.server.model.IPlayerMatchStatsFetcher;
import net.rugby.foundation.admin.server.model.IResultFetcher;
import net.rugby.foundation.admin.server.model.IStandingsFetcher;
import net.rugby.foundation.admin.server.orchestration.AdminOrchestrationTargets;
import net.rugby.foundation.admin.server.orchestration.IOrchestrationConfigurationFactory;
import net.rugby.foundation.admin.server.orchestration.OrchestrationHelper;
import net.rugby.foundation.admin.server.util.CountryLoader;
import net.rugby.foundation.admin.server.util.DigestEmailer;
import net.rugby.foundation.admin.server.workflow.IWorkflowConfigurationFactory;
import net.rugby.foundation.admin.server.workflow.fetchstats.ESPN6FetchTeamMatchStats;
import net.rugby.foundation.admin.server.workflow.fetchstats.FetchMatchStats.Home_or_Visitor;
import net.rugby.foundation.admin.server.workflow.fetchstats.FetchTeamMatchStats;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.MatchActions;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.RatingActions;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.SeriesActions;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.UserActions;
import net.rugby.foundation.admin.shared.EditPlayersTwitterAdminTask;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.admin.shared.IBlurb;
import net.rugby.foundation.admin.shared.IDigestEmail;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.admin.shared.IWorkflowConfiguration;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.core.server.IAccountManager;
import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IContentFactory;
import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.core.server.factory.ILineupSlotFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.core.server.factory.IPlaceFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingMatrixFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IRawScoreFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.core.server.promote.IPromoter;
import net.rugby.foundation.game1.server.factory.IEntryFactory;
import net.rugby.foundation.game1.server.factory.IMatchEntryFactory;
import net.rugby.foundation.game1.server.factory.IRoundEntryFactory;
import net.rugby.foundation.model.shared.CoreConfiguration.Environment;
import net.rugby.foundation.model.shared.Country;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.ILineupSlot;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IMatchResult.ResultType;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingQuery.MinMinutes;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.model.shared.ISimpleScoreMatchResult;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.ITopTenUser;
import net.rugby.foundation.model.shared.PlayerRating;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema20130713;
import net.rugby.foundation.model.shared.UniversalRound;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
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
	private IContentFactory ctf;
	private IStandingFactory sf;
	private IStandingsFetcherFactory sff;
	private IUrlCacher uc;
	private IRatingQueryFactory rqf;
	private IPlayerRatingFactory prf;
	private ISeriesConfigurationFactory scf;
	private IUniversalRoundFactory urf;
	private IRatingSeriesFactory rsf;
	private IRatingGroupFactory rgf;
	private IRatingMatrixFactory rmf;
	private IRawScoreFactory rwsf;
	private IBlurbFactory bf;
	private IPlaceFactory spf;
	private IDigestEmailFactory def;
	private DigestEmailer digestEmailer;
	private IAccountManager am;
	private IPromoter promoter;
	private ILineupFetcherFactory luff;
	private ILineupSlotFactory lsf;

	private static final long serialVersionUID = 1L;
	public RugbyAdminServiceImpl() {
		try {
			queuer = new OrchestrationHelper();

			Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.INFO);
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
			IAdminTaskFactory atf, IMatchResultFactory mrf, IRawScoreFactory rwsf,
			IPlayerMatchStatsFetcherFactory pmsff, IMatchRatingEngineSchemaFactory mresf, ITopTenListFactory ttlf, 
			IContentFactory ctf, IStandingFactory sf, IStandingsFetcherFactory sff,
			IUrlCacher uc, IRatingQueryFactory rqf, IPlayerRatingFactory prf, ISeriesConfigurationFactory scf,
			IUniversalRoundFactory urf, IRatingSeriesFactory rsf, IRatingGroupFactory rgf, IRatingMatrixFactory rmf,
			IBlurbFactory bf, IPlaceFactory spf, IDigestEmailFactory def, IAccountManager am, IPromoter promoter,
			ILineupFetcherFactory luff, ILineupSlotFactory lsf) {
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
			this.rwsf = rwsf;
			this.pmsff = pmsff;
			this.mresf = mresf;
			this.ttlf = ttlf;
			this.ctf = ctf;
			this.sf = sf;
			this.sff = sff;
			this.uc = uc;
			this.rqf = rqf;
			this.prf = prf;
			this.scf = scf;
			this.urf = urf;
			this.rsf = rsf;
			this.rgf = rgf;
			this.rmf = rmf;
			this.bf = bf;
			this.spf = spf;
			this.def = def;
			this.am = am;
			this.promoter = promoter;
			this.luff = luff;
			this.lsf = lsf;

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
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "fetchCompetition. url: " + url);
				IForeignCompetitionFetcher fetcher = fcff.getForeignCompetitionFetcher(url, compType);

				ICompetition comp = fetcher.getCompetition(url, rounds, teams);

				// do we have this in our database?
				//				List<ICompetition> comps = cf.getAllComps();
				//				ICompetition compDB = null;
				//				for (ICompetition dBComp : comps) {
				//					if (dBComp.getLongName() != null && dBComp.getLongName().equals(comp.getLongName())) {
				//						compDB = dBComp;			
				//					}
				//				}
				//
				//				if (compDB != null) {
				//					return compDB;
				//				} else {
				//					// add the rounds for them
				//					ArrayList<Long> rids = new ArrayList<Long>();
				//					for (IRound r : rounds) {
				//						rids.add(r.getId());
				//					}
				//					comp.setRoundIds(rids);
				//					comp.setRounds(rounds);
				//					return comp;
				//				}

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
	public ICompetition saveCompetition(ICompetition comp, Map<String,ITeamGroup> teams) {
		try {
			if (checkAdmin()) {
				if (comp != null) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "saveCompetition. comp: " + comp.getLongName());
				} else {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "saveCompetition. comp: null");
				}
				comp = cf.put(comp);

				//				// there should just be one...
				//				ICoreConfiguration c = ccf.get();
				//
				//				c.addCompetition(comp.getId(), comp.getShortName());
				//				if (comp.getUnderway()) {
				//					c.addCompUnderway(comp.getId());
				//				} else {
				//					c.removeCompUnderway(comp.getId());
				//				}
				//				//c.setDefaultCompId(comp.getId());
				//				ccf.put(c);

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
	public Map<String, ITeamGroup> fetchTeams(String urlDate, String weeks, CompetitionType compType) {
		try {
			if (checkAdmin()) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "fetchTeams. urlDate: " + urlDate);
				CountryLoader cloader = new CountryLoader();
				cloader.Run(countryf);

				IForeignCompetitionFetcher fetcher = fcff.getForeignCompetitionFetcher(urlDate, compType);

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
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "fetchRounds. url: " + url);
				IForeignCompetitionFetcher fetcher = fcff.getForeignCompetitionFetcher(url, compType);

				List<IRound>  rounds = fetcher.getRounds(url, matches);

				// do we have this in our database? If so, replace the fetched one with the one that already has an ID
				// a round in the DB is considered the same if it contains the same matches

				//				Map<Integer,IRound> changes = new HashMap<Integer,IRound>();
				//
				//				// have to do two-pass to avoid concurrentChangeException
				//				for (IRound r : rounds) {
				//					IRound roundDB = rf.find(r);
				//
				//					if (roundDB != null) {
				//						changes.put(rounds.indexOf(r), roundDB);
				//					}
				//				}
				//
				//				// now swap in the found round for the in situ one
				//				for (Integer index : changes.keySet()) {
				//					rounds.remove((int)index);
				//					rounds.add(index,changes.get(index));	
				//				}


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
	public Map<String, IMatchGroup> fetchMatches(String url, int weeksOffset, Map<String,ITeamGroup> teams, Map<String,IMatchGroup> matches, CompetitionType compType) {
		try {
			if (checkAdmin()) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "fetchMatches. url: " + url);

				IForeignCompetitionFetcher fetcher = fcff.getForeignCompetitionFetcher(url, compType);
				Map<String, IMatchGroup> updatedMatches = fetcher.getMatches("", weeksOffset, teams, matches);
				//updatedMatches = deduplicateMatches(updatedMatches);  //too dangerous for deleting crufty comps
				if (updatedMatches != null) {
					matches.putAll(updatedMatches);
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

	private Map<String, IMatchGroup> deduplicateMatches(Map<String, IMatchGroup> matches) {
		Map<String, IMatchGroup> dupes = new HashMap<String, IMatchGroup>();

		if (matches != null) {
			for (IMatchGroup m: matches.values()) {
				IMatchGroup found = mf.find(m);
				if (found != null) {
					dupes.put(found.getDisplayName(), found);
				}
			}
		}

		// swap out the copies with the real ones.
		for (String name: dupes.keySet()) {
			matches.put(name, dupes.get(name));
		}

		return matches;
	}
	@Override
	public Map<String, ITeamGroup> saveTeams(Map<String, ITeamGroup> teams) {
		try {
			if (checkAdmin()) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "SaveTeams. ");
				int count = 0;
				// make sure our teams have all been saved.
				for (String key : teams.keySet()) {
					if (teams.get(key).getId() == null) {
						ITeamGroup t = tf.put(teams.get(key));
						assert (t.getId() != null);
						//update map version
						teams.put(t.getDisplayName(), t);
						++count;

					}
				}
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Added " + count + " new teams.");
			}
			return teams;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public Map<String, IMatchGroup> fetchMatches(String url, Map<String, ITeamGroup> teams, CompetitionType compType) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<ICompetition> getComps(Filter filter) {
		try {
			if (checkAdmin()) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "getComps. filter: " + filter);
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
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "getOrchestrationConfiguration.");
				List<IOrchestrationConfiguration> list = ocf.getAll();


				for (IOrchestrationConfiguration oc : list) {
					newMap.put(cf.get(oc.getCompID()).getLongName(), oc);
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
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "saveOrchestrationConfiguration.");
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
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "getMatches. roundId: " + roundId);
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
	public ITeamGroup saveTeam(ITeamGroup teamGroup, boolean saveMatches) {
		try {
			if (checkAdmin()) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "saveTeam. teamGroup: " + teamGroup != null ? teamGroup.getDisplayName():"null");
				tf.put(teamGroup);

				if (saveMatches) {
					// The displayName of the team has been changed. Loop through all the future matches we have scheduled for this team and update its displayName.
					List<IMatchGroup> matches = mf.getFutureMatchesForTeam(teamGroup.getId());

					for (IMatchGroup m : matches) {
						m.setDisplayName();
						mf.put(m);
					}
				}
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
				//				List<Long> compIds = wfcf.get().getUnderwayCompetitions();
				//				for (Long compId: compIds) {
				//					ICompetition comp = cf.get(compId);
				//					IRound r = comp.getPrevRound();
				//					if (r != null) {
				//						ef.setRoundAndComp(null, comp);
				//						List<IEntry> entries = ef.getEntries();
				//						for (IEntry e: entries) {
				//							IRoundEntry re = e.getRoundEntries().get(r.getId());
				//							if (re == null) {  // @REX shouldn't the factory or entity do this?!
				//								re = ref.getNew();
				//
				//								// last match of the round is the tiebreaker
				//								re.setTieBreakerMatchId(r.getMatches().get(r.getMatches().size()-1).getId()); 
				//
				//								re.setRoundId(r.getId());
				//
				//								e.getRoundEntries().put(r.getId(),re);	
				//							}
				//							if (re != null) {
				//								Map<Long,IMatchEntry> pickMap = re.getMatchPickMap();
				//
				//								if (pickMap == null || pickMap.isEmpty()) {
				//									// need to fix
				//									e.getRoundEntries().get(r.getId()).setMatchPickMap(new HashMap<Long,IMatchEntry>());
				//
				//									for (IMatchGroup m: r.getMatches()) {
				//										if (!m.getLocked()) {
				//											IMatchEntry me = new MatchEntry();
				//											me.setMatchId(m.getId());
				//
				//											if (m.getStatus() == Status.FINAL_HOME_WIN) {
				//												me.setTeamPicked(m.getHomeTeam());
				//												me.setTeamPickedId(m.getHomeTeamId());
				//											} else {
				//												me.setTeamPicked(m.getVisitingTeam());
				//												me.setTeamPickedId(m.getVisitingTeamId());
				//											}
				//
				//											me = mef.put(me);
				//											e.getRoundEntries().get(r.getId()).getMatchPickMap().put(m.getId(), me);
				//
				//										} else {
				//											changes.add("Couldn't add round 1 pick list to entry " + e.getName() + " -- match " + m.getDisplayName() + " (" + m.getId()+ ") is locked.");
				//										}
				//									}
				//
				//									ef.put(e);
				//									changes.add("Added round 1 pick list to entry " + e.getName());
				//								}
				//							}
				//						}
				//					}
				//				}

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

				//				ICoreConfiguration cc = ccf.get();
				//				// add or remove from CoreConfiguration map depending on the Underway flag
				//				if (comp.getUnderway()) {
				//					cc.addCompUnderway(comp.getId());
				//				} else {
				//					cc.removeCompUnderway(comp.getId());
				//				}
				//
				//				if (comp.getShowToClient()) {
				//					cc.addCompForClient(comp.getId());
				//				} else {
				//					cc.removeCompForClient(comp.getId());
				//				}
				//
				//				ccf.put(cc);

				//refresh the core config
				ICoreConfiguration config = ccf.get();
				ccf.dropFromCache(config.getId());
				config = ccf.get();

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
				if (matchGroup != null) {
					// see if they are changing the workflowStatus
					IMatchGroup old = mf.get(matchGroup.getId());
					if (!matchGroup.getWorkflowStatus().equals(old.getWorkflowStatus())) {
						if (WorkflowStatus.NO_STATS == matchGroup.getWorkflowStatus() || WorkflowStatus.FETCHED == matchGroup.getWorkflowStatus()) {
							// OPTA didn't create stats
							// do we have a pipeline?							
							String id = matchGroup.getFetchMatchStatsPipelineId();
							if (id != null && !id.isEmpty()) {
								matchGroup.setFetchMatchStatsPipelineId(null);
								mf.put(matchGroup);
								try {
									PipelineService service = PipelineServiceFactory.newPipelineService();
									service.deletePipelineRecords(id,true,false);
								} catch (NoSuchObjectException nsox) {
									// it's ok, just was a dangling reference in the match record
								}

								List<? extends IAdminTask> tasks = atf.getForPipeline(id);
								atf.delete((List<IAdminTask>) tasks);
							}
						}
					}
					mf.put(matchGroup);
					return matchGroup;
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
				return cf.get(compId);
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
				if (id == null) {
					return pf.create();
				} else {
					return pf.get(id);
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
	public IPlayer savePlayer(IPlayer player, IAdminTask task) {
		try {
			if (checkAdmin()) {
				player = pf.put(player);
				if (task != null && task.getAction().equals(IAdminTask.Action.EDITPLAYER)) {
					if (task.getPromise() != null) {
						PipelineService service = PipelineServiceFactory.newPipelineService();
						try {
							service.submitPromisedValue(task.getPromise(), player.getId());
						} catch (NoSuchObjectException ex) {
							Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
						} catch (OrphanedObjectException ex) {
							Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
						}
					}

					atf.complete(task);
					player.getBlockingTaskIds().remove(task.getId());
					player.getTaskIds().remove(task.getId());
					pf.put(player);
				} else if (task != null && task.getAction().equals(IAdminTask.Action.EDITPLAYERTWITTER)) {
					if (task.getPromise() != null) {
						PipelineService service = PipelineServiceFactory.newPipelineService();
						try {
							service.submitPromisedValue(task.getPromise(), player.getId());
						} catch (NoSuchObjectException ex) {
							Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
						} catch (OrphanedObjectException ex) {
							Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
						}
					}

					atf.complete(task);
					player.getBlockingTaskIds().remove(task.getId());
					player.getTaskIds().remove(task.getId());
					pf.put(player);
					if (player.getTwitterHandle() != null && !player.getTwitterHandle().isEmpty()){
						ITopTenItem tti = null;
						ITopTenList ttl = ttlf.get(((EditPlayersTwitterAdminTask)task).getTopTenListId());
						for (ITopTenItem _tti : ttl.getList()){
							if (_tti.getPlayerId().equals(player.getId())){
								tti = _tti;
								break;
							}
						}
						promoter.promoteItem(tti, ttl);
					}
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

				return null; //(List<IPlayerMatchStats>) pmsf.getByMatchId(matchId);
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
				//				Country c = new Country(5000L, "None", "NONE", "---", "Unassigned");
				//				countryf.put(c);
				//
				//				PipelineService service = PipelineServiceFactory.newPipelineService();
				//
				//				IMatchGroup match = mf.get(matchId);
				//
				//				String pipelineId = "";
				//
				//				// first check if this match already has a pipeline going and kill it if it does
				//				if (match.getFetchMatchStatsPipelineId() != null && !match.getFetchMatchStatsPipelineId().isEmpty()) {
				//					// delete adminTasks first
				//					List<? extends IAdminTask> tasks = atf.getForPipeline(match.getFetchMatchStatsPipelineId());
				//					atf.delete((List<IAdminTask>) tasks);
				//
				//					// now the pipeline records
				//					service.deletePipelineRecords(match.getFetchMatchStatsPipelineId(), true, false);
				//					match.setFetchMatchStatsPipelineId(null);
				//
				//				}
				//
				//				//pipelineId = service.startNewPipeline(new GenerateMatchRatings(pf, tmsf, pmsf, countryf, mref, pmrf), match, new JobSetting.MaxAttempts(1));
				//				try {
				//					pipelineId = service.startNewPipeline(new GenerateMatchRatings(), match, new JobSetting.MaxAttempts(3));
				//				} catch (RetriesExhaustedException ree) {
				//					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Bad stuff", ree);
				//				}
				//				match.setFetchMatchStatsPipelineId(pipelineId);
				//				mf.put(match);
				//
				//				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "pipelineId: " + pipelineId);
				//
				//				//				while (true) {
				//				//					Thread.sleep(2000);
				//
				//				//				}
				//
				//				return pipelineId;

				String url2 = ccf.get().getEngineUrl() +
						"/orchestration/IMatchGroup";

				QueueFactory.getDefaultQueue().add(withUrl(url2.toString()).etaMillis(60000).
						param(AdminOrchestrationActions.MatchActions.getKey(), MatchActions.FETCHSTATS.toString()).
						param(AdminOrchestrationTargets.Targets.getKey(), AdminOrchestrationTargets.Targets.MATCH.toString()).
						param("id",matchId.toString()).
						param("extraKey", "0"));

				// give the orchestration 15 seconds to start the pipeline and look for the pipeline id
				Thread.sleep(15000);
				IMatchGroup m = mf.get(matchId);

				return m.getFetchMatchStatsPipelineId();

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
				return pmsf.get(id);
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
							service.submitPromisedValue(task.getPromise(), stats.getId());
						} catch (NoSuchObjectException e) {
							e.printStackTrace();
						} catch (OrphanedObjectException e) {
							e.printStackTrace();
						}
					}
					atf.complete(task);
					stats.getTaskIds().remove(task.getId());
					stats.getBlockingTaskIds().remove(task.getId());
					pmsf.put(stats);
				}
				IPlayerRating pr = prf.create();
				pr.setPlayer(pf.get(stats.getPlayerId()));
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
			List<Long> roundIds, List<position> posis, List<Long> countryIds, List<Long> teamIds, Long schemaId,
			Boolean scaleTime, Boolean scaleComp, Boolean scaleStanding, Boolean scaleMinutesPlayed, Boolean instrument) {
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
				rq.setScaleMinutesPlayed(scaleMinutesPlayed);
				if (scaleMinutesPlayed) {
					if (rq.getCompIds().size() > 1) {
						rq.setMinMinutesType(MinMinutes.TOTAL);
						rq.setMinMinutes(350);
					} else {
						rq.setMinMinutesType(MinMinutes.ROUND);
						rq.setMinMinutes(30);					
					}
				}
				rq.setInstrument(instrument);
				rq.setSchemaId(schemaId);

				// check that we have all the comps for the rounds specified, in multi-comp queries this info is missing
				for (Long rid : rq.getRoundIds()) {
					IRound r = rf.get(rid);
					if (r != null) {
						if (!rq.getCompIds().contains(r.getCompId())) {
							rq.getCompIds().add(r.getCompId());
						}
					}
				}
				rq = rqf.put(rq);

				// now trigger the backend processing task
				//Queue queue = QueueFactory.getDefaultQueue();
				//String url = "/admin/orchestration/IRatingQuery";

				String url2 = ccf.get().getEngineUrl() +
						"/orchestration/IRatingQuery";

				//Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Starting Rating Query " + rq.getId());
				QueueFactory.getDefaultQueue().add(withUrl(url2.toString()).etaMillis(300000).
						param(AdminOrchestrationActions.RatingActions.getKey(), RatingActions.GENERATE.toString()).
						param(AdminOrchestrationTargets.Targets.getKey(), AdminOrchestrationTargets.Targets.RATING.toString()).
						param("id",rq.getId().toString()).
						param("extraKey", "0L"));

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
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "refetchTeamMatchStats. team: " + team.getDisplayName() + " in match " + match.getDisplayName());

				Home_or_Visitor hov = Home_or_Visitor.VISITOR;
				if (match.getHomeTeamId().equals(team.getId())) {
					hov = Home_or_Visitor.HOME;
				}

				// delete the old one
				tmsf.delete(tms);
				
				String pipelineId = "";

				//pipelineId = service.startNewPipeline(new GenerateMatchRatings(pf, tmsf, pmsf, countryf, mref, pmrf), match, new JobSetting.MaxAttempts(1));
				pipelineId = service.startNewPipeline(new ESPN6FetchTeamMatchStats(), match, hov, null, new JobSetting.MaxAttempts(3));
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "pipelineId: " + pipelineId);

				while (true) {
					Thread.sleep(2000);
					JobInfo jobInfo = service.getJobInfo(pipelineId);
					switch (jobInfo.getJobState()) {
					case COMPLETED_SUCCESSFULLY:
						//service.deletePipelineRecords(pipelineId);
						return getTeamMatchStats(match.getId(),team.getId()); // (List<IPlayerMatchStats>) jobInfo.getOutput();
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
							service.submitPromisedValue(task.getPromise(), tms.getId());
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
				IRatingEngineSchema schema = mresf.get(schemaId);
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
	public ScrumMatchRatingEngineSchema createMatchRatingEngineSchema() {
		try {
			if (checkAdmin()) {
				IRatingEngineSchema schema = mresf.create();
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
	public boolean deleteRawScoresForMatchRatingEngineSchema(ScrumMatchRatingEngineSchema20130713 schema) {
		try {
			if (checkAdmin()) {
				rwsf.delete(schema);
				return true;
			} else {
				return false;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return false;
		}	}

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
				ICompetition c = cf.get(id);
				if (c != null) {
					return cf.delete(c);
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
	public TopTenSeedData createTopTenList(TopTenSeedData tti, Map<IPlayer, String> twitterMap) {
		try {
			if (checkAdmin()) {
				ttlf.create(tti);

				// save twitter handles for players
				for (IPlayer p: twitterMap.keySet()) {
					p.setTwitterHandle(twitterMap.get(p));
					pf.put(p);
				}

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
	public Boolean setCompAsGlobal(Long compId) {
		try {
			if (checkAdmin()) {
				ICoreConfiguration cc = ccf.get();
				cc.setGlobalCompId(compId);
				cc = ccf.put(cc);
				if (cc != null && cc.getGlobalCompId() != null) {
					return cc.getGlobalCompId().equals(compId);
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

			// check that environment is set properly
			Environment env = conf.getEnvironment();

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

			// if we had to adjust it, save
			// note that this will happen when we "copy down" prod data to development
			// we also check this value when determining whether to send out emails in our development environment so it's important!
			if (conf.getEnvironment() != null && !conf.getEnvironment().equals(env)) {
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
				return sf.getForRound(rf.get(roundId));
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
					if (s.getRoundId() == null && s.getRound() != null) {
						s.setRoundId(s.getRound().getId());
					}
					sf.put(s);
				}
				return sf.getForRound(rf.get(roundId));
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
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "fetchRoundStandings. roundId: " + roundId);
				if (roundId != null) {
					IRound r = rf.get(roundId);
					if (r != null) {
						IStandingsFetcher fetcher = sff.getFetcher(r);
						if (fetcher != null) {
							Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "fetchRoundStandings. got fetcher.");
							if (r.getCompId() != null) {
								ICompetition c = cf.get(r.getCompId());
								if (c != null) {
									Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "fetchRoundStandings. got comp.");
									fetcher.setComp(c);
									fetcher.setRound(r);
									fetcher.setUc(uc);

									// with the new espn.co.uk tables, we are seeing the new comps (as of SR 2016) 
									// not working with the old template syntax. Allow over-ride from admin.html
									if (c.getTableURL() != null && !c.getTableURL().isEmpty()) {
										fetcher.setUrl(c.getTableURL());
									} else {
										fetcher.setUrl(c.getForeignURL()+"?template=pointstable");
									}

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
				if (id == null) return "WOT?";
				PipelineService service = PipelineServiceFactory.newPipelineService();

				JobInfo jobInfo = service.getJobInfo(id);
				switch (jobInfo.getJobState()) {
				case COMPLETED_SUCCESSFULLY:
					//					service.deletePipelineRecords(id);
					//					IMatchGroup m = mf.get(matchId);
					//					m.setFetchMatchStatsPipelineId(null);
					//					mf.put(m);
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

	@Override
	public IMatchGroup AddMatchToRound(IRound round, Long homeTeamId, Long visitTeamId, Long espnMatchId, Long espnLeagueId) {
		try {
			if (checkAdmin()) {
				IRound r = rf.get(round.getId());
				IMatchGroup mg = mf.create();
				mg.setDate(DateTime.now().toDate());
				//mg.setDisplayName("Change me");
				mg.setHomeTeamId(homeTeamId);
				mg.setVisitingTeamId(visitTeamId);
				mg.setForeignId(espnMatchId);
				mg.setForeignLeagueId(espnLeagueId);
				//http://www.espn.co.uk/rugby/match?gameId=289721&league=289234
				mg.setForeignUrl("http://www.espn.co.uk/rugby/match?gameId=" + espnMatchId + "&league=" + espnLeagueId);
				if (homeTeamId != null && visitTeamId != null) {
					mg.setHomeTeam(tf.get(homeTeamId));
					mg.setVisitingTeam(tf.get(visitTeamId));
					mg.setDisplayName();
				} else { // no teams provided. Shouldn't allow
					return null;
					//					ITeamGroup t = tf.getTeamByName("TBD");
					//					if (t == null)
					//					{
					//						t = tf.getTeamByName("TBC");
					//						if (t == null)
					//						{
					//							Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Could not find team TBD to create new empty match. Giving up.");
					//							return null;
					//						}
					//					}
					//					mg.setHomeTeam(t);
					//					mg.setVisitingTeam(t);
				}
				mf.put(mg);
				r.addMatch(mg);
				rf.put(r);
				return mg;
			}
			return null;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public String cleanUp() {
		try {
			if (checkAdmin()) {
				Queue queue = QueueFactory.getDefaultQueue();

				queue.add(withUrl(ccf.get().getEngineUrl() + "/cleanUp").param("action", net.rugby.foundation.engine.server.CleanupServlet.Actions.SHRINK.name()).param("clazz", PlayerRating.class.getCanonicalName()));
				//				String url2 = ccf.get().getEngineUrl() +
				//						"/orchestration/IRatingQuery";
				//
				//				QueueFactory.getDefaultQueue().add(withUrl(url2.toString()).etaMillis(60000).
				//						param(AdminOrchestrationActions.RatingActions.getKey(), RatingActions.CLEANUP.toString()).
				//						param(AdminOrchestrationTargets.Targets.getKey(), AdminOrchestrationTargets.Targets.RATING.toString()).
				//						param("id","0").
				//						param("extraKey", "0"));

				//				rqf.deleteAll();
				//				prf.deleteAll();
				//				flushAllPipelineJobs();

			} 
			return "Cleanup underway!";
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;

		}
	}

	@Override
	public List<ISeriesConfiguration> getAllSeriesConfigurations(Boolean active) {
		try {
			List<ISeriesConfiguration> retval = scf.getAll(active);
			for (ISeriesConfiguration sc : retval) {
				// clear out the pipeline as soon as it is completed
				try {


					if (sc.getPipelineId() != null) {
						PipelineService service = PipelineServiceFactory.newPipelineService();
						JobInfo jobInfo = service.getJobInfo(sc.getPipelineId());
						switch (jobInfo.getJobState()) {
						case COMPLETED_SUCCESSFULLY:
							service.deletePipelineRecords(sc.getPipelineId());
							sc.setPipelineId(null);
							scf.put(sc);
							break;
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
						};
					}
				} catch (Throwable ex) {

					// bad pipeline handle. delete
					sc.setPipelineId(null);
					scf.put(sc);

				}
			}
			return retval;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;	
		}
	}

	@Override
	public ISeriesConfiguration getSeriesConfiguration(Long id) {
		try {
			if (id == null)
				return scf.create();
			else {
				ISeriesConfiguration sc = scf.get(id);

				// clear out the pipeline as soon as it is completed
				//				if (sc.getPipelineId() != null) {
				//					PipelineService service = PipelineServiceFactory.newPipelineService();
				//					JobInfo jobInfo = service.getJobInfo(sc.getPipelineId());
				//					switch (jobInfo.getJobState()) {
				//					case COMPLETED_SUCCESSFULLY:
				//						service.deletePipelineRecords(sc.getPipelineId());
				//						sc.setPipelineId(null);
				//						scf.put(sc);
				//						break;
				//					case RUNNING:
				//						break;
				//					case STOPPED_BY_ERROR:
				//						throw new RuntimeException("Job stopped " + jobInfo.getError());
				//					case STOPPED_BY_REQUEST:
				//						throw new RuntimeException("Job stopped by request.");
				//					case WAITING_TO_RETRY:
				//						break;
				//					default:
				//						break;
				//					};
				//					
				//				}
				return sc;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;	
		}	
	}

	@Override
	public String processSeriesConfiguration(Long sConfigId) {
		try {
			if (checkAdmin()) {

				String url2 = ccf.get().getEngineUrl() +
						"/orchestration/ISeriesConfiguration";

				QueueFactory.getDefaultQueue().add(withUrl(url2.toString()).etaMillis(60000).
						param(AdminOrchestrationActions.SeriesActions.getKey(), SeriesActions.PROCESS.toString()).
						param(AdminOrchestrationTargets.Targets.getKey(), AdminOrchestrationTargets.Targets.SERIES.toString()).
						param("id",sConfigId.toString()).
						param("extraKey", "0"));

				// give the orchestration 15 seconds to start the pipeline and look for the pipeline id
				Thread.sleep(15000);
				ISeriesConfiguration sc = scf.get(sConfigId);
				assert(sc.getPipelineId() != null);
				return sc.getPipelineId();

			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public Boolean deleteSeriesConfiguration(Long sConfigId) {
		try {
			if (checkAdmin()) {
				ISeriesConfiguration sc = scf.get(sConfigId);
				//IRatingSeries series = null;
				if (sc != null) {
					//					if (sc.getSeriesId() != null) {
					//						series = rsf.get(sc.getSeriesId());
					//						if (series != null) {
					//							// first delete TTLs associated
					//							for (Long rgid : series.getRatingGroupIds()) {
					//								IRatingGroup rg = rgf.get(rgid);
					//								rgf.deleteTTLs(rg);
					//							}
					//						}
					//					}

					if (sc.getPipelineId() != null) {
						PipelineService service = PipelineServiceFactory.newPipelineService();
						service.deletePipelineRecords(sc.getPipelineId(), true, true);
						sc.setPipelineId(null);
					}

					return scf.delete(sc);
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return false;	
		}	
	}

	@Override
	public ISeriesConfiguration saveSeriesConfiguration(ISeriesConfiguration sConfig) throws Exception {
		try {
			if (checkAdmin()) {
				// valid configs are:
				//	1) Mode == BY_MATCH and Criteria == ROUND
				//	2) Mode == BY_POSITION and (Criteria == IN_FORM || Criteria == IMPACT)
				//	3) Mode == BY_COMP and Criteria == ROUND
				//	4) Mode == BY_TEAM and (Criteria == IN_FORM || Criteria == IMPACT)
				if (sConfig.getActiveCriteria().contains(Criteria.BEST_ALLTIME)) {
					throwUnsupportedSeriesConfigException();
				}

				if (sConfig.getMode().equals(RatingMode.BY_COUNTRY)) {
					throwUnsupportedSeriesConfigException();
				}

				if (sConfig.getActiveCriteria().contains(Criteria.ROUND)) {
					if (!sConfig.getMode().equals(RatingMode.BY_MATCH) && !sConfig.getMode().equals(RatingMode.BY_COMP)) {
						throwUnsupportedSeriesConfigException();
					}
				}

				if (sConfig.getActiveCriteria().contains(Criteria.IN_FORM)) {
					if (!(sConfig.getMode().equals(RatingMode.BY_POSITION) || sConfig.getMode().equals(RatingMode.BY_TEAM))) {
						throwUnsupportedSeriesConfigException();
					}
				}

				if (sConfig.getActiveCriteria().contains(Criteria.BEST_YEAR)) {
					if (!sConfig.getMode().equals(RatingMode.BY_POSITION)) {
						throwUnsupportedSeriesConfigException();
					}
				}

				if (sConfig.getActiveCriteria().contains(Criteria.AVERAGE_IMPACT))  {
					if (!(sConfig.getMode().equals(RatingMode.BY_POSITION) || sConfig.getMode().equals(RatingMode.BY_TEAM))) {
						throwUnsupportedSeriesConfigException();
					}
				}



				return scf.put(sConfig);
			} else {
				return null;
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			throw ex;	 // rethrow to client?
		}	
	}

	private void throwUnsupportedSeriesConfigException() throws Exception {
		throw new Exception("Invalid Mode/Criteria combo. Valid values are: \n" +
				"1) Mode == BY_MATCH and Criteria == ROUND \n" +
				"2) Mode == BY_POSITION and (Criteria == IN_FORM and/or Criteria == IMPACT)\n" + 
				"3) Mode == BY_TEAM and (Criteria == IN_FORM and/or Criteria == IMPACT)\n" + 
				"4) Mode == BY_COMP and Criteria == ROUND");

	}

	@Override
	public List<UniversalRound> getUniversalRounds(int size) {
		try {
			if (size == 20)
				return urf.lastTwentyUniversalRounds();
			else 
				return urf.lastYearUniversalRounds();
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;	
		}	}

	@Override
	public ICompetition addVirtualComp() {
		try {
			if (checkAdmin()) {
				ICompetition comp = cf.create();
				comp.setLongName("CHANGE ME");
				comp.setShortName("CHANGE");
				comp.setAbbr("CHNG");
				comp.setUnderway(false);
				comp.setCompType(CompetitionType.GLOBAL);
				cf.put(comp);
				return comp;
			} else {
				return null;
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			throw ex;	 // rethrow to client?
		}	
	}

	@Override
	public ISeriesConfiguration rollBackSeriesConfiguration(Long id) {
		try {
			if (checkAdmin()) {
				ISeriesConfiguration sc = scf.get(id);
				IRatingSeries series = null;
				if (sc != null) {
					if (sc.getSeriesId() != null) {
						series = rsf.get(sc.getSeriesId());
						if (series != null) {
							// we either need to remove a pending RatingGroup (status != OK) or a complete one (status == OK)
							// 	- in the second instance, need to bump the target and last round back one.
							if (sc.getStatus().equals(ISeriesConfiguration.Status.OK)) {
								sc.setTargetRoundOrdinal(sc.getLastRoundOrdinal());
								if (series.getRatingGroupIds().size() == 1) {
									sc.setLastRoundOrdinal(0);
								} else {
									sc.setLastRoundOrdinal(sc.getLastRoundOrdinal()-1);
								}
							}
							// So we need to delete the latest RatingGroup and set the status to OK
							Long rgid = series.getRatingGroupIds().get(0);
							IRatingGroup rg = rgf.get(rgid);
							rgf.deleteTTLs(rg);
							rgf.delete(rg);
							rgf.dropFromCache(rgid);
							series.getRatingGroupIds().remove(rgid);
							series.getRatingGroups().remove(rg);
							sc.setStatus(ISeriesConfiguration.Status.OK);
							rsf.put(series);
							rsf.dropFromCache(series.getId());
							scf.put(sc);
							scf.dropFromCache(id);
						}
					}

					if (sc.getPipelineId() != null) {
						PipelineService service = PipelineServiceFactory.newPipelineService();
						service.deletePipelineRecords(sc.getPipelineId(), true, true);
						sc.setPipelineId(null);
					}

					return scf.get(id);
				} else {
					return null;
				}
			}
			return null;
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			//throw ex;	 // rethrow to client?
			return null;
		}	
	}

	@Override
	public IRatingQuery rerunRatingQuery(Long id) {
		try {
			if (checkAdmin()) {

				IRatingQuery rq = rqf.get(id);

				if (rq != null) {
					String url2 = ccf.get().getEngineUrl() +
							"/orchestration/IRatingQuery";

					QueueFactory.getDefaultQueue().add(withUrl(url2.toString()).etaMillis(300000).
							param(AdminOrchestrationActions.RatingActions.getKey(), RatingActions.RERUN.toString()).
							param(AdminOrchestrationTargets.Targets.getKey(), AdminOrchestrationTargets.Targets.RATING.toString()).
							param("id",rq.getId().toString()).
							param("extraKey", "0L"));

					return rq;
				}
			} 

			return null;			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}

	}

	@Override
	public Boolean addRound(Long compId, int uri, String name) {
		try {
			if (checkAdmin()) {
				return cf.addRound(compId, uri, name);
			} 

			return null;			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}


	}
	
	@Override
	public Boolean scrapeRound(Long compId, int uri, String name) {
		try {
			if (checkAdmin()) {
				return cf.scrapeRound(compId, uri, name);
			} 

			return null;			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}


	}

	@Override
	public List<IBlurb> getAllBlurbs(Boolean active) {
		try {
			if (checkAdmin()) {
				return bf.getActive();
			} 

			return null;			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public List<IBlurb> addBlurb(String url, String linkText, String bodyText) {
		try {
			if (checkAdmin()) {
				assert (url != null && !url.isEmpty());
				IBlurb blurb = bf.create();
				String[] chunks = url.split("/");
				String guid = chunks[chunks.length-1];
				if (guid != null) {
					IServerPlace sp = spf.getForGuid(guid);
					blurb.setServerPlace(sp);
					blurb.setServerPlaceId(sp.getId());
					blurb.setBodyText(bodyText);
					blurb.setLinkText(linkText);

					bf.put(blurb);
				} 

				return bf.getActive();
			} 		
			return null;			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public String getListNameForUrl(String url) {
		try {
			if (checkAdmin()) {
				assert (url != null && !url.isEmpty());
				IBlurb blurb = bf.create();
				String[] chunks = url.split("/");
				String guid = chunks[chunks.length-1];
				if (guid != null) {
					IServerPlace sp = spf.getForGuid(guid);
					if (sp != null && sp.getListId() != null) {
						return ttlf.get(sp.getListId()).getTitle();
					}
				} 

				return null;
			} 		
			return null;			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}

	}

	@Override
	public String getDigestPreview(String message, List<Long> blurbIds) {
		try {
			if (checkAdmin()) {
				if (digestEmailer == null) {
					digestEmailer = new DigestEmailer(cf);
				}
				//				List<IBlurb> blurbs = new ArrayList<IBlurb>();
				//				for (Long id : blurbIds) {
				//					blurbs.add(bf.get(id));
				//				}
				//				digestEmailer.setBlurbs(blurbs);
				//				digestEmailer.configure("Subject", "Title", "Body");
				//				return digestEmailer.buildMessage();
				IDigestEmail de = def.create();
				de.setMessage(message);
				de.setBlurbIds(blurbIds);
				de.setSubject("Latest from The Rugby Net");
				de.setTitle("Latest from The Rugby Net");
				def.populatedigestEmail(de);

				digestEmailer.configure(de,null);
				return digestEmailer.buildMessage();
			}
			return null;			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}

	}

	@Override
	public Integer sendDigestEmail(String message, List<Long> blurbIds) {
		try {
			if (checkAdmin()) {
				IDigestEmail email = def.create();
				email.setMessage(message);
				email.setBlurbIds(blurbIds);
				email.setSubject("Latest from The Rugby Net");
				email.setTitle("Latest from The Rugby Net");
				def.put(email);

				List<IAppUser> recips = auf.getDigestEmailRecips();
				for (IAppUser u : recips) {

					if (u != null) {
						String url2 = ccf.get().getEngineUrl() + "/orchestration/IDigestEmail";

						QueueFactory.getDefaultQueue().add(withUrl(url2.toString()).etaMillis(300000).
								param(AdminOrchestrationActions.RatingActions.getKey(), UserActions.SEND_DIGEST.toString()).
								param(AdminOrchestrationTargets.Targets.getKey(), AdminOrchestrationTargets.Targets.USER.toString()).
								param("id",u.getId().toString()).
								param("extraKey", email.getId().toString()));
					}

				}

				return recips.size();
			}
			return null;			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}


	@Override
	public String getDigestUserList() {
		try {
			if (checkAdmin()) {

				String retval = "<h3>Users who have not opted out: </h3>";
				List<IAppUser> recips = auf.getDigestEmailRecips();
				for (IAppUser u : recips) {

					if (u != null) {
						retval += u.getEmailAddress() + "<br/>";
					}				
				}

				return retval;
			}
			return null;			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public Integer archive(List<Long> blurbIds) {
		try {
			if (checkAdmin()) {

				Integer retval = 0;
				for (Long bid : blurbIds) {
					IBlurb b = bf.get(bid);
					if (b!=null) {
						b.setActive(false);
						bf.put(b);
						retval++;
					}
				}

				return retval;
			}
			return null;			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public Integer facebook(List<Long> blurbIds) {
		try {
			if (checkAdmin()) {

				Integer retval = 0;
				for (Long bid : blurbIds) {
					IBlurb b = bf.get(bid);
					if (b!=null) {
						if (postToFacebook(b))
							retval++;
					}
				}

				return retval;
			}
			return null;			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	private final static String BUFFER_CREATE_URL = "https://api.bufferapp.com/1/updates/create.json";
	private boolean postToFacebook(IBlurb b) {
		try {
			String charset = java.nio.charset.StandardCharsets.UTF_8.name();

			StringBuilder params = new StringBuilder();
			params.append("access_token=1/4266c2423560ded98f0b532cac894c07");
			params.append("&profile_ids[]=53f4ab03c4320ad025b8ee70");
			params.append("&text=" + URLEncoder.encode(b.getBodyText(),charset));
			params.append("&shorten=false");
			params.append("&profile_service=facebook");  // << this seems to be ignored, they just go to twitter
			String link = ccf.get().getBaseToptenUrl() + "s/" + b.getServerPlace().getGuid();
			params.append("&media[link]=" + URLEncoder.encode(link,charset));
			params.append("media[title]=" + URLEncoder.encode(b.getLinkText(),charset));

			byte[] postData = params.toString().getBytes(charset);
			int    postDataLength = postData.length;

			URL url = new URL(BUFFER_CREATE_URL);
			HttpURLConnection conn= (HttpURLConnection) url.openConnection();           
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects( false );
			conn.setRequestMethod( "POST" );
			conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
			conn.setRequestProperty( "charset", charset);
			conn.setRequestProperty( "Content-Length", Integer.toString(postDataLength));
			conn.setUseCaches( false );

			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.write(postData);
			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));

			StringBuilder sb = new StringBuilder();
			for (int c; (c = in.read()) >= 0;)
				sb.append((char)c);
			String response = sb.toString();

			JSONObject json = new JSONObject(response);

			return json.getBoolean("success");


			//		} catch (MalformedURLException ex) {
			//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			//			return false;
		} catch (IOException ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
			//		} catch (URISyntaxException ex) {
			//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			//			return false;
		}  catch (JSONException ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
	}

	@Override
	public List<Long> twitter(List<Long> blurbIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override

	public IRound saveRound(IRound r) {
		try {
			if (checkAdmin()) {

				rf.put(r);

				return r;
			}
			return null;			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	public List<String> bulkUploadEmails(List<String> emailsValid) {
		try {
			if (checkAdmin()) {
				List<String> retval = new ArrayList<String>();
				for (String e : emailsValid) {

					retval.add(am.createDigestAccount(e));

				}
				return retval;
			}
			return null;			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}

	@Override
	public IMatchGroup FetchLineups(Long id) {
		try {
			if (checkAdmin()) {
				IMatchGroup m = mf.get(id);
				if (m != null) {
					IRound r = rf.get(m.getRoundId());
					if (r != null) {
						ICompetition c = cf.get(r.getCompId());
						if (c != null) {

							ILineupFetcher luf = luff.getLineupFetcher(c.getCompType());						
							luf.setComp(c);
							luf.setMatch(m);
							List<ILineupSlot> homeLineup = luf.get(true);
							List<ILineupSlot> visitingLineup = luf.get(false);

							if (homeLineup.size() > 0 && visitingLineup.size() > 0) {
								for (ILineupSlot lus : homeLineup) {
									lsf.put(lus);
								}

								for (ILineupSlot lus : visitingLineup) {
									lsf.put(lus);
								}

								m.setWorkflowStatus(WorkflowStatus.LINEUPS);
								mf.put(m);

								return m;
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
	public List<String> getMatchWorkflowLog(Long id) {
		try {
			if (checkAdmin()) {
				IMatchGroup m = mf.get(id);
				if (m != null) {
					return m.getWorkflowLog();
				}
			}
			return null;			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);		
			return null;
		}
	}





}
