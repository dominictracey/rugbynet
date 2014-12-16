package net.rugby.foundation.admin.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.admin.shared.IV1EngineWeightValues;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRawScoreFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRawScore;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema20130713;
import net.rugby.foundation.model.shared.SimpleScoreMatchResult;
import net.rugby.foundation.model.shared.IRatingQuery.Status;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.PlayerRating;
import net.rugby.foundation.model.shared.PlayerRating.RatingComponent;
import net.rugby.foundation.model.shared.Position.position;

public class ScrumQueryRatingEngineV100 implements IQueryRatingEngine  {

	protected Map<Long,ITeamMatchStats> tmsHomeMap = new HashMap<Long,ITeamMatchStats>();
	protected Map<Long,ITeamMatchStats> tmsVisitMap = new HashMap<Long,ITeamMatchStats>();
	protected List<IPlayerMatchStats> pmsList = new ArrayList<IPlayerMatchStats>();
	// Key: matchId; Value: scaling factor (0-1) based on the round standings for the home and visiting teams in the match
	protected Map<Long,Float> standingsFactorMap = new HashMap<Long,Float>();
	protected Map<Long,Float> matchCompWeights = new HashMap<Long,Float>(); // maps a matchId to a competition weightingFactor
	// Key: playerId; Value: List of PlayerStatShares for that player in the current query context
	Map<Long, List<IPlayerStatShares>> playerScoreMap = new HashMap<Long, List<IPlayerStatShares>>();
	// Key: matchId; Value: match label
	protected final Map<Long, String> matchLabelMap = new HashMap<Long, String>();
	// Key: matchId; Value: scaling factor for that match based on how long ago it occurred
	protected final Map<Long, Float> matchTimeScaleMap = new HashMap<Long, Float>();
	// Given the current linear aging of match results, the number of days before a match becomes worthless (i.e. scaling of 0)
	protected final int maxAge = 365;

	protected IMatchGroupFactory mf;
	protected IPlayerFactory pf;
	protected IPlayerRatingFactory prf;
	protected ArrayList<IRatingEngineSchema> supportedSchemas;
	protected IStandingFactory sf;
	protected IRoundFactory rf;
	protected IPlayerMatchStatsFactory pmsf;
	protected ITeamMatchStatsFactory tmsf;
	protected IRatingQueryFactory rqf;
	protected ICompetitionFactory cf;
	protected IMatchResultFactory mrf;

	public List<IPlayerRating> mrl;
	public IRatingQuery query;

	// the sum of all of the stats*schemaValues
	protected float totalRawScore;
	//	private float totalUnscaledScore;
	// the sum of all the stats*schemaValues*scalingFactor
	private float totalScaledScore;

	// these are the green items in column D of the simulator
	// flat number of PlayerMatchStats (D7)
	protected int numStats = 0;
	// sum of the standing scaling factors used for the PMSs. 
	private float numStatsStandingsScaled;
	// sum of the comp scaling factors used for the PMSs. (D13)
	private float numStatsCompScaled;
	// sum of the time scaling factors used for the PMSs. (D10)
	private float numStatsTimeScaled;
	// sum of the all scaling factors used for the PMSs. (D32)
	private float numStatsTotalScaled;
	// and we keep them in here to make an attempt at scalability
	protected Map<String,Float> scaleTotalNumMap = new HashMap<String,Float>();

	//this is the totals in (yellow) column F of the simulator
	protected Map<String, Float> scaleTotalMap = new HashMap<String,Float>();

	protected final String COMP_SCALE_KEY="Competition";
	protected final String AGE_SCALE_KEY="MatchAge";
	protected final String STANDINGS_SCALE_KEY="Standings";
	protected final String NO_SCALE_KEY="Unscaled";
	protected final String ALL_SCALE_KEY="ActualScaled";
	private IRawScoreFactory rsf;

	public ScrumQueryRatingEngineV100() {

	}

	public ScrumQueryRatingEngineV100(IPlayerFactory pf, IMatchGroupFactory mf, IPlayerRatingFactory prf, IRoundFactory rf, IStandingFactory sf, 
			IPlayerMatchStatsFactory pmsf, ITeamMatchStatsFactory tmsf, IRatingQueryFactory rqf, ICompetitionFactory cf, IMatchResultFactory mrf,
			IRawScoreFactory rsf) {
		supportedSchemas = new ArrayList<IRatingEngineSchema>();
		supportedSchemas.add(new ScrumMatchRatingEngineSchema20130713());
		this.pf = pf;
		this.prf = prf;
		this.mf = mf;
		this.rf = rf;
		this.sf = sf;
		this.pmsf = pmsf;
		this.tmsf = tmsf;
		this.rqf = rqf;
		this.cf = cf;
		this.mrf = mrf;
		this.rsf = rsf;

		scaleTotalMap.put(NO_SCALE_KEY, 0f);
		scaleTotalMap.put(ALL_SCALE_KEY, 0f);
		scaleTotalMap.put(AGE_SCALE_KEY, 0f);
		scaleTotalMap.put(COMP_SCALE_KEY, 0f);
		scaleTotalMap.put(STANDINGS_SCALE_KEY, 0f);
	}






