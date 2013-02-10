/**
 * 
 */
package net.rugby.foundation.game1.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlecode.objectify.ObjectifyService;

import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.core.server.factory.IClubhouseFactory;
import net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.game1.client.Game1Service;
import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.server.factory.IConfigurationFactory;
import net.rugby.foundation.game1.server.factory.IEntryFactory;
import net.rugby.foundation.game1.server.factory.ILeaderboardFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.server.factory.IMatchEntryFactory;
import net.rugby.foundation.game1.server.factory.IMatchStatsFactory;
import net.rugby.foundation.game1.shared.ClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.Configuration;
import net.rugby.foundation.game1.shared.Entry;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.IConfiguration;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.game1.shared.ILeaderboard;
import net.rugby.foundation.game1.shared.ILeague;
import net.rugby.foundation.game1.shared.IMatchEntry;
import net.rugby.foundation.game1.shared.IMatchStats;
import net.rugby.foundation.game1.shared.Leaderboard;
import net.rugby.foundation.game1.shared.LeaderboardRow;
import net.rugby.foundation.game1.shared.League;
import net.rugby.foundation.game1.shared.MatchEntry;
import net.rugby.foundation.game1.shared.MatchStats;
import net.rugby.foundation.game1.shared.RoundEntry;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.IClubhouseMembership;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.LoginInfo;

/**
 * @author home
 *
 */
@Singleton
public class Game1ServiceImpl extends RemoteServiceServlet implements Game1Service {

	private static final long serialVersionUID = 1L;
	//private final Objectify ofy;
	private IEntryFactory ef;
	private ILeaderboardFactory lbf;
	private ICompetitionFactory cf;
	private IAppUserFactory auf;
	private IClubhouseFactory chf;
	private IClubhouseMembershipFactory chmf;
	private IClubhouseLeagueMapFactory chlmf;
	private ILeagueFactory lf;
	private IConfigurationFactory configf;
	private IMatchStatsFactory msf;
	private IMatchEntryFactory mef;
	
	private static boolean dbRegistrationsComplete = false;

	public Game1ServiceImpl() {
		//this.ofy = DataStoreFactory.getOfy();

		if (!dbRegistrationsComplete) {
			ObjectifyService.register(Entry.class);
			ObjectifyService.register(RoundEntry.class);
			ObjectifyService.register(MatchEntry.class);
			ObjectifyService.register(Leaderboard.class);
			ObjectifyService.register(LeaderboardRow.class);
			ObjectifyService.register(League.class);
			ObjectifyService.register(ClubhouseLeagueMap.class);
			ObjectifyService.register(Configuration.class);
			ObjectifyService.register(MatchStats.class);
			dbRegistrationsComplete = true;
		}
	}

