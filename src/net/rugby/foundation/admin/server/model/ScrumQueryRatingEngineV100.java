package net.rugby.foundation.admin.server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.admin.shared.IRatingEngineSchema;
import net.rugby.foundation.admin.shared.IV1EngineWeightValues;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema20130713;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.Position.position;

public class ScrumQueryRatingEngineV100 implements
IQueryRatingEngine  {

	private Map<Long,ITeamMatchStats> tmsHomeMap = new HashMap<Long,ITeamMatchStats>();
	private Map<Long,ITeamMatchStats> tmsVisitMap = new HashMap<Long,ITeamMatchStats>();
	private List<IPlayerMatchStats> pmsList = new ArrayList<IPlayerMatchStats>();
	private Map<Long,Float> standingsFactorMap = new HashMap<Long,Float>();
	
	private ITeamMatchStatsFactory tmsf;
	private IMatchGroupFactory mf;
	private IPlayerFactory pf;
	private IPlayerMatchRatingFactory pmrf;
	private ArrayList<IRatingEngineSchema> supportedSchemas;
	private IStandingFactory sf;
	private IRoundFactory rf;

	public ScrumQueryRatingEngineV100() {

	}

	public ScrumQueryRatingEngineV100(IPlayerFactory pf, IMatchGroupFactory mf, IPlayerMatchRatingFactory pmrf, ITeamMatchStatsFactory tmsf, IRoundFactory rf, IStandingFactory sf) {
		supportedSchemas = new ArrayList<IRatingEngineSchema>();
		supportedSchemas.add(new ScrumMatchRatingEngineSchema20130713());
		this.pf = pf;
		this.pmrf = pmrf;
		this.tmsf = tmsf;
		this.mf = mf;
		this.rf = rf;
		this.sf = sf;
	}


	// this is odd... it's different than the number of people who get on the field during the game (numPlayers below)
	// need to think more about what this means. 
	// The danger we want to avoid is a team that uses subs "diluting" its players' rankings.
	//
	//private final int playersOnField = 30;

	private List<IPlayerRating> mrl;

	protected class PlayerStatShares {

		private IPlayerMatchStats pms;

		private float tries;
		private float tryAssists;
		private float points;
		private float kicks;
		private float passes;
		private float runs;
		private float metersRun;
		private float cleanBreaks;
		private float defendersBeaten;
		private float offloads;
		private float turnovers;
		private float tacklesMade;
		private float tacklesMissed;
		private float lineoutsWonOnThrow;
		private float lineoutsStolenOnOppThrow;
		private float penaltiesConceded;
		private float yellowCards;
		private float redCards;

		// time-skewed team stats
		private float scrumShare;
		private float lineoutShare;
		private float ruckShare;
		private float maulShare;
		private float scrumLost;
		private float lineoutLost;
		private float ruckLost;
		private float maulLost;
		private float scrumStolen;
		private float lineoutStolen;
		private float ruckStolen;
		private float maulStolen;
		private float minutesShare;
		private float win;

		private float pointDifferential;

		private float backScore;
		private float forwardScore;

		private int numPlayers;

		private float playerScore = 0F;

		private IV1EngineWeightValues weights;

		public PlayerStatShares(IV1EngineWeightValues weights, ITeamMatchStats tms, ITeamMatchStats otms, /*ITeamMatchStats combined,*/ IPlayerMatchStats pms, IMatchGroup match, /*int totalTryAssists, int totalLineoutsStolen, int totalMinutes,*/ int numPlayers) {

			assert(tms.getTeamId().equals(pms.getTeamId()));

			Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINEST);

			this.pms = pms;
			this.weights = weights;
			tries = (float)pms.getTries();
			tryAssists= (float) pms.getTryAssists() ;	
			points= (float) pms.getPoints();	
			kicks=(float) pms.getKicks() ;		
			passes = (float) pms.getPasses() ;		
			runs = (float) pms.getRuns();	
			metersRun = (float) pms.getMetersRun();	
			cleanBreaks = (float) pms.getCleanBreaks();	
			defendersBeaten = (float) pms.getDefendersBeaten();
			offloads = (float) pms.getOffloads();	
			turnovers = (float) pms.getTurnovers();		
			tacklesMade = (float) pms.getTacklesMade();
			tacklesMissed = (float) pms.getTacklesMissed();			
			lineoutsWonOnThrow = (float) pms.getLineoutsWonOnThrow();	
			lineoutsStolenOnOppThrow = (float) pms.getLineoutsStolenOnOppThrow();	
			penaltiesConceded = (float) pms.getPenaltiesConceded();	
			yellowCards = (float) pms.getYellowCards();
			redCards = (float) pms.getRedCards() ;
			minutesShare = (float) pms.getTimePlayed();

			// Our team stats
			scrumShare = (float) tms.getScrumsWonOnOwnPut();
			lineoutShare = (float) tms.getLineoutsWonOnOwnThrow();
			ruckShare =  (float) tms.getRucksWon() ;
			maulShare = (float) tms.getMaulsWon();		
			scrumLost = (float) (tms.getScrumsPutIn()-tms.getScrumsWonOnOwnPut());
			lineoutLost = (float) (tms.getLineoutsThrownIn() - tms.getLineoutsWonOnOwnThrow());
			ruckLost = (float) (tms.getRucks() - tms.getRucksWon());
			maulLost = (float) (tms.getMauls() - tms.getMaulsWon());

			// Opposition team stats - if the opposition loses something on their "put", we add it to our win
			scrumStolen =  (float) (otms.getScrumsPutIn()-otms.getScrumsWonOnOwnPut());
			lineoutStolen = (float) (otms.getLineoutsThrownIn() - otms.getLineoutsWonOnOwnThrow());
			ruckStolen = (float) (otms.getRucks() - otms.getRucksWon());
			maulStolen = (float) (otms.getMauls() - otms.getMaulsWon());

			pms = adjustStatsByPosition(pms);

			this.numPlayers = numPlayers;

			CalculatePointDifferential(match);

			Normalize();
		}

		private IPlayerMatchStats adjustStatsByPosition(IPlayerMatchStats pms2) {
			if (pms2.getPosition().equals(position.FLYHALF)) {

			} else if (pms2.getPosition().equals(position.FLYHALF) || pms2.getPosition().equals(position.CENTER)) {
				metersRun *= 2;
			}  else if (pms2.getPosition().equals(position.SCRUMHALF)) {

			} else if (pms2.getPosition().equals(position.FULLBACK)) {

			} else if (pms2.getPosition().equals(position.WING)) {

			} else if (pms2.getPosition().equals(position.PROP)) {
				scrumShare = (scrumShare*1.5F);

			}  else if (pms2.getPosition().equals(position.HOOKER)) {
				scrumShare = (scrumShare*1.3F);

			} else if (pms2.getPosition().equals(position.LOCK)) {
				lineoutStolen = 0; // no double dip
			} else if (pms2.getPosition().equals(position.FLANKER) || pms2.getPosition().equals(position.NUMBER8)) {
				scrumShare = (scrumShare*.5F);
				lineoutShare = (lineoutShare*.3f);

			}

			return pms2;

		}

		private void CalculatePointDifferential(IMatchGroup match) {
			try {
				pointDifferential = (match.getSimpleScoreMatchResult().getHomeScore() - match.getSimpleScoreMatchResult().getVisitScore());

				if (!pms.getTeamId().equals(match.getHomeTeamId())) {
					pointDifferential *= -1;
				}

				if (pointDifferential > 1) {
					win = 1;
				} else {
					win = 0;
				}
			} catch (Throwable e) {
				String message = "Could not calculate points differential for match. We need to have a valid match result before being able to calculate ratings!";
				if (match != null) {
					message += " Match is: " + match.getDisplayName();
				} 

				message += " Player is: " + pms.getName() + " in match " + pms.getMatchId();
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, message, e);
				pointDifferential = 0;
			}
		}

		private void Normalize() {
			tries *= weights.getTriesWeight();                   
			tryAssists *= weights.getTryAssistsWeight();              
			points *= weights.getPointsWeight();                  
			kicks *= weights.getKicksWeight();                   
			passes *= weights.getPassesWeight();                  
			runs *= weights.getRunsWeight();                    
			metersRun *= weights.getMetersRunWeight();               
			cleanBreaks *= weights.getCleanBreaksWeight();             
			defendersBeaten *= weights.getDefendersBeatenWeight();         
			offloads *= weights.getOffloadsWeight();                
			turnovers *= weights.getTurnoversWeight();               
			tacklesMade *= weights.getTacklesMadeWeight();             
			tacklesMissed *= weights.getTacklesMissedWeight();           
			lineoutsWonOnThrow *= weights.getLineoutsWonOnThrowWeight();      
			lineoutsStolenOnOppThrow *= weights.getLineoutsStolenOnOppThrowWeight();
			penaltiesConceded *= weights.getPenaltiesConcededWeight();       
			yellowCards *= weights.getYellowCardsWeight();             
			redCards *= weights.getRedCardsWeight();                

			scrumShare *= weights.getScrumShareWeight();              
			lineoutShare *= weights.getLineoutShareWeight();            
			ruckShare *= weights.getRuckShareWeight();               
			maulShare *= weights.getMaulShareWeight();               
			minutesShare *= weights.getMinutesShareWeight();  

			scrumLost *= weights.getScrumLostWeight();              
			lineoutLost *= weights.getLineoutLostWeight();            
			ruckLost *= weights.getRuckLostWeight();               
			maulLost *= weights.getMaulLostWeight();     
			
			scrumStolen *= weights.getScrumStolenWeight();              
			lineoutStolen *= weights.getLineoutStolenWeight();            
			ruckStolen *= weights.getRuckStolenWeight();               
			maulStolen *= weights.getMaulStolenWeight();  

			pointDifferential = pointDifferential * weights.getPointsDifferentialWeight();
			win = win * weights.getWin();

			//Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST, pms.getName() + toString());

		}

		@Override
		public String toString() {
			return " T:"+tries+" TA:"+tryAssists+" Pts:"+points+" K:"+kicks+" R:"+runs+" P:"+passes+" MR:"+metersRun+
					cleanBreaks+" DB:"+defendersBeaten+" OL:"+offloads+" TO:"+turnovers+" T:"+tacklesMade+" TM:"+tacklesMissed+"\n LO:"+lineoutsWonOnThrow+
					" LOStolen:"+lineoutsStolenOnOppThrow+"\n P:"+penaltiesConceded+" YC:"+yellowCards+" RC: "+redCards+"\n ScrumShare:"+scrumShare+
					" LOShare:"+lineoutShare+" RuckShare:"+ruckShare+" MSh:"+ maulShare+"\n ScrumLost:"+scrumLost+
					" LOLost:"+lineoutLost+" RuckLost:"+ruckLost+" MLost:"+ maulLost+"\n ScrumStolen:"+scrumStolen+
					" LOStolen:"+lineoutStolen+" RuckStolen:"+ruckStolen+" MStolen:"+maulStolen+"\n MinShare:"+ minutesShare+" PtDiff:"+ pointDifferential+" Win:" + win + "\n Back Score:" + getBackScore() + "\n Forward Score:" + getForwardScore();
		}

		private float getBackScore() {
			backScore =  (tries + tryAssists + points + kicks + passes + runs + metersRun + cleanBreaks + 
					defendersBeaten + offloads + turnovers + tacklesMade + tacklesMissed + (.3f * ruckShare) + (.3f * ruckLost) *penaltiesConceded +
					yellowCards + redCards + minutesShare + pointDifferential + win); 
			return backScore;
		}

		private float getForwardScore() {
			forwardScore = (lineoutsWonOnThrow + lineoutsStolenOnOppThrow + scrumShare + scrumLost + lineoutShare + lineoutLost + (.7f * ruckShare) + (.7f * ruckLost) + maulShare + maulLost + scrumStolen + lineoutStolen + ruckStolen + maulStolen); 
			return forwardScore;
		}

		private int isForward() {
			if (	pms.getPosition().equals(position.PROP) ||
					pms.getPosition().equals(position.HOOKER) ||
					pms.getPosition().equals(position.LOCK) ||
					pms.getPosition().equals(position.FLANKER) ||
					pms.getPosition().equals(position.NUMBER8) ) {
				return 1;
			} else {
				return 0;
			}
		}

		public float getPlayerScore() {
			if (playerScore  == 0F) {
				playerScore = getBackScore() + (isForward() * getForwardScore()); //)/(1+isForward());
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE, "player score for: " + pms.getName() + " is " + playerScore + " (forward score: " + forwardScore + ", back score: " + backScore + ")" );
			}
			return playerScore;
		}

		public IPlayerMatchStats getPlayerMatchStats() {
			return pms;
		}

		public Integer getRating(float totalScores) {
			Float normalizedSmoothScore = (float) (playerScore/totalScores);
			return Math.round(normalizedSmoothScore * 500 * numPlayers);  // we use numPlayers and not playersOnField here so they average out to 500.
		}

		public void scaleForStandings(Float matchStandingFactor) {
			playerScore *= matchStandingFactor;
			
		}

	}

	@Override
	public List<IPlayerRating> generate(IRatingEngineSchema schema) {

		String playerName = "nobody yet";
		String matchName = "no match yet";
		try {
		// Have to have these weight values available in the schema to work
		assert (schema instanceof IV1EngineWeightValues);

		List<PlayerStatShares> pss = new ArrayList<PlayerStatShares>();
		mrl = new ArrayList<IPlayerRating>();

		float totalPlayerScore = 0F;

		for (IPlayerMatchStats pms : pmsList) {
			playerName = pms.getName();
			IMatchGroup m = mf.get(pms.getMatchId());
			matchName = m.getDisplayName();
			if (!standingsFactorMap.containsKey(pms.getMatchId())) {
				getStandingFactorForMatch(pms.getMatchId());

			}
			if (!pms.getPosition().equals(position.NONE)) {
				ITeamMatchStats tms = null;
				ITeamMatchStats otms = null;
				if (pms.getTeamId().equals(m.getHomeTeamId())) {
					tms = tmsHomeMap.get(m.getId());
					otms =  tmsVisitMap.get(m.getId());
				} else {
					tms = tmsVisitMap.get(m.getId());
					otms = tmsHomeMap.get(m.getId());
				}
				PlayerStatShares score = new PlayerStatShares((IV1EngineWeightValues)schema, tms, otms, pms, m, pmsList.size());
				// scale the rating by the match's standingsFactor
				score.scaleForStandings(standingsFactorMap.get(score.getPlayerMatchStats().getMatchId()));
				pss.add(score);
				totalPlayerScore += score.getPlayerScore();
			} 
		}

		for (PlayerStatShares score : pss) {
			IPlayerMatchRating pmr = pmrf.getNew(pf.get(score.getPlayerMatchStats().getPlayerId()), null, score.getRating(totalPlayerScore), schema, score.getPlayerMatchStats(), score.toString());
			pmrf.put(pmr);
			mrl.add(pmr);
		}

		sendReport();

		return mrl;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Engine threw a rod on player " + playerName + " from match " + matchName, e);
			return null;
		}
	}

	private void getStandingFactorForMatch(Long matchId) {
		rf.setId(mf.get(matchId).getRoundId());
		List<IStanding> list = sf.getForRound(rf.getRound());
		IMatchGroup m = mf.get(matchId);
		int sTot = 0;
		int count = 0;
		boolean found = false;
		for (IStanding s : list) {
			if (s.getTeamId().equals(m.getHomeTeamId()) || s.getTeamId().equals(m.getVisitingTeamId())) {
				sTot += s.getStanding();
				count ++;
				if (count == 2) {
					found = true;
					break;
				}
			}
		}
		
		Float standingsFactor = 1F;
		if (found) {
			standingsFactor =  1F / (float)(Math.sqrt(sTot-2F)) + .43F;
		}
		
		standingsFactorMap.put(matchId, standingsFactor);
		
	}

	@Override
	public void addTeamStats(List<ITeamMatchStats> teamStats) {
		for (ITeamMatchStats ts : teamStats) {
			if (ts.getIsHome() == null) {
				// have to figure out for the ones that were created before 9/12/2013
				IMatchGroup m = mf.get(ts.getMatchId());
				ts.setIsHome(ts.getTeamId().equals(m.getHomeTeamId()));
			}

			if (ts.getIsHome()) {
				tmsHomeMap.put(ts.getMatchId(), ts);
			} else {
				tmsVisitMap.put(ts.getMatchId(), ts);
			}
		}

	}

	@Override
	public void addPlayerStats(List<IPlayerMatchStats> playerStats) {
		for (IPlayerMatchStats pms : playerStats) {
			if (pms.getPosition() != position.NONE) {
				pmsList.add(pms);
			}
		}
	}


	@Override
	public StatisticalSummary getStatisticalSummary() {
		StatisticalSummary ss = new SummaryStatistics();
		for (IPlayerRating r : mrl) {
			((SummaryStatistics)ss).addValue(r.getRating());
		}
		return ss;
	}

	@Override
	public List<IRatingEngineSchema> getSupportedSchemas() {
		return supportedSchemas;
	}

	@Override
	public String toString() {
		return "[ " + this.getClass().getCanonicalName() + " PMS count: " + pmsList.size() + "TMS count: " + (tmsHomeMap.size() + tmsVisitMap.size()) + " ]";
	}

	private void sendReport() {
		AdminEmailer emailer = new AdminEmailer();
		String sep = " =================== ";
		emailer.setSubject("Rating Engine Report");

		String body = " PlayerMatchStats count: " + pmsList.size() + " TMS count: " + (tmsHomeMap.size() + tmsVisitMap.size()) + "\n";
		body += sep + "Overall" + sep + "\n" + sep + sep + "\n";

		StatisticalSummary ss = new SummaryStatistics();
		for (IPlayerRating r : mrl) {
			((SummaryStatistics)ss).addValue(r.getRating());
		}

		body += ss.toString();

		// backs vs. forwards
		body += sep + "Backs vs. Forwards" + sep + "\n" + sep + sep + "\n";
		StatisticalSummary forwards  = new SummaryStatistics();
		StatisticalSummary backs = new SummaryStatistics();
		for (IPlayerRating r : mrl) {
			if (r.getMatchStats().get(0).isForward()>0) {
				((SummaryStatistics)forwards).addValue(r.getRating());
			}	else {
				((SummaryStatistics)backs).addValue(r.getRating());
			}	
		}
		body += sep + "Backs" + "\n";
		body += backs.toString();

		body += sep + "Forwards" + "\n";
		body += forwards.toString();

		body += sep + "Positions" + sep + "\n" + sep + sep + "\n";

		for (int i=1; i<position.values().length-1; ++i) {
			body += sep + position.getAt(i) + "\n";
			ss = new SummaryStatistics();
			for (IPlayerRating r : mrl) {
				if (r instanceof IPlayerMatchRating) {
					if (((IPlayerMatchRating)r).getPlayerMatchStats().getPosition().equals(position.getAt(i))) {
						((SummaryStatistics)ss).addValue(r.getRating());
					}
				}
			}
			body += ss.toString();
		}

		body += sep + "Matches" + sep + "\n" + sep + sep + "\n";
		Iterator<ITeamMatchStats> it = tmsHomeMap.values().iterator();
		while (it.hasNext()) {
			Long id = it.next().getMatchId();
			IMatchGroup m = mf.get(id);
			body += sep + m.getDisplayName() + "\n";
			ss = new SummaryStatistics();
			for (IPlayerRating r : mrl) {
				if (r instanceof IPlayerMatchRating) {
					if (((IPlayerMatchRating)r).getPlayerMatchStats().getMatchId().equals(id)) {
						((SummaryStatistics)ss).addValue(r.getRating());
					}
				}	
			}
			body += ss.toString();
		}
		emailer.setMessage(body);
		emailer.send();
	}
}