	protected class PlayerStatShares implements IPlayerStatShares {

		protected IPlayerMatchStats pms;

		// player stats
		protected float tries;
		protected float tryAssists;
		protected float points;
		protected float kicks;
		protected float passes;
		protected float runs;
		protected float metersRun;
		protected float cleanBreaks;
		protected float defendersBeaten;
		protected float offloads;
		protected float turnovers;
		protected float tacklesMade;
		protected float tacklesMissed;
		protected float lineoutsWonOnThrow;
		protected float lineoutsStolenOnOppThrow;
		protected float penaltiesConceded;
		protected float yellowCards;
		protected float redCards;

		// time-skewed team stats
		protected float scrumShare;
		protected float lineoutShare;
		protected float ruckShare;
		protected float maulShare;
		protected float scrumLost;
		protected float lineoutLost;
		protected float ruckLost;
		protected float maulLost;
		protected float scrumStolen;
		protected float lineoutStolen;
		protected float ruckStolen;
		protected float maulStolen;

		// game stats
		protected float minutesShare;
		protected float win;
		protected float pointDifferential;

		// stat weighting values from schema
		protected IV1EngineWeightValues weights;

		// these are the raw scores from multiplying the stats by the schema weighting factors
		protected float backScore;
		protected float forwardScore;
		protected float playerScore = 0F;

		// the percentage that this record's raw score is of the total of all raw scores
		protected float unscaledPercent;

		// Accumulation of adjustments for a variety of factors introduced through scale()
		protected float scalingFactor = 1f;

		// the raw score adjusted for global scaling factors such as standings and competitions
		protected float scaledScore;
		// the percentage that this record's raw score is of the total of all scaled scores
		protected float scaledPercent;

		// the number of carbon-based life forms participating - this should only be used in the engine; not needed here.
		//protected int numPlayers;

		// the number of PlayerMatchStats involved (players may have multiple)
		//protected int numStats;

		// unscaledPercent * numStats * 500;
		protected Integer unscaledRating;
		// scaledPercent * numStats * 500;
		protected Integer scaledRating;

		// scaling narrative for inspection/debugging
		protected String scalingNarrative = "Scaling Factor\tScaling Value\tScaled Score" + /*\tScaled Rating*/ "\n-----------------------------------------\n"; 
		protected IMatchGroup match;

		protected Map<String, Float> scalingFactorMap = new HashMap<String, Float>();

		private ITeamMatchStats tms;

		private ITeamMatchStats otms;

		private String matchLabel;
		private String details;

		public PlayerStatShares(IV1EngineWeightValues weights, ITeamMatchStats tms, ITeamMatchStats otms, IPlayerMatchStats pms, IMatchGroup match, int numStats) {

			assert(tms.getTeamId().equals(pms.getTeamId()));

			Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINEST);

			this.pms = pms;
			this.tms = tms;
			this.otms = otms;
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
			pms = adjustStatsByTimePlayed(pms);

			//this.numStats = numStats;
			this.match = match;

			CalculatePointDifferential(match);

			Normalize();
			scalingFactorMap.put(NO_SCALE_KEY, 1f);
		}

		public PlayerStatShares(IV1EngineWeightValues schema, IRawScore raw,
				IPlayerMatchStats pms, IMatchGroup m, int size) {
			details = raw.getDetails();
			playerScore = raw.getRawScore();
			match = m;
			this.pms = pms;
			scalingFactorMap.put(NO_SCALE_KEY, 1f);
		}