	@Inject
	public void setFactories(IEntryFactory ef, ILeaderboardFactory lbf, ICompetitionFactory cf, IAppUserFactory auf,
			IClubhouseFactory chf, IClubhouseMembershipFactory chmf, IClubhouseLeagueMapFactory chlmf, 
			ILeagueFactory lf, IConfigurationFactory configf, IMatchStatsFactory msf, IMatchEntryFactory mef) {
		this.ef = ef;
		this.lbf = lbf;
		this.cf = cf;
		this.auf = auf;
		this.chf = chf;
		this.chmf = chmf;
		this.chlmf = chlmf;
		this.lf = lf;
		this.configf = configf;
		this.msf = msf;
		this.mef = mef;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.Game1Service#getEntry(java.lang.Long)
	 */
	@Override
	public IEntry getEntry(Long id) {
		IEntry entry = null;
		try {
			ef.setId(id);
			entry = ef.getEntry();		
		} catch (Throwable ex) {
			Logger.getLogger("Game1 Service").log(Level.SEVERE, "getEntry: " + ex.getMessage());
			return null;
		}
		return entry;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.Game1Service#saveEntry(net.rugby.foundation.game1.shared.Entry)
	 */
	@Override
	public IEntry saveEntry(IEntry entry) {
		try {
			entry = ef.put(entry);
		} catch (Throwable ex) {
			Logger.getLogger("Game1 Service").log(Level.SEVERE, "saveEntry: " + ex.getMessage());
			return null;
		}
		return entry;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.Game1Service#deleteEntry(net.rugby.foundation.game1.shared.Entry)
	 */
	@Override
	public Boolean deleteEntry(IEntry entry) {
		//		try {
		//			for (IRoundEntry re : entry.getRoundEntries().values()) {
		//				ofy.delete(new Key<RoundEntry>(RoundEntry.class,re.getId()));
		//			}
		//			ofy.delete(new Key<Entry>(Entry.class,entry.getId()));
		//		} catch (Throwable ex) {
		//			Logger.getLogger("Game1 Service").log(Level.SEVERE, "deleteEntry: " + ex.getMessage());
		//			return false;
		//		}
		return true;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.Game1Service#deleteEntriesForUser(net.rugby.foundation.model.shared.AppUser)
	 */
	@Override
	public Boolean deleteEntriesForUser(IAppUser user) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.Game1Service#getEntriesForCurrentUser(java.lang.Long)
	 */
	@Override
	public ArrayList<IEntry> getEntriesForCurrentUser(Long compId) {
		IAppUser u = getAppUser();

		if (u == null) {
			return null;
		}

		ArrayList<IEntry> entries = null;

		try {
			ef.setUserIdAndCompId(u.getId(), compId);
			entries = ef.getEntries();
		} catch (Throwable ex) {
			Logger.getLogger("Game1 Service").log(Level.SEVERE, "getEntriesForCurrentUser: " + ex.getMessage());
			return null;
		}
		return entries;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.Game1Service#createEntry(java.lang.String)
	 */
	@Override
	public IEntry createEntry(String name, Long compId) {
		IAppUser u = getAppUser();

		if (u == null) {
			return null;
		}

		IEntry entry = null;
		try {
			ef.setId(-1L);  //ask for an empty one
			entry = ef.getEntry();		
			entry.setName(name);
			entry.setCompId(compId);
			entry.setCreated(new Date());
			entry.setOwnerId(u.getId());
			entry = ef.put(entry);

			// now add the entry.id to the CC.CLM.get(CompId) and to all the Leagues in the AppUser’s ClubhouseMemberships’ CLM entries for the Competition.
			chmf.setAppUserId(u.getId());
			List<IClubhouseMembership> list = chmf.getList();  // all the user's clubhouses

			// for each clubhouse, add the new entry to the league for this comp
			for (IClubhouseMembership chm : list) {

				// the factory will fetch if it exists or create the league and CLM otherwise
				chlmf.setClubhouseAndCompId(compId, chm.getClubhouseID());

				IClubhouseLeagueMap clm = chlmf.get();
				lf.setId(clm.getLeagueId());
				ILeague league = lf.get();
				league.getEntryIds().add(entry.getId());
				lf.put(league);
			}


		} catch (Throwable ex) {
			Logger.getLogger("Game1 Service").log(Level.SEVERE, "createEntry: " + ex.getMessage());
			return null;
		}
		return entry;

	}

	private IAppUser getAppUser()
	{
		// do they have a session going?
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession(false);
		if (session != null) {
			LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
			if (loginInfo.isLoggedIn()) {
				auf.setEmail(loginInfo.getEmailAddress());
				return auf.get();
			}
		}

		return null;

	}


	@Override
	public ILeaderboard getLeaderboard(Long compId, Long clubhouseId) {
		ILeaderboard lb = null;
		try {

			// first find the prevRound for the comp
			//cf.setId(compId);
			ICompetition comp = getComp(compId);  
			
			IRound round = comp.getPrevRound();
			if (round == null) {
				return null; // the first round isn't complete yet - no leaderboard available
			}

			chlmf.setClubhouseAndCompId(compId, clubhouseId);

			lbf.setClmAndRound(chlmf.get(), round);

			lb = lbf.get();

		} catch (Throwable ex) {
			Logger.getLogger("Game1 Service").log(Level.SEVERE, "getLeaderboard: " + ex.getMessage());
			return null;
		}

		return lb;
	}
	@Override
	public IConfiguration getConfiguration() {
		try {			
			return configf.get();

		} catch (Throwable ex) {
			Logger.getLogger("Game1 Service").log(Level.SEVERE, "getLeaderboard: " + ex.getMessage());
			return null;
		}

	}

	@Override
	public Boolean createNewClubhouseLeagues(IClubhouse clubhouse) {
		try {			

			// create leagues for all competitions underway
			IConfiguration config = getConfiguration();
			for (Long compId : config.getLeagueIdMap().keySet()) {


				// now create a ClubhouseLeagueMap and League
				chlmf.setClubhouseAndCompId(compId, clubhouse.getId());
				IClubhouseLeagueMap chlm = chlmf.get();
				chlmf.put(chlm);

				lf.setId(chlm.getLeagueId());
				ILeague league = lf.get();

				// add entries for the owner to all these leagues
				List<IEntry> entries = getEntriesForCurrentUser(compId);
				for (IEntry e : entries) {
					league.addEntry(e);
				}
				lf.put(league);

				// leaderboards will be created by the orchestrations
				// ILeaderboard lb = createLeaderboard(league.getId());

				Logger.getLogger("Game1 Service").log(Level.INFO, "createNewClubhouseLeagues: new league Id is " + league.getId().toString());
			}

			return true;

		} catch (Throwable ex) {
			Logger.getLogger("Game1 Service").log(Level.SEVERE, "getLeaderboard: " + ex.getMessage());
			return false;
		}

	}

	@Override
	public IConfiguration updateConfiguration(IConfiguration config, List<Long> compsToAdd, List<Long> compsToDrop) {
		for (Long compId : compsToAdd) {
			cf.setId(compId);
			ICompetition comp = cf.getCompetition();



			// create a league for every active clubhouse
			List<IClubhouse> clubhouses = chf.getAll();
			for (IClubhouse ch : clubhouses) {
				lf.setId(0L);
				ILeague league = lf.get();
				lf.put(league);

				chlmf.setId(0L);
				IClubhouseLeagueMap clm = chlmf.get();
				clm.setClubhouseId(ch.getId());
				clm.setCompId(compId);
				clm.setLeagueId(league.getId());

				chlmf.put(clm);

				// reference the CompClubhouse in the config
				if (ch.getId().equals(comp.getCompClubhouseId())) {
					config.getCompetitionClubhouseLeageMapIds().add(clm.getId());
				}

			}


		}

		configf.put(config);

		return config;
	}

	@Override
	public IEntry getEntryByName(String name, Long compId) {
		try {			
			IAppUser u = getAppUser();

			if (u == null) {
				return null;
			}

			cf.setId(compId);
			ICompetition comp = cf.getCompetition();

			ef.setNameAndComp(name, comp, u.getId());

			return ef.getEntry();

		} catch (Throwable ex) {
			Logger.getLogger("Game1 Service").log(Level.SEVERE, "getEntryByName: " + ex.getMessage());
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.Game1Service#getMatchStats(java.lang.Long)
	 */
	@Override
	public List<IMatchStats> getMatchStats(Long matchId) {
		try {
			msf.setMatchId(matchId);
			return msf.getAll();
		} catch (Throwable ex) {
			Logger.getLogger("Game1 Service").log(Level.SEVERE, "getEntryByName: " + ex.getMessage());
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.Game1Service#updateMatchStats(net.rugby.foundation.game1.shared.IConfiguration, java.util.List)
	 */
	@Override
	public IConfiguration updateMatchStats(IConfiguration config, List<Long> compsToRedo) {
		
		for (Long compId : compsToRedo) {
			ICompetition comp = getComp(compId);
			for (IRound r: comp.getRounds()) {
				for (IMatchGroup m: r.getMatches()) {
					msf.setMatchId(m.getId());
					msf.delete();
					IMatchStats ms = msf.getMatchStatsShard();
					
					Set<IMatchEntry> mes = mef.getForMatch(m.getId());
					for (IMatchEntry me : mes) {
						if (me.getTeamPickedId().equals(m.getHomeTeamId())) {
							ms.setNumHomePicks(ms.getNumHomePicks()+1);
						}
					}
					ms.setNumPicks((long) mes.size());
					msf.put(ms);
				}
			}
		}
		return config;
	}
	
	/*
	 * TODO stole this out of the Core code - can we share cross-module?
	 */
	
	private ICompetition getComp(Long compId) {
		try {

			cf.setId(compId);
			return cf.getCompetition();

		} catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}
}
