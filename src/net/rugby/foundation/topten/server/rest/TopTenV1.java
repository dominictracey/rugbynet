package net.rugby.foundation.topten.server.rest;

import java.util.List;

import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.Content;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
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
	private IRatingSeriesFactory rsf;
	private IPlayerRatingFactory prf;
	private IPlayerMatchStatsFactory pmsf;
	private IMatchGroupFactory mgf;
	private IRoundFactory rf;
	private IPlayerFactory pf;
	private ITeamGroupFactory tf;
	private IUniversalRoundFactory urf;
	private IRoundNodeFactory rnf;

	private static Injector injector = null;

	public TopTenV1() {
		if (injector == null) {
			injector = BPMServletContextListener.getInjectorForNonServlets();
		}

		this.cf = injector.getInstance(ICompetitionFactory.class);
		this.ccf = injector.getInstance(IConfigurationFactory.class);
		this.rsf = injector.getInstance(IRatingSeriesFactory.class);
		this.prf = injector.getInstance(IPlayerRatingFactory.class);
		this.pmsf = injector.getInstance(IPlayerMatchStatsFactory.class);
		this.mgf = injector.getInstance(IMatchGroupFactory.class);
		this.rf = injector.getInstance(IRoundFactory.class);
		this.pf = injector.getInstance(IPlayerFactory.class);
		this.tf = injector.getInstance(ITeamGroupFactory.class);
		this.urf = injector.getInstance(IUniversalRoundFactory.class);
		this.rnf = injector.getInstance(IRoundNodeFactory.class);
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

//	final String GRAPHABLE_SERIES_CACHE_KEY = "graphableSeriesCacheKey";
	
	@ApiMethod(name = "series.position", path="series/position/get", httpMethod="GET")
	public List<RoundNode> getGraphableSeries(@Named("compId") Long compId, @Named("position") int positionOrdinal) {
//		GraphableSeries retval = getFromCache(compId,positionOrdinal);
//		
//		if (retval != null && retval.roundNodes != null && retval.roundNodes.size() > 0 && retval.roundNodes.get(0).playerMatches != null && retval.roundNodes.get(0).playerMatches.keySet().size()>30) {
//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Dropping position " + positionOrdinal + " from cache. Has length of " + retval.roundNodes.get(0).playerMatches.keySet().size());
//			dropFromCache(getCacheKey(compId,positionOrdinal));	
//			retval = null;
//		}
//		
//		if (retval == null) {
//			retval = buildPage(compId, positionOrdinal);
//
//			putToCache(getCacheKey(compId,positionOrdinal),retval);
//		}
//		
//		
//
//		return retval.roundNodes;
		return rnf.get(compId,positionOrdinal);
		
	}

//	private GraphableSeries buildPage(Long compId, int positionOrdinal) {
//		GraphableSeries retval = new GraphableSeries();
//		// get the position series for the comp
//		IRatingSeries s = rsf.get(compId, RatingMode.BY_POSITION);
//
//		if (s == null) {
//			return retval;
//		}
//
//		if (!rnMapMap.containsKey(compId)) {
//			rnMapMap.put(compId, new HashMap<Integer,RoundNode>());
//		}
//		
//		// for each RatingGroup of the comp, add a new GraphableSeries to the retval
//		IRatingGroup g = s.getRatingGroups().get(0); //latest is first
//		// use the in form matrix (or the BEST_YEAR for the global comp)
//		for (IRatingMatrix m : g.getRatingMatrices()) {
//			if (m.getCriteria().equals(Criteria.IN_FORM) || m.getCriteria().equals(Criteria.BEST_YEAR)) {
//				
//				// now the position selected
//				populateGS(compId, retval, m.getRatingQueries().get(positionOrdinal),g, positionOrdinal);
//
//			}
//		}
//		return retval;
//	}



	
}