		private IPlayerMatchStats adjustStatsByTimePlayed(IPlayerMatchStats pms) {
			float timeScale = pms.getTimePlayed()/80f;

			scrumShare *= timeScale;
			lineoutShare *= timeScale;
			ruckShare *= timeScale;
			maulShare *= timeScale;		
			scrumLost *= timeScale;
			lineoutLost *= timeScale;
			ruckLost *= timeScale;
			maulLost *= timeScale;

			// Opposition team stats - if the opposition loses something on their "put", we add it to our win
			scrumStolen *= timeScale;
			lineoutStolen *= timeScale;
			ruckStolen *= timeScale;
			maulStolen *= timeScale;
			return pms;
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

			details = pms.getName() + "\tTeam: " + pms.getTeamAbbr() +"\n"+
					"Tr: "+String.format("%.2f",tries)+" (" +pms.getTries() + ")\tTA: "+String.format("%.2f",tryAssists)+"\t("+pms.getTryAssists()+")\tPts: "+String.format("%.2f",points)+" ("+ pms.getPoints()+")\n"+
					"K: "+kicks+" ("+pms.getKicks()+")\tR: "+runs+" ("+pms.getRuns()+")\tP: "+passes+" ("+pms.getPasses()+")\n"+
					"MR: "+metersRun+" ("+pms.getMetersRun()+")\tCB: "+cleanBreaks+" ("+pms.getCleanBreaks()+")\tDB: "+defendersBeaten+" ("+pms.getDefendersBeaten()+")\n"+
					"OL: "+offloads+" ("+pms.getOffloads()+")\tTO: "+turnovers+" ("+pms.getTurnovers()+")\tT: "+tacklesMade+" ("+pms.getTacklesMade()+")\tTM: "+tacklesMissed+" ("+pms.getTacklesMissed()+")\n"+
					"LO:"+lineoutsWonOnThrow+" ("+pms.getLineoutsWonOnThrow()+")\tLOS: "+lineoutsStolenOnOppThrow+" ("+pms.getLineoutsStolenOnOppThrow()+")\n"+
					"P: "+penaltiesConceded+" ("+pms.getPenaltiesConceded()+")\tYC: "+yellowCards+" ("+pms.getYellowCards()+")\tRC: "+redCards+" ("+pms.getRedCards()+")\n"+
					"ScW: "+scrumShare+" ("+tms.getScrumsWonOnOwnPut()+")\tScL:"+scrumLost+" ("+(tms.getScrumsPutIn()-tms.getScrumsWonOnOwnPut())+")\tScrS:"+scrumStolen+"("+(otms.getScrumsPutIn()-otms.getScrumsWonOnOwnPut())+")\n"+
					"LOW: "+lineoutShare+" ("+tms.getLineoutsWonOnOwnThrow()+")\tLOL: "+lineoutLost+" ("+(tms.getLineoutsThrownIn() - tms.getLineoutsWonOnOwnThrow())+")\tLOS: "+lineoutStolen+" ("+(otms.getLineoutsThrownIn() - otms.getLineoutsWonOnOwnThrow())+")\n"+
					"RuW: "+ruckShare+" ("+tms.getRucksWon()+")\tRuL: "+ruckLost+" ("+(tms.getRucks()-tms.getRucksWon())+")\tRuS: "+ruckStolen+" ("+(otms.getRucks() - otms.getRucksWon())+")\n"+
					"MaW: "+ maulShare+" ("+tms.getMaulsWon()+")\tMaL: "+ maulLost+" ("+(tms.getMauls()-tms.getMaulsWon())+")\tMaS: "+maulStolen+" ("+(otms.getMauls() - otms.getMaulsWon())+")\n"+ 
					"Min: "+ minutesShare+" ("+pms.getTimePlayed()+")\tPtDiff: "+ pointDifferential+" ("+(pointDifferential/weights.getPointsDifferentialWeight())+")\tWin: " + win + "\n"+
					"Back: " + getBackScore() + "\tFwd:" + getForwardScore();
			//Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST, pms.getName() + toString());

		}

		/* (non-Javadoc)
		 * @see net.rugby.foundation.admin.server.model.IPlayerStatShares#toString()
		 */
		@Override
		public String toString() {
			return details;
		}

