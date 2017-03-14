package net.rugby.foundation.topten.server.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.Criteria;
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
import net.rugby.foundation.topten.server.rest.RoundNode;
import net.rugby.foundation.topten.server.rest.RoundNode.PlayerMatch;

public abstract class BaseRoundNodeFactory extends BaseCachingFactory<RoundNode>
		implements IRoundNodeFactory {

	final String GRAPHABLE_SERIES_CACHE_KEY = "graphableSeriesCacheKey";

	protected IRatingSeriesFactory rsf;
	protected IPlayerRatingFactory prf;
	protected IPlayerMatchStatsFactory pmsf;
	protected IMatchGroupFactory mgf;
	protected IRoundFactory rf;
	protected ITeamGroupFactory tf;
	protected IPlayerFactory pf;
	protected IUniversalRoundFactory urf;

	private IRatingGroupFactory rgf;
//	protected IPlayerMatchFactory pmf;

	//@Inject
	public BaseRoundNodeFactory(IRatingSeriesFactory rsf, IPlayerRatingFactory prf, IPlayerMatchStatsFactory pmsf, 
								IMatchGroupFactory mgf, IRoundFactory rf, ITeamGroupFactory tf, IPlayerFactory pf, IUniversalRoundFactory urf, IRatingGroupFactory rgf) {
		this.rsf = rsf;
		this.prf = prf;
		this.pmsf = pmsf;
		this.mgf = mgf;
		this.rf = rf;
		this.tf = tf;
		this.pf = pf;
		this.urf = urf;
		this.rgf = rgf;
	}

	@Override 
	public RoundNode create(Long compId, int urOrd, String label, int positionOrdinal) {
		RoundNode rn = create();
		rn.playerMatches = new HashMap<String, List<PlayerMatch>>();
		rn.compId = compId;
		rn.round = urOrd;
		rn.label = label;
		rn.setPositionOrdinal(positionOrdinal);
		
		return rn;
	}
	
	@Override
	public List<RoundNode> get(Long compId, int positionOrdinal) {
		List<RoundNode> retval = getList(getCacheKey(compId, positionOrdinal));
		if (retval == null) {
			retval = getListFromPersistentDatastore(compId, positionOrdinal);
			putList(getCacheKey(compId, positionOrdinal), retval);
			// now see if we need to recreate
			//ICompetition comp = cf.get(compId);
			IRatingSeries s = rsf.get(compId, RatingMode.BY_POSITION);

			// is the latest RatingGroup for the series the same as the first one in the retval?
			IRatingGroup g = rgf.get(s.getRatingGroupIds().get(0)); //latest is first
			
			if (retval.size() == 0 || g.getUniversalRoundOrdinal() != retval.get(0).round)  {
				rnMapMap.clear();
				
				// need to redo it all!
				for (RoundNode rn: retval) {
					delete(rn); //delete from datastore
				}
			
				retval.clear();
				
				// use the in form matrix (or the BEST_YEAR for the global comp)
				for (IRatingMatrix m : g.getRatingMatrices()) {
					if (m.getCriteria().equals(Criteria.IN_FORM) || m.getCriteria().equals(Criteria.BEST_YEAR)) {
						
						// now the position selected
						populateGS(compId, retval, m.getRatingQueries().get(positionOrdinal),g, positionOrdinal);
	
					}
				}
			
				for (RoundNode rn : retval) {
					put(rn);
				}
				
				putList(getCacheKey(compId, positionOrdinal),retval);
			}
		}
		return retval;
	}

	/**
	 * Allow the workflow to force us to see if any more are needed.
	 */
	@Override
	public void dropListFromCache(Long compId, int posOrd) {
		dropFromCache(getCacheKey(compId, posOrd));
	}
	
	abstract protected List<RoundNode> getListFromPersistentDatastore(Long compId, int positionOrdinal);
	
	private String getCacheKey(Long compId, int positionOrdinal) {
		return GRAPHABLE_SERIES_CACHE_KEY + compId.toString() + "_" + positionOrdinal;
	}
	
	Map<Long,Map<Integer,RoundNode>> rnMapMap = new HashMap<Long,Map<Integer,RoundNode>>(); // key: compId, value: Map key: roundOrd, value: value RoundNode for that comp/pos
	Map<String, String> teamMap = new HashMap<String,String>();; // key: playerName, value: teamAbbr
	Map<String, String> positionMap = new HashMap<String,String>(); // key: playerName, value: position Style
	
	private void populateGS(Long compId, List<RoundNode> retval, IRatingQuery rq, IRatingGroup g, int positionOrdinal) {


		List<IPlayerRating> prq = prf.query(rq);
		Map<String,Long> players = new HashMap<String,Long>();
		IRatingSeries rs = rsf.get(g.getRatingSeriesId());
		
		for (IPlayerRating pr : prq) {
			for (RatingComponent rc : pr.getRatingComponents()) {
				try {
					IPlayerMatchStats pms = pmsf.get(rc.getPlayerMatchStatsId());
					IMatchGroup m = mgf.get(pms.getMatchId());
					IRound r = rf.get(m.getRoundId());
					RoundNode rn = getRoundNode(compId, r.getUrOrdinal(), positionOrdinal);
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
					pm.playerId = pms.getPlayerId();
					
					getRating(pm, rs, r.getUrOrdinal(), positionOrdinal, pms.getPlayerId());
					setNotes(pm, pms);
					
					IPlayer p = pf.get(pms.getPlayerId());
					if (!players.containsKey(p.getDisplayName())) { players.put(p.getDisplayName(),p.getId()); }	
					if (!teamMap.containsKey(p.getDisplayName())) { teamMap.put(p.getDisplayName(), tf.get(pms.getTeamId()).getAbbr()); }		
					if (!positionMap.containsKey(p.getDisplayName())) { positionMap.put(p.getDisplayName(), pm.position); }	
					
					if (!rn.playerMatches.containsKey(p.getDisplayName())) {
						rn.playerMatches.put(p.getDisplayName(), new ArrayList<PlayerMatch>());
					}
					rn.playerMatches.get(p.getDisplayName()).add(pm);
				} 
				catch (Throwable ex) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "problem building roundNodes", ex);
					// just keep swimming...
				}
			}
		}
		
		for (RoundNode rn : rnMapMap.get(compId).values()) {
			fillInDidNotPlays(rn, players, g.getRatingSeries(), positionOrdinal);
			retval.add(rn);
		}
		
		Collections.sort(retval, new Comparator<RoundNode>() {
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
			PlayerMatch pm = newDidNotPlay(rn);
			getRating(pm, rs, rn.round, positionOrdinal, players.get(name));
			if (teamMap.containsKey(name)) {
				pm.teamAbbr = teamMap.get(name);
			}
			if (positionMap.containsKey(name)) {
				pm.position = positionMap.get(name);
			}
			
			if (!rn.playerMatches.containsKey(name)) {
				rn.playerMatches.put(name, new ArrayList<PlayerMatch>());
			}
			
			pm.playerId = players.get(name);
			
			rn.playerMatches.get(name).add(pm);
		}
	}

	private PlayerMatch newDidNotPlay(RoundNode rn) {

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
		for (Long rgId: ratingSeries.getRatingGroupIds()) {
			IRatingGroup rg = rgf.get(rgId);
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
		pm.notes = "";
		
		// tries
		if (pms.getTries() > 0) {
			if (pms.getTries() > 1) {
				pm.notes += pms.getTries() + " tries" + PlayerMatch.sep;
			} else {
				pm.notes += pms.getTries() + " try" + PlayerMatch.sep;
			}
			count++;
		}
		
		// try assists
		if (pms.getTryAssists() > 0) {
			if (pms.getTryAssists() > 1) {
				pm.notes += pms.getTryAssists() + " try assists" + PlayerMatch.sep;
			} else {
				pm.notes += pms.getTryAssists() + " try assist" + PlayerMatch.sep;
			}
			count++;
		}
		
		// tackles 
		if (pms.getTacklesMade() > 10) {
			pm.notes += pms.getTacklesMade() + " tackles" + PlayerMatch.sep;
			count++;
		}
		
		// tackles missed
		if (pms.getTacklesMissed() > 3) {
			pm.notes += pms.getTacklesMissed() + " tackles missed" + PlayerMatch.sep;
			count++;
		}
		
		// offloads
		if (pms.getOffloads() > 2) {
			pm.notes += pms.getOffloads() + " offloads" + PlayerMatch.sep;
			count++;
		}
		
		// defenders beaten
		if (pms.getDefendersBeaten() > 2) {
			pm.notes += pms.getDefendersBeaten() + " defenders beaten" + PlayerMatch.sep;
			count++;
		}
		
		// clean breaks
		if (pms.getCleanBreaks() > 2) {
			pm.notes += pms.getCleanBreaks() + " clean breaks" + PlayerMatch.sep;
			count++;
		}
		
		// runs
		if (pms.getRuns() > 6) {
			pm.notes += pms.getRuns() + " carries" + PlayerMatch.sep;
			count++;
		}
		
		// metres carries
		if (pms.getMetersRun() > 30) {
			pm.notes += pms.getMetersRun() + " metres gained" + PlayerMatch.sep;
			count++;
		}
		
		//lineouts
		if (pms.getLineoutsWonOnThrow() > 3) {
			pm.notes += pms.getLineoutsWonOnThrow() + " lineouts won" + PlayerMatch.sep;
			count++;
		}
		
		//lineouts
		if (pms.getLineoutsStolenOnOppThrow() > 3) {
			pm.notes += pms.getLineoutsStolenOnOppThrow() + " lineouts stolen" + PlayerMatch.sep;
			count++;
		}
		
		//penalties conceded
		if (pms.getPenaltiesConceded() > 1) {
			pm.notes += pms.getPenaltiesConceded() + " penalties conceded" + PlayerMatch.sep;
			count++;
		}
				
		//yellow card
		if (pms.getYellowCards() > 0) {
			pm.notes += pms.getYellowCards() + " yellow card" + PlayerMatch.sep;
			count++;
		}
		
		//red card
		if (pms.getRedCards() > 1) {
			pm.notes += pms.getRedCards() + " red card" + PlayerMatch.sep;
			count++;
		}
	}

	private RoundNode getRoundNode(Long compId, int urOrd, int positionOrdinal) {
		// do we already have it?
		if (!rnMapMap.containsKey(compId)) {
			rnMapMap.put(compId, new HashMap<Integer,RoundNode>());
		}
		if (rnMapMap.get(compId).containsKey(urOrd)) {
			return rnMapMap.get(compId).get(urOrd);
		} else {
			RoundNode rn = create(compId, urOrd, urf.get(urOrd).shortDesc, positionOrdinal);
			rnMapMap.get(compId).put(urOrd, rn);
			return rn;
		}
	}

//	private GraphableSeries getFromCache(Long compId, Integer positionOrdinal) {
//		try {
//			byte[] value = null;
//			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
//			GraphableSeries page = null;
//
//			value = (byte[])syncCache.get(getCacheKey(compId,positionOrdinal));
//			if (value != null) {
//				// send back the cached version
//				ByteArrayInputStream bis = new ByteArrayInputStream(value);
//				ObjectInput in;
//
//				in = new ObjectInputStream(bis);
//
//				Object obj = in.readObject();
//				if (obj instanceof GraphableSeries) {
//					page = (GraphableSeries)obj;
//				}
//
//				bis.close();
//				in.close();
//			}
//
//			return page;
//		} catch (Throwable ex) {
//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
//			return null;
//		}
//
//	}
//	
//
//	private void putToCache(String key, GraphableSeries page) {
//		try {
//			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			ObjectOutput out = new ObjectOutputStream(bos);   
//			out.writeObject(page);
//			byte[] yourBytes = bos.toByteArray(); 
//
//			out.close();
//			bos.close();
//
//			syncCache.put(key, yourBytes);
//		} catch (Throwable ex) {
//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
//
//		}
//
//	}
//	
//	private void dropFromCache(String id) {
//		try {
//			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
//
//			if (syncCache.contains(id)) {
//				syncCache.delete(id);
//			}
//		} catch (Throwable ex) {
//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
//		}	
//	}
}
