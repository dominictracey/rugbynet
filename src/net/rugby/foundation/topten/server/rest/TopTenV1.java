package net.rugby.foundation.topten.server.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rugby.foundation.admin.server.factory.espnscrum.ILineupFetcherFactory;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.core.server.factory.IStandingFullFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.Content;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.ILineupSlot;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.IStandingFull;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.ScrumPlayer;
import net.rugby.foundation.model.shared.ScrumPlayerMatchStats;
import net.rugby.foundation.topten.server.factory.IRoundNodeFactory;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.inject.Injector;



@Api(
		name = "topten",
		version = "v1",
		clientIds = {Ids.WEB_CLIENT_ID, Ids.ANDROID_CLIENT_ID, Ids.IOS_CLIENT_ID},
		audiences = {Ids.ANDROID_AUDIENCE}
		)
public class TopTenV1 {
	private ICompetitionFactory cf;
	private IConfigurationFactory ccf;
	private IRoundNodeFactory rnf;
	private IMatchGroupFactory mgf;
	private IPlayerMatchStatsFactory pmsf;
	private ITeamMatchStatsFactory tmsf;
	private IPlayerFactory pf;
	private IStandingFullFactory sf;
	private ILineupFetcherFactory lff;
	
	protected class TeamMatchStats {
		public Long id; // matchId
		public List<ITeamMatchStats> tmsList = new ArrayList<ITeamMatchStats>();
	}
	protected class PlayerMatchStats {
		public Long id; // matchId
		public List<IPlayerMatchStats> players = new ArrayList<IPlayerMatchStats>();
		//public List<IPlayerMatchStats> teamTwo = new ArrayList<IPlayerMatchStats>();
	}
	protected class CompetitionStanding {
		public Long id; // compId
		public List<IStandingFull> standings = new ArrayList<IStandingFull>();
	}
	
	private static Injector injector = null;

	public TopTenV1() {
		if (injector == null) {
			injector = BPMServletContextListener.getInjectorForNonServlets();
		}

		this.cf = injector.getInstance(ICompetitionFactory.class);
		this.ccf = injector.getInstance(IConfigurationFactory.class);
		this.rnf = injector.getInstance(IRoundNodeFactory.class);
		this.mgf = injector.getInstance(IMatchGroupFactory.class);
		this.pmsf = injector.getInstance(IPlayerMatchStatsFactory.class);
		this.tmsf = injector.getInstance(ITeamMatchStatsFactory.class);
		this.pf = injector.getInstance(IPlayerFactory.class);
		this.sf = injector.getInstance(IStandingFullFactory.class);
		this.tmsf = injector.getInstance(ITeamMatchStatsFactory.class);
		this.lff = injector.getInstance(ILineupFetcherFactory.class);
	}

	@ApiMethod(name = "content.getcontent", httpMethod = "GET")
	public Content getcontent() {
		return new Content(90123124L,"OK");
		//		return (Competition) cf.getUnderwayComps().get(0);
	}

	@ApiMethod(name = "competition.getGlobal", path="competitions/global", httpMethod = "GET")
	public ICompetition getGlobal() {

		return cf.getUnderwayComps().get(0);
	}

	@ApiMethod(name = "competition.get", path="competitions/get", httpMethod = "GET")
	public ICompetition getCompetition(@Named("id") Long id) {

		return cf.get(id);
	}

	@ApiMethod(name = "competition.getAll", path="competitions", httpMethod = "GET")
	public List<ICompetition> getallcompetition() {

		return cf.getUnderwayComps();
	}

	@ApiMethod(name = "configuration.get", path="configuration", httpMethod = "GET")
	public ICoreConfiguration getConfiguration() {

		return ccf.get();
	}
	
	@ApiMethod(name = "series.position", path="series/position/get", httpMethod="GET")
	public List<RoundNode> getGraphableSeries(@Named("compId") Long compId, @Named("position") int positionOrdinal) {
		return rnf.get(compId,positionOrdinal);
		
	}
	
	@ApiMethod(name = "match.getByEspnId", path="match/getByEspnId", httpMethod="GET")
	public IMatchGroup getMatchByEspnId(@Named("espnId") Long espnId) {
		return mgf.getMatchByEspnId(espnId);
		
	}

	@ApiMethod(name = "match.getScrumPlayerMatchStats", path="match/getScrumPlayerMatchStats", httpMethod="GET")
	public PlayerMatchStats getScrumPlayerMatchStats(@Named("matchId") Long matchId) {
		PlayerMatchStats pms = new PlayerMatchStats();
		pms.id = matchId;
		List<IPlayerMatchStats> list = pmsf.getByMatchId(matchId);
		
//		Long team = list.get(0).getTeamId();		
//		for(IPlayerMatchStats cur : list){
//			if(cur.getTeamId().equals(team)){
//				pms.teamOne.add(cur);
//			}
//			else{
//				pms.teamTwo.add(cur);
//			}
//		}
		pms.players.addAll(list);
		
		return pms;		
	}

	@ApiMethod(name = "match.putScrumPlayerMatchStats", path="match/putScrumPlayerMatchStats", httpMethod="PUT")
	public IPlayerMatchStats putScrumPlayerMatchStats(ScrumPlayerMatchStats pms) {
		pmsf.put(pms);

		return pms;
		
	}
	
	@ApiMethod(name = "player.getByEspnId", path="player/getByEspnId", httpMethod="GET")
	public IPlayer getPlayerByEspnId(@Named("espnId") Long espnId) {
		return pf.getByScrumId(espnId);
		
	}
	
	@ApiMethod(name = "player.putPlayer", path="match/putPlayer", httpMethod="PUT")
	public IPlayer putPlayer(ScrumPlayer p) {
		pf.put(p);

		return p;
		
	}
	
	@ApiMethod(name = "competition.getStandings", path="competitions/getStandings", httpMethod="GET")
	public CompetitionStanding getStandings(@Named("compId") Long compId) {
		CompetitionStanding cs = new CompetitionStanding();
		cs.standings = sf.getLatestForComp(compId);
		cs.id = compId;
		
		return cs;
	}
	
	@ApiMethod(name = "match.get", path="match/get", httpMethod="GET")
	public IMatchGroup getMatch(@Named("id") Long id) {
		return mgf.get(id);
		
	}
	
	@ApiMethod(name = "teamMatchStats.get", path="teamMatchStats/get", httpMethod="GET")
	public TeamMatchStats getTeamMatchStats(@Named("matchId") Long matchId) {
		TeamMatchStats retval = new TeamMatchStats();
		retval.id = matchId;
		ITeamMatchStats tms = tmsf.getHomeStats(mgf.get(matchId));
		retval.tmsList.add(tms);
		tms = tmsf.getVisitStats(mgf.get(matchId));
		retval.tmsList.add(tms);
		return retval;
	}
	
	@ApiMethod(name = "teamLineups.get", path="teamLineups/get", httpMethod="GET")
	public List<ILineupSlot> getTeamLineups(@Named("matchId") Long matchId) {
		List<ILineupSlot> retval = new ArrayList<>();
		retval = lff.getLineupFetcher(null).get(true);
		
		
		return retval;
	}
}