		private float getBackScore() {
			backScore =  (tries + tryAssists + points + kicks + passes + runs + metersRun + cleanBreaks + 
					defendersBeaten + offloads + turnovers + tacklesMade + tacklesMissed + (.3f * ruckShare) + (.3f * ruckLost) + penaltiesConceded +
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

		/* (non-Javadoc)
		 * @see net.rugby.foundation.admin.server.model.IPlayerStatShares#getPlayerScore()
		 */
		@Override
		public float getUnscaledScore() {
			if (playerScore  == 0F) {
				playerScore = getBackScore() + (isForward() * getForwardScore()); //)/(1+isForward());
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE, "player score for: " + pms.getName() + " is " + playerScore + " (forward score: " + forwardScore + ", back score: " + backScore + ")" );
			}
			return playerScore;
		}

		/* (non-Javadoc)
		 * @see net.rugby.foundation.admin.server.model.IPlayerStatShares#getPlayerMatchStats()
		 */
		@Override
		public IPlayerMatchStats getPlayerMatchStats() {
			return pms;
		}

		@Override
		public IMatchGroup getMatch() {
			return match;
		}

		/* (non-Javadoc)
		 * @see net.rugby.foundation.admin.server.model.IPlayerStatShares#scaleForStandings(java.lang.Float)
		 */
		@Override
		public float scale(Float factor, String factorName) {
			scalingFactorMap.put(factorName,factor);
			scalingFactor *= factor;
			scalingFactorMap.put(ALL_SCALE_KEY, scalingFactor);
			scalingNarrative += factorName + "\t" + factor + "\t" + playerScore*factor + /*"\t" + playerScore*factor*500*numStats +*/ "\n";	
			return playerScore * factor;
		}

		// Cols H-J in simulator
		@Override
		public float getUnscaledPercentage(float total) {
			return getUnscaledScore() / total;
		}

		// Simulator H26:J28 (pass in totalScaledScore
		@Override
		public float getScaledPercentage(float total) {
			return getUnscaledScore() * scalingFactor/ total;
		}

		/* (non-Javadoc)
		 * @see net.rugby.foundation.admin.server.model.IPlayerStatShares#getUnscaledRating(float)
		 */
		@Override
		public Integer getUnscaledRating(float totalScores, float numStats) {
			unscaledPercent = (float) (playerScore/totalScores);
			unscaledRating = Math.round(unscaledPercent * 500 * numStats);  // we use numStats and not playersOnField here so they average out to 500.
			return unscaledRating;
		}

		@Override
		public Integer getScaledRating(float totalScores, float numStats) {
			scaledPercent = (float) (playerScore*scalingFactor/totalScores);
			scaledRating = Math.round(scaledPercent * 500 * numStats);  // we use numStats and not playersOnField here so they average out to 500.
			return scaledRating;
		}

		// Simulator E26:G28
		@Override
		public float getScaledScore() {
			return getUnscaledScore() * scalingFactor;
		}

		@Override
		public Float getScalingFactor(String type) {
			if (scalingFactorMap.containsKey(type)) {
				return scalingFactorMap.get(type);
			} else {
				return null;
			}
		}

		@Override
		public void setMatchLabel(String matchLabel) {
			this.matchLabel = matchLabel;
		}

		@Override
		public RatingComponent getRatingComponent(Map<String, Float> scaleTotalNumMap, Map<String, Float> scaleTotalMap) {
			//			unscaledRating = super.getRating(totalScores);
			//			scaledRating = getRating(totalScores);
			//pms.getName() + "(" + pms.getTeamAbbr() + ")\n" + toString()+"\n" + scalingNarrative + "; numRecords: " + numStats + 
			//" raw score: " + playerScore + " total raw: " + totalScores + "\n", 
			//unscaledRating, scaledRating, backScore, forwardScore, getUnscaledScore(), pms.getId(), getMatchLabel(pms)

			//				computedRating = super.getRating(scaleTotalMap);
			//				scaledRating = getRating(scaleTotalMap);

			//String statsDetails, float backScore, float forwardScore, float rawScore, Long playerMatchStatsId, String matchLabel
			Integer scaledRating = Math.round(playerScore*scalingFactorMap.get(ALL_SCALE_KEY)/scaleTotalMap.get(ALL_SCALE_KEY)*scaleTotalNumMap.get(ALL_SCALE_KEY)*500);
			Integer unscaledRating = Math.round(playerScore*scalingFactorMap.get(NO_SCALE_KEY)/scaleTotalMap.get(NO_SCALE_KEY)*scaleTotalNumMap.get(NO_SCALE_KEY)*500);
			RatingComponent rc = new RatingComponent(toString(),backScore,forwardScore,playerScore,pms.getId(),matchLabel, scaledRating, unscaledRating);

			rc.addRatingsDetails("Scaling\tValue\tScore\tRating\n----------------------------------------------------\n");
			for (String key: scalingFactorMap.keySet()) {
				rc.addRatingsDetails(key+"\t"+String.format("%.2f",scalingFactorMap.get(key))+"\t"+String.format("%.2f",playerScore*scalingFactorMap.get(key))+"\t"+Math.round(playerScore*scalingFactorMap.get(key)/scaleTotalMap.get(key)*scaleTotalNumMap.get(key)*500)+"\n");
			}
			return rc;
			//return new RatingComponent(pms.getName() + "(" + pms.getTeamAbbr() + ")\n" + toString()+"\nTime scale factor: " + timescale + "; numRecords: " + numPlayers + " raw score: " + playerScore + " total raw: " + scaleTotalMap + "\n", computedRating, scaledRating, backScore, forwardScore, getPlayerScore(), pms.getId(), getMatchLabel(pms));




		}

		@Override
		public String getSummaryRow(Map<String, Float> scaleTotalNumMap, Map<String, Float> scaleTotalMap) {
			// "\nMatch\tScore\tUnscaled\tScaled\tMatch Aged\tStandings\tCompetition\n
			String summary = matchLabel;
			summary  += "\t" + String.format("%.2f",playerScore);
			if (scalingFactorMap.containsKey(NO_SCALE_KEY)) {
				summary  += "\t" + String.format("%.2f",playerScore*scalingFactorMap.get(NO_SCALE_KEY)/scaleTotalMap.get(NO_SCALE_KEY)*scaleTotalNumMap.get(NO_SCALE_KEY)*500);
			} else {
				summary  += "\t --";
			}
			if (scalingFactorMap.containsKey(ALL_SCALE_KEY)) {
				summary  += "\t" + String.format("%.2f",playerScore*scalingFactorMap.get(ALL_SCALE_KEY)/scaleTotalMap.get(ALL_SCALE_KEY)*scaleTotalNumMap.get(ALL_SCALE_KEY)*500);
			} else {
				summary  += "\t --";
			}
			if (scalingFactorMap.containsKey(AGE_SCALE_KEY)) {
				summary  += "\t" + String.format("%.2f",playerScore*scalingFactorMap.get(AGE_SCALE_KEY)/scaleTotalMap.get(AGE_SCALE_KEY)*scaleTotalNumMap.get(AGE_SCALE_KEY)*500);
			} else {
				summary  += "\t --";
			}
			if (scalingFactorMap.containsKey(STANDINGS_SCALE_KEY)) {
				summary  += "\t" + String.format("%.2f",playerScore*scalingFactorMap.get(STANDINGS_SCALE_KEY)/scaleTotalMap.get(STANDINGS_SCALE_KEY)*scaleTotalNumMap.get(STANDINGS_SCALE_KEY)*500);
			} else {
				summary  += "\t --";
			}
			if (scalingFactorMap.containsKey(COMP_SCALE_KEY)) {
				summary  += "\t" + String.format("%.2f",playerScore*scalingFactorMap.get(COMP_SCALE_KEY)/scaleTotalMap.get(COMP_SCALE_KEY)*scaleTotalNumMap.get(COMP_SCALE_KEY)*500)+"\n";
			} else {
				summary  += "\t --";
			}
			return summary ;
		}
	}

