package net.rugby.foundation.topten.server.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.PlayerRating.RatingComponent;
import net.rugby.foundation.model.shared.RatingMode;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
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

	final String GRAPHABLE_SERIES_CACHE_KEY = "graphableSeriesCacheKey";
	
	@ApiMethod(name = "series.position", path="series/position/get", httpMethod="GET")
	public List<RoundNode> getGraphableSeries(@Named("compId") Long compId, @Named("position") int positionOrdinal) {
		GraphableSeries retval = getFromCache(compId,positionOrdinal);
		
		if (retval != null && retval.roundNodes != null && retval.roundNodes.size() > 0 && retval.roundNodes.get(0).playerMatches != null && retval.roundNodes.get(0).playerMatches.keySet().size()>30) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Dropping position " + positionOrdinal + " from cache. Has length of " + retval.roundNodes.get(0).playerMatches.keySet().size());
			dropFromCache(getCacheKey(compId,positionOrdinal));	
		}
		
		if (retval == null) {
			retval = buildPage(compId, positionOrdinal);

			putToCache(getCacheKey(compId,positionOrdinal),retval);
		}
		
		

		return retval.roundNodes;
	}

	private GraphableSeries buildPage(Long compId, int positionOrdinal) {
		GraphableSeries retval = new GraphableSeries();
		// get the position series for the comp
		IRatingSeries s = rsf.get(compId, RatingMode.BY_POSITION);

		if (s == null) {
			return retval;
		}

		rnMap = new HashMap<Integer,RoundNode>();
		
		// for each RatingGroup of the comp, add a new GraphableSeries to the retval
		IRatingGroup g = s.getRatingGroups().get(0); //latest is first
		// use the in form matrix (or the BEST_YEAR for the global comp)
		for (IRatingMatrix m : g.getRatingMatrices()) {
			if (m.getCriteria().equals(Criteria.IN_FORM) || m.getCriteria().equals(Criteria.BEST_YEAR)) {
				// now the position selected
				populateGS(retval, m.getRatingQueries().get(positionOrdinal),g, positionOrdinal);

			}
		}
		return retval;
	}

	private String getCacheKey(Long compId, int positionOrdinal) {
		return GRAPHABLE_SERIES_CACHE_KEY + compId.toString() + "_" + positionOrdinal;
	}

	Map<Integer,RoundNode> rnMap = null;
	Map<String, String> teamMap = null; // key: playerName, value: teamAbbr
	Map<String, String> positionMap = null; // key: playerName, value: position Style
	
	private void populateGS(GraphableSeries retval, IRatingQuery rq, IRatingGroup g, int positionOrdinal) {


		List<IPlayerRating> prq = prf.query(rq);
		Map<String,Long> players = new HashMap<String,Long>();
		teamMap = new HashMap<String,String>();
		positionMap = new HashMap<String,String>();
		
		for (IPlayerRating pr : prq) {
			for (RatingComponent rc : pr.getRatingComponents()) {
				try {
					IPlayerMatchStats pms = pmsf.get(rc.getPlayerMatchStatsId());
					IMatchGroup m = mgf.get(pms.getMatchId());
					IRound r = rf.get(m.getRoundId());
					RoundNode rn = getRoundNode(retval, r.getUrOrdinal());
					PlayerMatch pm = new PlayerMatch();
					pm.homeTeamAbbr = m.getHomeTeam().getAbbr();
					pm.homeTeamScore = new Integer(m.getSimpleScoreMatchResult().getHomeScore()).toString();
					pm.label = m.getDisplayName();
					pm.matchDate = m.getDate().toString();
					pm.matchRating = String.format("%d",(int)rc.getScaledRating()) + " (" + String.format("%.02f", rc.getRawScore()) + ")";
					pm.minutesPlayed = pms.getTimePlayed().toString();
					pm.position = "position " + pms.getPosition().getStyle();
					pm.visitingTeamAbbr = m.getVisitingTeam().getAbbr();
					pm.visitingTeamScore = new Integer(m.getSimpleScoreMatchResult().getVisitScore()).toString();
					pm.teamAbbr = tf.get(pms.getTeamId()).getAbbr();

					getRating(pm, g.getRatingSeries(), r.getUrOrdinal(), positionOrdinal, pms.getPlayerId());
					setNotes(pm, pms);
					
					IPlayer p = pf.get(pms.getPlayerId());
					if (!players.containsKey(p.getDisplayName())) { players.put(p.getDisplayName(),p.getId()); }	
					if (!teamMap.containsKey(p.getDisplayName())) { teamMap.put(p.getDisplayName(), tf.get(pms.getTeamId()).getAbbr()); }		
					if (!positionMap.containsKey(p.getDisplayName())) { positionMap.put(p.getDisplayName(), pm.position); }	
					
					rn.playerMatches.put(p.getDisplayName(), pm);
				} 
				catch (Throwable ex) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "problem building roundNodes", ex);
					// just keep swimming...
				}
			}
		}
		
		for (RoundNode rn : rnMap.values()) {
			fillInDidNotPlays(rn, players, g.getRatingSeries(), positionOrdinal);
			retval.roundNodes.add(rn);
		}
		
		Collections.sort(retval.roundNodes, new Comparator<RoundNode>() {
			@Override
		    public int compare(RoundNode e1, RoundNode e2) {
		        if(e1.round < e2.round){
		            return 1;
		        } else {
		            return -1;
		        }
		    }
		});
		

	}
	
	private void fillInDidNotPlays(RoundNode rn, Map<String, Long> players, IRatingSeries rs, int positionOrdinal) {
		List<String> temp = new ArrayList<String>();
		temp.addAll(players.keySet());
		
		for (String name : rn.playerMatches.keySet()) {
			temp.remove(name);
		}
		
		
		
		for (String name: temp) {
			PlayerMatch pm = newDidNotPlay();
			getRating(pm, rs, rn.round, positionOrdinal, players.get(name));
			if (teamMap.containsKey(name)) {
				pm.teamAbbr = teamMap.get(name);
			}
			if (positionMap.containsKey(name)) {
				pm.position = positionMap.get(name);
			}
			rn.playerMatches.put(name, pm);
		}
	}

	private PlayerMatch newDidNotPlay() {

		// now add in Did Not Play items to the RoundNode	
		PlayerMatch pm = new PlayerMatch();
		pm.homeTeamAbbr = "NON";
		pm.homeTeamScore = " ";
		pm.label = "Did not Play";
		pm.matchDate = " ";
		pm.matchRating = " - ";
		pm.minutesPlayed = " ";
		pm.position = " ";
		pm.visitingTeamAbbr = "NON";
		pm.visitingTeamScore = " ";

		return pm;
	}

	Map<Long,List<IPlayerRating>> prMap = new HashMap<Long,List<IPlayerRating>>();
	
	private void getRating(PlayerMatch pm, IRatingSeries ratingSeries, int urOrdinal, int positionOrdinal, Long playerId) {

		boolean found = false;
		for (IRatingGroup rg: ratingSeries.getRatingGroups()) {
			if (rg.getUniversalRoundOrdinal() == urOrdinal) {
				for (IRatingMatrix m : rg.getRatingMatrices()) {
					if (m.getCriteria().equals(Criteria.IN_FORM) || m.getCriteria().equals(Criteria.BEST_YEAR)) {
						IRatingQuery rq = m.getRatingQueries().get(positionOrdinal);
						List<IPlayerRating> prl = null;
						if (prMap.containsKey(rq.getId())) {
							prl = prMap.get(rq.getId());
						} else {
							prl = prf.query(rq);
							if (prl != null) {
								prMap.put(rq.getId(), prl);
							} else {
								// bad
								Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Could not find playerRatings for RatingQuery " + rq.getLabel() + rq.getId() + "... bailing"); 
								break;
							}
						}
						// now look through the playerRatings to see where our player is
						int count = 1;
						for (IPlayerRating pr : prl) {
							if (pr.getPlayerId().equals(playerId)) {
								pm.ranking = count;
								pm.rating = pr.getRating();
								found = true;
							}
							count++;
						}
					}
				}
				break;
			}
		}
		
		if (!found) {
			pm.ranking = 31;
			pm.rating = 0;
		}
	}

	private void setNotes(PlayerMatch pm, IPlayerMatchStats pms) {
		Integer count = 0;
		
		// tries
		if (pms.getTries() > 0) {
			if (pms.getTries() > 1) {
				pm.notes.put(count.toString(), pms.getTries() + " tries");
			} else {
				pm.notes.put(count.toString(), pms.getTries() + " try");
			}
			count++;
		}
		
		// try assists
		if (pms.getTryAssists() > 0) {
			if (pms.getTryAssists() > 1) {
				pm.notes.put(count.toString(), pms.getTryAssists() + " try assists");
			} else {
				pm.notes.put(count.toString(), pms.getTryAssists() + " try assist");
			}
			count++;
		}
		
		// tackles 
		if (pms.getTacklesMade() > 10) {
			pm.notes.put(count.toString(), pms.getTacklesMade() + " tackles");
			count++;
		}
		
		// tackles missed
		if (pms.getTacklesMissed() > 3) {
			pm.notes.put(count.toString(), pms.getTacklesMissed() + " tackles missed");
			count++;
		}
		
		// offloads
		if (pms.getOffloads() > 2) {
			pm.notes.put(count.toString(), pms.getOffloads() + " offloads");
			count++;
		}
		
		// defenders beaten
		if (pms.getDefendersBeaten() > 2) {
			pm.notes.put(count.toString(), pms.getDefendersBeaten() + " defenders beaten");
			count++;
		}
		
		// clean breaks
		if (pms.getCleanBreaks() > 2) {
			pm.notes.put(count.toString(), pms.getCleanBreaks() + " clean breaks");
			count++;
		}
		
		// runs
		if (pms.getRuns() > 6) {
			pm.notes.put(count.toString(), pms.getRuns() + " carries");
			count++;
		}
		
		// metres carries
		if (pms.getMetersRun() > 30) {
			pm.notes.put(count.toString(), pms.getMetersRun() + " metres gained");
			count++;
		}
		
		//lineouts
		if (pms.getLineoutsWonOnThrow() > 3) {
			pm.notes.put(count.toString(), pms.getLineoutsWonOnThrow() + " lineouts won");
			count++;
		}
		
		//lineouts
		if (pms.getLineoutsStolenOnOppThrow() > 3) {
			pm.notes.put(count.toString(), pms.getLineoutsStolenOnOppThrow() + " lineouts stolen");
			count++;
		}
		
		//penalties conceded
		if (pms.getPenaltiesConceded() > 1) {
			pm.notes.put(count.toString(), pms.getPenaltiesConceded() + " penalties conceded");
			count++;
		}
				
		//yellow card
		if (pms.getYellowCards() > 0) {
			pm.notes.put(count.toString(), pms.getYellowCards() + " yellow card");
			count++;
		}
		
		//red card
		if (pms.getRedCards() > 1) {
			pm.notes.put(count.toString(), pms.getRedCards() + " red card");
			count++;
		}
	}

	private RoundNode getRoundNode(GraphableSeries retval, int urOrd) {
		// do we already have it?
		if (rnMap.containsKey(urOrd)) {
			return rnMap.get(urOrd);
		} else {
			RoundNode rn = new RoundNode();
			rn.round = urOrd;
			rn.label = urf.get(urOrd).shortDesc;
			rnMap.put(urOrd, rn);
			return rn;
		}
	}

	private GraphableSeries getFromCache(Long compId, Integer positionOrdinal) {
		try {
			byte[] value = null;
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			GraphableSeries page = null;

			value = (byte[])syncCache.get(getCacheKey(compId,positionOrdinal));
			if (value != null) {
				// send back the cached version
				ByteArrayInputStream bis = new ByteArrayInputStream(value);
				ObjectInput in;

				in = new ObjectInputStream(bis);

				Object obj = in.readObject();
				if (obj instanceof GraphableSeries) {
					page = (GraphableSeries)obj;
				}

				bis.close();
				in.close();
			}

			return page;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			return null;
		}

	}
	

	private void putToCache(String key, GraphableSeries page) {
		try {
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(page);
			byte[] yourBytes = bos.toByteArray(); 

			out.close();
			bos.close();

			syncCache.put(key, yourBytes);
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);

		}

	}
	
	private void dropFromCache(String id) {
		try {
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();

			if (syncCache.contains(id)) {
				syncCache.delete(id);
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
		}	
	}
}
