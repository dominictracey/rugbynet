package net.rugby.foundation.topten.server.rest;

import java.util.List;

import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.Content;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
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
	public List<IPlayerMatchStats> getScrumPlayerMatchStats(@Named("matchId") Long matchId) {
		return pmsf.getByMatchId(matchId);
		
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
}