	/**
	 * Convert our PlayerMatchStats to PlayerStatShares, which are the elements the engine consumes. When complete, we will have
	 * totalUnscaledScores and totalScaledScores populated
	 * @param schema
	 * @param pss
	 * @return
	 */
	protected List<IPlayerStatShares> populate(IRatingEngineSchema schema, List<IPlayerStatShares> pss, boolean scaleStandings, boolean scaleCompetition, boolean scaleMatchAge) {
		String playerName = "nobody yet";
		String matchName = "no match yet";
		// Have to have these weight values available in the schema to work
		assert (schema instanceof IV1EngineWeightValues);
		DateTime now = DateTime.now();
		for (IPlayerMatchStats pms : pmsList) {
			try {
				playerName = pms.getName();
				IMatchGroup m = mf.get(pms.getMatchId());
				matchName = m.getDisplayName();
				if (!standingsFactorMap.containsKey(pms.getMatchId())) {
					getStandingFactorForMatch(m);
				}
				if (!matchCompWeights.containsKey(pms.getMatchId())) {
					getCompWeightingFactorForMatch(m);
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

					float totalScales = 1f;

					// see if we have already crunched the numbers on the stats and reuse if so
					IRawScore raw = rsf.getForPMSid(pms.getId(), schema.getId());
					IPlayerStatShares score = null;					
					if (raw == null) {
						score = getNewStatShares((IV1EngineWeightValues)schema, tms, otms, pms, m);
						// save it off so we don't have to recalc
						raw = rsf.create();
						raw.setPlayerId(pms.getPlayerId());
						raw.setPlayerMatchStatsId(pms.getId());
						raw.setSchemaId(schema.getId());
						raw.setDetails(score.toString());
						raw.setRawScore(score.getUnscaledScore());
						rsf.put(raw);
					} else {
						score = getNewStatShares((IV1EngineWeightValues)schema, raw, pms, m);
					}

					score.setMatchLabel(getMatchLabel(pms));
					// scale the rating by the match's standingsFactor
					if (scaleStandings) {
						float scaleAmount = standingsFactorMap.get(score.getPlayerMatchStats().getMatchId());
						score.scale(scaleAmount,STANDINGS_SCALE_KEY);
						numStatsStandingsScaled += scaleAmount;
						scaleTotalMap.put(STANDINGS_SCALE_KEY,scaleTotalMap.get(STANDINGS_SCALE_KEY) + score.getUnscaledScore()*scaleAmount);
						totalScales *= scaleAmount;
					}
					// scale by the competition's weight
					if (scaleCompetition) {
						float scaleAmount = matchCompWeights.get(score.getPlayerMatchStats().getMatchId());
						score.scale(scaleAmount,COMP_SCALE_KEY);
						numStatsCompScaled += scaleAmount;
						scaleTotalMap.put(COMP_SCALE_KEY,scaleTotalMap.get(COMP_SCALE_KEY) + score.getUnscaledScore()*scaleAmount);
						totalScales *= scaleAmount; 
					}
					// scale by the match age
					if (scaleMatchAge) {
						float scaleAmount = getTimeScale(now,score);
						score.scale(scaleAmount, AGE_SCALE_KEY);
						numStatsTimeScaled += scaleAmount;
						scaleTotalMap.put(AGE_SCALE_KEY,scaleTotalMap.get(AGE_SCALE_KEY) + score.getUnscaledScore()*scaleAmount);
						totalScales *= scaleAmount;
					}
					pss.add(score);
					numStatsTotalScaled += totalScales;
					scaleTotalMap.put(ALL_SCALE_KEY,scaleTotalMap.get(ALL_SCALE_KEY) + score.getUnscaledScore()*totalScales);
					scaleTotalMap.put(NO_SCALE_KEY,scaleTotalMap.get(NO_SCALE_KEY) + score.getUnscaledScore());
					numStats++;
					accumulateScores(score);
				} 
			} catch (Throwable e) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Engine threw a rod on player " + playerName + " from match " + matchName, e);
				throw e;
			}
		}
		return pss;

	}

	protected void accumulateScores(IPlayerStatShares pss) {
		//totalUnscaledScore += pss.getUnscaledScore();	
		totalScaledScore += pss.getScaledScore();
	}

	protected IPlayerStatShares getNewStatShares(IV1EngineWeightValues schema, ITeamMatchStats tms, ITeamMatchStats otms, IPlayerMatchStats pms, IMatchGroup m) {
		return new PlayerStatShares(schema, tms, otms, pms, m, pmsList.size());
	}

	protected IPlayerStatShares getNewStatShares(IV1EngineWeightValues schema, IRawScore raw, IPlayerMatchStats pms, IMatchGroup m) {
		return new PlayerStatShares(schema, raw, pms, m, pmsList.size());
	}

	@Override
	public List<IPlayerRating> generate(IRatingEngineSchema schema, boolean scaleStandings, boolean scaleCompetition, boolean scaleMatchAge, boolean sendReport) {


		List<IPlayerStatShares> pss = new ArrayList<IPlayerStatShares>();
		mrl = new ArrayList<IPlayerRating>();
		try {
			pss = populate(schema, pss, scaleStandings, scaleCompetition, scaleMatchAge);

			//set up our scaled total map
			scaleTotalNumMap.put(NO_SCALE_KEY, (float) numStats);
			scaleTotalNumMap.put(AGE_SCALE_KEY, numStatsTimeScaled);
			scaleTotalNumMap.put(COMP_SCALE_KEY, numStatsCompScaled);
			scaleTotalNumMap.put(STANDINGS_SCALE_KEY, numStatsStandingsScaled);
			scaleTotalNumMap.put(ALL_SCALE_KEY, numStatsTotalScaled);

			// now group the PlayerStatShares by player (since in a time series one player may have multiple matches)
			for (IPlayerStatShares score : pss) {
				if (!playerScoreMap.containsKey(score.getPlayerMatchStats().getPlayerId())) {
					playerScoreMap.put(score.getPlayerMatchStats().getPlayerId(), new ArrayList<IPlayerStatShares>());
				}
				playerScoreMap.get(score.getPlayerMatchStats().getPlayerId()).add(score);
			}

			// go through the players and create a PlayerRating for them
			for (Long p : playerScoreMap.keySet()) {
				IPlayerRating pr = prf.create();
				pr.setGenerated(DateTime.now().toDate());
				pr.setPlayerId(playerScoreMap.get(p).get(0).getPlayerMatchStats().getPlayerId());
				pr.setQueryId(query.getId());
				pr.setSchemaId(schema.getId());
				pr.setDetails(playerScoreMap.get(p).get(0).getPlayerMatchStats().getName() + "\nMatch\tScore\tUnscaled\tScaled\tMatch Aged\tStandings\tCompetition\n-----------------------------------------------------------\n");

				float scores = 0f;
				for (IPlayerStatShares s : playerScoreMap.get(p)) {
					scores += s.getScaledScore();
					pr.addMatchStatId(s.getPlayerMatchStats().getId());
					pr.addMatchStats(s.getPlayerMatchStats());	
					pr.addRatingComponent(s.getRatingComponent(scaleTotalNumMap, scaleTotalMap));
					pr.setDetails(pr.getDetails()+s.getSummaryRow(scaleTotalNumMap, scaleTotalMap));
				}

				pr.setRating(Math.round((scores/totalScaledScore)*500*playerScoreMap.keySet().size())); //numStats)); //TotalScaled));
				mrl.add(pr);

			}

			// sort by rating
			Collections.sort(mrl, new Comparator<IPlayerRating>() {
				@Override
				public int compare(IPlayerRating o1, IPlayerRating o2) {
					return Integer.compare(o2.getRating(), o1.getRating());
				}
			});


			// put the first 30 results in the db
			Iterator<IPlayerRating> iter = mrl.iterator();
			for (int i = 0; i<30; ++i) {
				if (iter.hasNext()) {	
					prf.put(iter.next());
				} else {
					break;
				}
			}
			
			if (sendReport) {
				sendReport();
			}

			// mark query complete
			query.setStatus(Status.COMPLETE);
			rqf.put(query);
		} catch (Exception e) {
			// mark query errored out
			query.setStatus(Status.ERROR);
			rqf.put(query);
			// we don't flush the ratings completed thus far. They may be handy and will get flushed on the re-run.
		}
		return mrl;

	}

	protected void getStandingFactorForMatch(IMatchGroup m) {
		IRound r = rf.get(m.getRoundId());
		List<IStanding> list = sf.getForRound(r);

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

		standingsFactorMap.put(m.getId(), standingsFactor);


	}

	protected void getCompWeightingFactorForMatch(IMatchGroup m) {
		// and the comp weightingFactor
		if (!matchCompWeights.containsKey(m.getId())) {

			IRound r = rf.get(m.getRoundId());
			ICompetition c = cf.get(r.getCompId());
			if (c.getWeightingFactor() != null) {
				matchCompWeights.put(m.getId(), c.getWeightingFactor());
			} else {
				matchCompWeights.put(m.getId(), 1F); // default to 1.0
			}
		}
	}

	//@Override
	public boolean addTeamStats(List<ITeamMatchStats> teamStats) {
		for (ITeamMatchStats ts : teamStats) {

			// sanity check
			if (ts.getLineoutsThrownIn() == null || ts.getRucksWon() == null) {
				return false;
			}

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

		return true;
	}

	//@Override
	public Boolean addPlayerStats(List<IPlayerMatchStats> playerStats) {
		if (playerStats == null || playerStats.isEmpty()) return false;

		for (IPlayerMatchStats pms : playerStats) {
			if (pms.getPosition() == position.RESERVE || pms.getTimePlayed() == null) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Trying to invoke engine with PlayerMatchStats for " + pms.getName() + " from team " + pms.getTeamAbbr() + " but his position in RESERVE or timePlayed not set. A task was probably missed.");
				return false;
			}
			if (pms.getPosition() != position.NONE) {
				pmsList.add(pms);
			}
		}
		return true;
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
		String retval = "[ " + this.getClass().getCanonicalName() + " PMS count: " + pmsList.size() + "TMS count: " + (tmsHomeMap.size() + tmsVisitMap.size()) + " ]";

		if (query != null) {
			retval += "Query is: " + query.toString();
		}
		return retval;
	}

	protected void sendReport() {
		AdminEmailer emailer = new AdminEmailer();

		emailer.setSubject("Rating Engine Report");
		emailer.setMessage(getMetrics());
		emailer.setSubject(query.toString());
		emailer.send();
	}

	@Override
	public String getMetrics() {
		String sep = " ================== ";
		String body = "<p> PlayerMatchStats count: " + pmsList.size() + " TMS count: " + (tmsHomeMap.size() + tmsVisitMap.size()) + "</p><p>";
		body += query.toString() + "</p><p>";
		body += "http://www.rugby.net/Admin.html#PortalPlace::queryId=" + query.getId().toString() + "</p><p>";
		body += "<hr> Overall" + "</p><pre>";

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

		//		for (int i=1; i<position.values().length-1; ++i) {
		for (position pos : position.values()) {
			if (query.getPositions().contains(pos)) {
				body += sep + pos + "\n";
				ss = new SummaryStatistics();
				for (IPlayerRating r : mrl) {
					//				if (r instanceof IPlayerMatchRating) {
					//					if (((IPlayerMatchRating)r).getPlayerMatchStats().getPosition().equals(position.getAt(i))) {
					//						((SummaryStatistics)ss).addValue(r.getRating());
					//					}
					//				} else 
					if (r instanceof PlayerRating) {
						for (RatingComponent rc : r.getRatingComponents()) {
							IPlayerMatchStats pms = pmsf.get(rc.getPlayerMatchStatsId());
							if (pms != null && pms.getPosition().equals(pos)) {
								((SummaryStatistics)ss).addValue(rc.getScaledRating());
							}
						}
					}
				}
				if (ss.getN() > 0)
					body += ss.toString();
			}

		}

		//		body += sep + "Matches" + sep + "\n" + sep + sep + "\n";
		//		Iterator<ITeamMatchStats> it = tmsHomeMap.values().iterator();
		//		while (it.hasNext()) {
		//			Long id = it.next().getMatchId();
		//			IMatchGroup m = mf.get(id);
		//			body += sep + m.getDisplayName() + "\n";
		//			ss = new SummaryStatistics();
		//			for (IPlayerRating r : mrl) {
		//				//				if (r instanceof IPlayerMatchRating) {
		//				//					if (((IPlayerMatchRating)r).getPlayerMatchStats().getMatchId().equals(id)) {
		//				//						((SummaryStatistics)ss).addValue(r.getRating());
		//				//					}
		//				//				}  else 
		//				if (r instanceof PlayerRating) {
		//					for (RatingComponent rc : r.getRatingComponents()) {
		//						IPlayerMatchStats pms = pmsf.get(rc.getPlayerMatchStatsId());
		//						if (pms != null && pms.getMatchId() != null && pms.getMatchId().equals(id)) {
		//							((SummaryStatistics)ss).addValue(rc.getScaledRating());
		//						}
		//					}
		//				}	
		//			}
		//			if (ss.getN() > 0)
		//				body += ss.toString();
		//		}
		body += "</pre>";
		return body;

	}

	@Override
	public boolean setQuery(IRatingQuery q) {
		this.query = q;
		boolean retval = false;
		List<IPlayerMatchStats> pmsl = pmsf.query(q);
		if (pmsl != null && !pmsl.isEmpty()) {
			retval = addPlayerStats(pmsl);
			if (retval) {
				retval = addTeamStats(tmsf.query(q));
			}
		}
		return retval;
	}


	protected String getMatchLabel(IPlayerMatchStats pms) {
		if (!matchLabelMap.containsKey(pms.getMatchId())) {
			IMatchGroup m = mf.get(pms.getMatchId());
			IRound r = rf.get(m.getRoundId());
			ICompetition c = cf.get(r.getCompId());
			int hScore = 0;
			int vScore = 0;
			if (m.getSimpleScoreMatchResultId() != null) {
				IMatchResult mr = mrf.get(m.getSimpleScoreMatchResultId());
				if (mr != null) {
					hScore = ((SimpleScoreMatchResult)mr).getHomeScore();
					vScore = ((SimpleScoreMatchResult)mr).getVisitScore();
				}
			}
			matchLabelMap.put(pms.getMatchId(), m.getHomeTeam().getAbbr() + " " + hScore + " - " + m.getVisitingTeam().getAbbr() + " " + vScore + " (" + c.getAbbr() + "-R" + r.getAbbr() + ")");
		}

		return matchLabelMap.get(pms.getMatchId());
	}

	protected float getTimeScale(DateTime origin, IPlayerStatShares pss) {
		float timescale = 0f;
		if (!matchTimeScaleMap.containsKey(pss.getPlayerMatchStats().getMatchId())) {
			LocalDate jorigin = new LocalDate(origin);
			IMatchGroup match = mf.get(pss.getPlayerMatchStats().getMatchId());
			LocalDate jmatch = new LocalDate(match.getDate());
			// days will be a negative number
			int days = Days.daysBetween(jorigin, jmatch).getDays();
			if (days < (int)(maxAge*(-1))) {		
				return 0f;
			}
			// linear from 0->maxAge days
			timescale = (maxAge+((float)days))/maxAge;

			matchTimeScaleMap.put(pss.getPlayerMatchStats().getMatchId(), timescale);
		}
		return matchTimeScaleMap.get(pss.getPlayerMatchStats().getMatchId());
	}
}
