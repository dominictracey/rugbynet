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
import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRawScoreFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingQuery.MinMinutes;
import net.rugby.foundation.model.shared.IRawScore;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.PlayerRating;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema20130713;
import net.rugby.foundation.model.shared.SimpleScoreMatchResult;
import net.rugby.foundation.model.shared.IRatingQuery.Status;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamMatchStats;
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
	// Key: playerId; Value: Total minutes played
	Map<Long, Integer> playerTimeMap = new HashMap<Long, Integer>();
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

	private float numStatsMinutesPlayedScaled;
	private Float numStatsNotMinuteScaled;
	
	// sum of the all scaling factors used for the PMSs. (D32)
	private float numStatsTotalScaled;
	// and we keep them in here to make an attempt at scalability
	protected Map<String,Float> scaleTotalNumMap = new HashMap<String,Float>();

	//this is the totals in (yellow) column F of the simulator
	protected Map<String, Float> scaleTotalMap = new HashMap<String,Float>();

	// a list of PlayerIds who should be excluded from the query for whatever reason (e.g. they haven't played enough minutes)
	protected List<Long> killList = new ArrayList<Long>();

	protected final String COMP_SCALE_KEY="Competition";
	protected final String AGE_SCALE_KEY="MatchAge";
	protected final String STANDINGS_SCALE_KEY="Standings";
	protected final String TIME_PLAYED_SCALE_KEY="TimePlayed";
	protected final String NO_MINUTE_SCALE_KEY = "NoMinuteScaling";
	protected final String NO_SCALE_KEY="Unscaled";
	protected final String ALL_SCALE_KEY="ActualScaled";

	protected final int NUM_TO_STORE = 30;
	
	private IRawScoreFactory rsf;
	private ICountryFactory cnf;
	private ITeamGroupFactory tgf;

	protected List<Long> roundPruneList = new ArrayList<Long>();
	protected List<Integer>uRoundOrdList = null;
//	private float numStatsNoMinutesTotalScaled;
//	private float totalNoMinutesScaledScore = 0f;


	public ScrumQueryRatingEngineV100() {
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.INFO);
	}

	public ScrumQueryRatingEngineV100(IPlayerFactory pf, IMatchGroupFactory mf, IPlayerRatingFactory prf, IRoundFactory rf, IStandingFactory sf, 
			IPlayerMatchStatsFactory pmsf, ITeamMatchStatsFactory tmsf, IRatingQueryFactory rqf, ICompetitionFactory cf, IMatchResultFactory mrf,
			IRawScoreFactory rsf, ICountryFactory cnf, ITeamGroupFactory tgf) {
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
		this.cnf = cnf;
		this.tgf = tgf;

		scaleTotalMap.put(NO_SCALE_KEY, 0f);
		scaleTotalMap.put(ALL_SCALE_KEY, 0f);
		scaleTotalMap.put(AGE_SCALE_KEY, 0f);
		scaleTotalMap.put(COMP_SCALE_KEY, 0f);
		scaleTotalMap.put(STANDINGS_SCALE_KEY, 0f);
		scaleTotalMap.put(TIME_PLAYED_SCALE_KEY, 0f);
		scaleTotalMap.put(NO_MINUTE_SCALE_KEY, 0f);
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

			CalculatePointDifferential(match);
			
			pms = adjustStatsByPosition(pms);
			pms = adjustStatsByTimePlayed(pms);

			this.match = match;

			Normalize();
			scalingFactorMap.put(NO_SCALE_KEY, 1f);
			scalingFactorMap.put(ALL_SCALE_KEY, 1f);
		}

		public PlayerStatShares(IV1EngineWeightValues schema, IRawScore raw,
				IPlayerMatchStats pms, IMatchGroup m, int size) {
			details = raw.getDetails();
			playerScore = raw.getRawScore();
			match = m;
			this.pms = pms;
			scalingFactorMap.put(NO_SCALE_KEY, 1f);
			scalingFactorMap.put(ALL_SCALE_KEY, 1f);
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

			// reduce playing time for cards
			if (yellowCards > 0) {
				minutesShare -= yellowCards * 10;
			}

			// don't parse out the times for these atm, so just mark it zero!
			if (redCards > 0) {
				minutesShare = 0;
			}

			// 6/12/2015 nerfing subs!
			win *= timeScale;
			pointDifferential *= timeScale;

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
				scrumLost = (scrumLost*1.5F);
				scrumStolen = (scrumStolen*1.5F);
			}  else if (pms2.getPosition().equals(position.HOOKER)) {
				scrumShare = (scrumShare*1.3F);
				scrumLost = (scrumLost*1.3F);
				scrumStolen = (scrumStolen*1.3F);
				lineoutStolen = 0; // defensively hookers don't do much in a steal
			} else if (pms2.getPosition().equals(position.LOCK)) {
				lineoutStolen = 0; // no double dip
			} else if (pms2.getPosition().equals(position.FLANKER)) {
				scrumShare = (scrumShare*.5F);
				scrumLost = (scrumLost*.5F);
				scrumStolen = (scrumStolen*.5F);
				lineoutShare = (lineoutShare*.3f);
				lineoutStolen = 0; // no double dip
			} else if (pms2.getPosition().equals(position.NUMBER8)) {
				scrumShare = (scrumShare*.5F);
				scrumLost = (scrumLost*.5F);
				scrumStolen = (scrumStolen*.5F);
				lineoutShare = (lineoutShare*.3f);
				lineoutStolen = 0; // no double dip

				// knock down their metres gained
				metersRun *= .5;
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

			details = pms.getName() + "\t " + pms.getTeamAbbr() + "\t " + pms.getPosition().getAbbr() +"\n" + 
					"Tr: "+String.format("%.2f",tries)+" (" +pms.getTries() + ")\tTA: "+String.format("%.2f",tryAssists)+"\t("+pms.getTryAssists()+")\tPts: "+String.format("%.2f",points)+" ("+ pms.getPoints()+")\n"+
					"K: "+String.format("%.2f",kicks)+" ("+pms.getKicks()+")\tR: "+String.format("%.2f",runs)+" ("+pms.getRuns()+")\tP: "+String.format("%.2f",passes)+" ("+pms.getPasses()+")\n"+
					"MR: "+String.format("%.2f",metersRun)+" ("+pms.getMetersRun()+")\tCB: "+String.format("%.2f",cleanBreaks)+" ("+pms.getCleanBreaks()+")\tDB: "+String.format("%.2f",defendersBeaten)+" ("+pms.getDefendersBeaten()+")\n"+
					"OL: "+String.format("%.2f",offloads)+" ("+pms.getOffloads()+")\tTO: "+String.format("%.2f",turnovers)+" ("+pms.getTurnovers()+")\tT: "+String.format("%.2f",tacklesMade)+" ("+pms.getTacklesMade()+")\tTM: "+String.format("%.2f",tacklesMissed)+" ("+pms.getTacklesMissed()+")\n"+
					"LO:"+String.format("%.2f",lineoutsWonOnThrow)+" ("+pms.getLineoutsWonOnThrow()+")\tLOS: "+String.format("%.2f",lineoutsStolenOnOppThrow)+" ("+pms.getLineoutsStolenOnOppThrow()+")\n"+
					"P: "+String.format("%.2f",penaltiesConceded)+" ("+pms.getPenaltiesConceded()+")\tYC: "+String.format("%.2f",yellowCards)+" ("+pms.getYellowCards()+")\tRC: "+String.format("%.2f",redCards)+" ("+pms.getRedCards()+")\n"+
					"ScW: "+String.format("%.2f",scrumShare)+" ("+tms.getScrumsWonOnOwnPut()+")\tScL:"+String.format("%.2f",scrumLost)+" ("+(tms.getScrumsPutIn()-tms.getScrumsWonOnOwnPut())+")\tScrS:"+String.format("%.2f",scrumStolen)+"("+(otms.getScrumsPutIn()-otms.getScrumsWonOnOwnPut())+")\n"+
					"LOW: "+String.format("%.2f",lineoutShare)+" ("+tms.getLineoutsWonOnOwnThrow()+")\tLOL: "+String.format("%.2f",lineoutLost)+" ("+(tms.getLineoutsThrownIn() - tms.getLineoutsWonOnOwnThrow())+")\tLOS: "+String.format("%.2f",lineoutStolen)+" ("+(otms.getLineoutsThrownIn() - otms.getLineoutsWonOnOwnThrow())+")\n"+
					"RuW: "+String.format("%.2f",ruckShare)+" ("+tms.getRucksWon()+")\tRuL: "+String.format("%.2f",ruckLost)+" ("+(tms.getRucks()-tms.getRucksWon())+")\tRuS: "+String.format("%.2f",ruckStolen)+" ("+(otms.getRucks() - otms.getRucksWon())+")\n"+
					"MaW: "+ String.format("%.2f",maulShare)+" ("+tms.getMaulsWon()+")\tMaL: "+ String.format("%.2f",maulLost)+" ("+(tms.getMauls()-tms.getMaulsWon())+")\tMaS: "+String.format("%.2f",maulStolen)+" ("+(otms.getMauls() - otms.getMaulsWon())+")\n"+ 
					"Min: "+ String.format("%.2f",minutesShare)+" ("+pms.getTimePlayed()+")\tPtDiff: "+ String.format("%.2f",pointDifferential)+" ("+(pointDifferential/weights.getPointsDifferentialWeight())+")\tWin: " + String.format("%.2f",win) + "\n"+
					"Back: " + String.format("%.2f",getBackScore()) + "\tFwd:" + String.format("%.2f",getForwardScore());
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
				//Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE, "player score for: " + pms.getName() + " is " + playerScore + " (forward score: " + forwardScore + ", back score: " + backScore + ")" );
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
		public float getNoMinutesScaledScore() {
			return getUnscaledScore() * scalingFactorMap.get(TIME_PLAYED_SCALE_KEY);
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
			RatingComponent rc = new RatingComponent(null/*toString()*/,backScore,forwardScore,playerScore,pms.getId(),matchLabel, scaledRating, unscaledRating, match.getForeignId(), cf.get(rf.get(match.getRoundId()).getCompId()).getForeignID());

//			rc.addRatingsDetails("Scaling\tValue\tScore\tRating\n----------------------------------------------------\n");
//			for (String key: scalingFactorMap.keySet()) {
//				rc.addRatingsDetails(key+"\t"+String.format("%.2f",scalingFactorMap.get(key))+"\t"+String.format("%.2f",playerScore*scalingFactorMap.get(key))+"\t"+Math.round(playerScore*scalingFactorMap.get(key)/scaleTotalMap.get(key)*scaleTotalNumMap.get(key)*500)+"\n");
//			}
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
			if (scalingFactorMap.containsKey(TIME_PLAYED_SCALE_KEY)) {
				summary  += "\t" + String.format("%.2f",playerScore*scalingFactorMap.get(TIME_PLAYED_SCALE_KEY)/scaleTotalMap.get(TIME_PLAYED_SCALE_KEY)*scaleTotalNumMap.get(TIME_PLAYED_SCALE_KEY)*500)+"\n";
			} else {
				summary  += "\t --";
			}
			return summary ;
		}

		@Override
		public String getEmailSummaryRow(Map<String, Float> scaleTotalNumMap, Map<String, Float> scaleTotalMap) {
			try {
				// "\nMatch\tScore\tUnscaled\tScaled\tMatch Aged\tStandings\tCompetition\n
				StringBuilder summary = new StringBuilder();
				summary.append("<tr>\n<td><a href=\"" + match.getForeignUrl() + "\" target=\"_blank\">" + matchLabel + "</a></td>\n");
				summary.append("<td>" + String.format("%.2f",playerScore) + "</td>\n");
				if (scalingFactorMap.containsKey(NO_SCALE_KEY) && !scalingFactorMap.get(NO_SCALE_KEY).equals(0f) ) {
					summary.append("<td>" + String.format("%.2f",playerScore*scalingFactorMap.get(NO_SCALE_KEY)/scaleTotalMap.get(NO_SCALE_KEY)*scaleTotalNumMap.get(NO_SCALE_KEY)*500) + "</td>\n");
				} else {
					summary.append("<td> -- </td>\n");
				}
				if (scalingFactorMap.containsKey(ALL_SCALE_KEY) && !scalingFactorMap.get(ALL_SCALE_KEY).equals(0f)) {
					summary.append("<td>" + String.format("%.2f",playerScore*scalingFactorMap.get(ALL_SCALE_KEY)/scaleTotalMap.get(ALL_SCALE_KEY)*scaleTotalNumMap.get(ALL_SCALE_KEY)*500) + "</td>\n");
				} else {
					summary.append("<td> -- </td>\n");
				}
				if (scalingFactorMap.containsKey(AGE_SCALE_KEY) && !scalingFactorMap.get(AGE_SCALE_KEY).equals(0f)) {
					summary.append("<td>" + String.format("%.2f",playerScore*scalingFactorMap.get(AGE_SCALE_KEY)/scaleTotalMap.get(AGE_SCALE_KEY)*scaleTotalNumMap.get(AGE_SCALE_KEY)*500) + "</td>\n");
				} else {
					summary.append("<td> -- </td>\n");
				}
				if (scalingFactorMap.containsKey(STANDINGS_SCALE_KEY) && !scalingFactorMap.get(STANDINGS_SCALE_KEY).equals(0f)) {
					summary.append("<td>" + String.format("%.2f",playerScore*scalingFactorMap.get(STANDINGS_SCALE_KEY)/scaleTotalMap.get(STANDINGS_SCALE_KEY)*scaleTotalNumMap.get(STANDINGS_SCALE_KEY)*500) + "</td>\n");
				} else {
					summary.append("<td> -- </td>\n");
				}
				if (scalingFactorMap.containsKey(COMP_SCALE_KEY) && !scalingFactorMap.get(COMP_SCALE_KEY).equals(0f)) {
					summary.append("<td>" + String.format("%.2f",playerScore*scalingFactorMap.get(COMP_SCALE_KEY)/scaleTotalMap.get(COMP_SCALE_KEY)*scaleTotalNumMap.get(COMP_SCALE_KEY)*500) + "</td>\n");
				} else {
					summary.append("<td> -- </td>\n");
				}
				if (scalingFactorMap.containsKey(TIME_PLAYED_SCALE_KEY) && !scalingFactorMap.get(TIME_PLAYED_SCALE_KEY).equals(0f)) {
					summary.append("<td>" + String.format("%.2f",playerScore*scalingFactorMap.get(TIME_PLAYED_SCALE_KEY)/scaleTotalMap.get(TIME_PLAYED_SCALE_KEY)*scaleTotalNumMap.get(TIME_PLAYED_SCALE_KEY)*500) + "</td>\n");
				} else {
					summary.append("<td> -- </td>\n");
				}
				summary.append("</tr>\n");
				return summary.toString();
			} catch (Throwable e) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problems constructing summary row", e);
				throw e;
			}
		}

	}

	/**
	 * Convert our PlayerMatchStats to PlayerStatShares, which are the elements the engine consumes. When complete, we will have
	 * totalUnscaledScores and totalScaledScores populated
	 * @param schema
	 * @param pss
	 * @return
	 */
	protected List<IPlayerStatShares> populate(IRatingEngineSchema schema, List<IPlayerStatShares> pss, boolean scaleStandings, boolean scaleCompetition, boolean scaleMatchAge, boolean scaleMinutesPlayed) {
		String playerName = "nobody yet";
		String matchName = "no match yet";
		// Have to have these weight values available in the schema to work
		assert (schema instanceof IV1EngineWeightValues);
		DateTime now = DateTime.now();
		for (IPlayerMatchStats pms : pmsList) {
			try {
				if (killList == null || !killList.contains(pms.getPlayerId())) {
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
						float totalNoMinutesScales = 1f;
						
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
							scaleTotalMap.put(NO_MINUTE_SCALE_KEY,scaleTotalMap.get(NO_MINUTE_SCALE_KEY) + score.getUnscaledScore()*scaleAmount);
							totalScales *= scaleAmount;
							totalNoMinutesScales *= scaleAmount;
						}
						// scale by the competition's weight
						if (scaleCompetition) {
							float scaleAmount = matchCompWeights.get(score.getPlayerMatchStats().getMatchId());
							score.scale(scaleAmount,COMP_SCALE_KEY);
							numStatsCompScaled += scaleAmount;
							scaleTotalMap.put(COMP_SCALE_KEY,scaleTotalMap.get(COMP_SCALE_KEY) + score.getUnscaledScore()*scaleAmount);
							scaleTotalMap.put(NO_MINUTE_SCALE_KEY,scaleTotalMap.get(NO_MINUTE_SCALE_KEY) + score.getUnscaledScore()*scaleAmount);
							totalScales *= scaleAmount; 
							totalNoMinutesScales *= scaleAmount;
						}
						// scale by the match age
						if (scaleMatchAge) {
							float scaleAmount = getTimeScale(now,score);
							score.scale(scaleAmount, AGE_SCALE_KEY);
							numStatsTimeScaled += scaleAmount;
							scaleTotalMap.put(AGE_SCALE_KEY,scaleTotalMap.get(AGE_SCALE_KEY) + score.getUnscaledScore()*scaleAmount);
							scaleTotalMap.put(NO_MINUTE_SCALE_KEY,scaleTotalMap.get(NO_MINUTE_SCALE_KEY) + score.getUnscaledScore()*scaleAmount);
							totalScales *= scaleAmount;
							totalNoMinutesScales *= scaleAmount;
						}
						// scale by minutes played
						if (scaleMinutesPlayed) {
							float scaleAmount = getMinutesPlayedScale(score);
							score.scale(scaleAmount, TIME_PLAYED_SCALE_KEY);
							numStatsMinutesPlayedScaled += scaleAmount;
							scaleTotalMap.put(TIME_PLAYED_SCALE_KEY,scaleTotalMap.get(TIME_PLAYED_SCALE_KEY) + score.getUnscaledScore()*scaleAmount);
							totalScales *= scaleAmount;
						}
						pss.add(score);
						numStatsTotalScaled += totalScales;
	//					numStatsNoMinutesTotalScaled += totalNoMinutesScales;
						
						scaleTotalMap.put(ALL_SCALE_KEY,scaleTotalMap.get(ALL_SCALE_KEY) + score.getUnscaledScore()*totalScales);
						scaleTotalMap.put(NO_MINUTE_SCALE_KEY, scaleTotalMap.get(NO_MINUTE_SCALE_KEY) + score.getUnscaledScore()*totalNoMinutesScales);
						scaleTotalMap.put(NO_SCALE_KEY,scaleTotalMap.get(NO_SCALE_KEY) + score.getUnscaledScore());
						numStats++;
						accumulateScores(score);
					}
				} 
			} catch (Throwable e) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Engine threw a rod on player " + playerName + " from match " + matchName, e);
				throw e;
			}
		}
		return pss;

	}

	//	private void accumulateTimePlayed(IPlayerStatShares score) {
	//		IPlayerMatchStats stats = score.getPlayerMatchStats();
	//		if (!playerTimeMap.containsKey(stats.getPlayerId())) {
	//			playerTimeMap.put(stats.getPlayerId(), stats.getTimePlayed());
	//		} else {
	//			int t = playerTimeMap.get(stats.getPlayerId());
	//			playerTimeMap.put(stats.getPlayerId(), t + stats.getTimePlayed());			
	//		}
	//	}

	protected void accumulateScores(IPlayerStatShares pss) {
	//	totalNoMinutesScaledScore += pss.getNoMinutesScaledScore();
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
	public List<IPlayerRating> generate(IRatingEngineSchema schema, boolean scaleStandings, boolean scaleCompetition, boolean scaleMatchAge, boolean scaleMinutesPlayed, boolean sendReport) {


		List<IPlayerStatShares> pss = new ArrayList<IPlayerStatShares>();
		mrl = new ArrayList<IPlayerRating>();
		try {


			// we need to do a pass through to remove people who haven't played the minimum if we are scaling on time played
			if (scaleMinutesPlayed) {
				dropPlayersWithoutMinimumTimePlayed();
			}

			pss = populate(schema, pss, scaleStandings, scaleCompetition, scaleMatchAge, scaleMinutesPlayed);

			//set up our scaled total map
			scaleTotalNumMap.put(NO_SCALE_KEY, (float) numStats);
			scaleTotalNumMap.put(AGE_SCALE_KEY, numStatsTimeScaled);
			scaleTotalNumMap.put(COMP_SCALE_KEY, numStatsCompScaled);
			scaleTotalNumMap.put(STANDINGS_SCALE_KEY, numStatsStandingsScaled);
			scaleTotalNumMap.put(NO_MINUTE_SCALE_KEY, numStatsNotMinuteScaled);
			scaleTotalNumMap.put(TIME_PLAYED_SCALE_KEY, numStatsMinutesPlayedScaled);
			scaleTotalNumMap.put(ALL_SCALE_KEY, numStatsTotalScaled);

			// now group the PlayerStatShares by player (since in a time series one player may have multiple matches)
			for (IPlayerStatShares score : pss) {
				if (!playerScoreMap.containsKey(score.getPlayerMatchStats().getPlayerId())) {
					playerScoreMap.put(score.getPlayerMatchStats().getPlayerId(), new ArrayList<IPlayerStatShares>());
				}
				playerScoreMap.get(score.getPlayerMatchStats().getPlayerId()).add(score);
			}
			
			// need to accumulate the total timescaled

			if (scaleMinutesPlayed) {
				totalScaledScore = 0f;
				for (Long p : playerScoreMap.keySet()) {
					float score = 0f;
					float time = 0f;
					for (IPlayerStatShares s : playerScoreMap.get(p)) {
						score += s.getNoMinutesScaledScore();
						time += s.getPlayerMatchStats().getTimePlayed();
					}
					totalScaledScore += score/time;
				}
			}


			// go through the players and create a PlayerRating for them
			for (Long p : playerScoreMap.keySet()) {

				IPlayerRating pr = prf.create();
				pr.setGenerated(DateTime.now().toDate());
				pr.setPlayerId(playerScoreMap.get(p).get(0).getPlayerMatchStats().getPlayerId());
				pr.setQueryId(query.getId());
				pr.setSchemaId(schema.getId());

				float scores = 0f;
				float time = 0;

				for (IPlayerStatShares s : playerScoreMap.get(p)) {
					if (scaleMinutesPlayed) {
						time += s.getPlayerMatchStats().getTimePlayed();
						scores += s.getNoMinutesScaledScore();
					} else {
						scores += s.getScaledScore();
					}
					pr.addMatchStatId(s.getPlayerMatchStats().getId());
					pr.addMatchStats(s.getPlayerMatchStats());	
					pr.addRatingComponent(s.getRatingComponent(scaleTotalNumMap, scaleTotalMap));
				}


				if (scaleMinutesPlayed) {
					scores = scores/time; 
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


			// put the first NUM_TO_STORE results in the db
			Iterator<IPlayerRating> iter = mrl.iterator();
			for (int i = 0; i<NUM_TO_STORE; ++i) {
				if (iter.hasNext()) {
					IPlayerRating pr = iter.next();

					//finalizeRatingComponents(pr, playerScoreMap.get(pr.getPlayerId()));  // the stuff we are going to display to the endusers

//					StringBuilder sb = new StringBuilder();
//					sb.append(playerScoreMap.get(pr.getPlayerId()).get(0).getPlayerMatchStats().getName() + "\nMatch\tScore\tUnscaled\tScaled\tMatch Aged\tStandings\tCompetition\n-----------------------------------------------------------\n");
//
//					for (IPlayerStatShares s : playerScoreMap.get(pr.getPlayerId())) {
//						sb.append(s.getSummaryRow(scaleTotalNumMap, scaleTotalMap));						
//					}

					Collections.sort(pr.getRatingComponents(), new Comparator<RatingComponent>() {
						@Override
						public int compare(RatingComponent o1, RatingComponent o2) {
							IPlayerMatchStats pms1 = pmsf.get(o1.getPlayerMatchStatsId());
							IMatchGroup m1 = mf.get(pms1.getMatchId());
							IPlayerMatchStats pms2 = pmsf.get(o2.getPlayerMatchStatsId());
							IMatchGroup m2 = mf.get(pms2.getMatchId());

							return m2.getDate().compareTo(m1.getDate());
						}
					});

					if (pr.getPlayerId() != null) {
						pr.setPlayer(pf.get(pr.getPlayerId()));
					}
					
	//				pr.setDetails(sb.toString());
					prf.put(pr);
				} else {
					break;
				}
			}

			if (sendReport) {
				sendReport();
			}

			// mark query complete
			query.setStatus(Status.COMPLETE);

			// also pull out any rounds that didn't have any stats
			for (Long rid : roundPruneList) {
				query.getRoundIds().remove(rid);
			}

			rqf.put(query);
		} catch (Exception e) {
			// mark query errored out
			query.setStatus(Status.ERROR);
			rqf.put(query);
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Error in Rating Engine generate() function", e);
			// we don't flush the ratings completed thus far. They may be handy and will get flushed on the re-run.
		}
		return mrl;

	}

	// *** THIS DOESN'T WORK BECAUSE THE PSS ISN'T POPULATED ONCE A RAWRATING IS CREATED FOR IT - PROBABLY WANT TO JUST CREATE A getPlayerMatchStats() FOR THE
	// ***		ENDUSER CLIENTS TO MAP TO CHARTS WHEN THEY LOOK AT MATCH-LEVEL DETAIL
	//	private void finalizeRatingComponents(IPlayerRating pr, List<IPlayerStatShares> list) {
	//		for (RatingComponent rc: pr.getRatingComponents()) {
	//			PlayerStatShares c = null;
	//			for (IPlayerStatShares share : list) {
	//				if (rc.getPlayerMatchStatsId().equals(share.getPlayerMatchStats().getId())) {
	//					c = (PlayerStatShares)share;
	//					break;
	//				}
	//			}
	//
	//			if (c != null) {
	//				rc.setOffence(c.tries + c.tryAssists + c.points + c.runs + c.passes + c.kicks + c.metersRun + c.cleanBreaks + c.defendersBeaten + c.offloads + c.turnovers);
	//				rc.setDefence(c.tacklesMade + c.tacklesMissed);
	//				rc.setSetPlay(c.scrumShare + c.scrumLost + c.scrumStolen + c.lineoutLost + c.lineoutShare + c.lineoutsStolenOnOppThrow + c.lineoutStolen + c.lineoutsWonOnThrow);
	//				rc.setLoosePlay(c.ruckLost + c.ruckShare + c.ruckShare + c.maulLost + c.maulShare + c.maulStolen);
	//				rc.setDiscipline(c.penaltiesConceded + c.yellowCards + c.redCards);
	//				rc.setMatchResult(c.win + c.pointDifferential + c.minutesShare);
	//			}
	//		}
	//
	//	}

	protected void dropPlayersWithoutMinimumTimePlayed() {
		Map<Long,Integer> playerTimes = new HashMap<Long,Integer>();
		int minimumMinutes = getMinimumMinutes(); 

		// if people don't have enough time, pull them out altogether so they don't mess up the total players count
		killList = new ArrayList<Long>();
		for (IPlayerMatchStats pms : pmsList) {
			if (!playerTimes.containsKey(pms.getPlayerId())) {
				playerTimes.put(pms.getPlayerId(), pms.getTimePlayed());
			} else {
				int t = playerTimes.get(pms.getPlayerId());
				playerTimes.put(pms.getPlayerId(), t + pms.getTimePlayed());
			}
		}

		for (Long pid : playerTimes.keySet()) {
			if (playerTimes.get(pid) < minimumMinutes) {
				killList.add(pid);
			}
		}
	}

	// Determine the minimum minutes someone needs to have played to participate in a query that is scaled by minutes.
	protected int getMinimumMinutes() {

		assert (query.getRatingMatrix().getCriteria() == Criteria.AVERAGE_IMPACT);

		if (query.getMinMinutesType().equals(MinMinutes.ROUND)) {
			return uRoundOrdList.size() * query.getMinMinutes();
		} else {
			return query.getMinMinutes();
		}
//		// if this is a real comp just use the number of rounds played so far
//		int minimumMinutes = uRoundOrdList.size() * roundMinMinutes;
//		
//		// if it is a virtual comp that uses club rugby and test rugby there is an 800 minute min
//		// if it is a virtual comp that uses only test rugby there is a 350 minute min
////		if (query.getCompIds().size() > 1) {
////			boolean isTest = true;
////			for (Long cid : query.getCompIds()) {
////				ICompetition c = cf.get(cid);
////				if (c.getWeightingFactor() < 2f) {
////					isTest = false;
////				}
////			}
////			minimumMinutes = isTest ? 350 : 800;
////		}
//
//		
//		return minimumMinutes;
	}

	protected void getStandingFactorForMatch(IMatchGroup m) {
		IRound r = rf.get(m.getRoundId());
		List<IStanding> list = sf.getForRound(r);

		int sTot = 0;
		int count = 0;
		boolean found = false;
		for (IStanding s : list) {
			if (s.getTeamId().equals(m.getHomeTeamId()) || s.getTeamId().equals(m.getVisitingTeamId())) {
				if (s.getStanding() != null) {
					sTot += s.getStanding();
					count ++;
					if (count == 2) {
						found = true;
						break;
					}
				} else {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING,"Running rating engine for round without standings in place.");
				}
			}
		}

		Float standingsFactor = 1F;
		if (found) {
			//standingsFactor =  1F / (float)(Math.sqrt(sTot-2F)) + .43F;
			standingsFactor =  1F / (float)(Math.sqrt(sTot)) + .5F;  // updated based on conversations of 12/29/2014 to be flatter at the top end
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


	// This give a number between 1 and 4. 80m => 1, 40M => 2, 20m => 4, 10m => 4 (don't want the people who run on for a minute and score a try to blow it up
	protected float getMinutesPlayedScale(IPlayerStatShares score) {

		if (score != null && score.getPlayerMatchStats() != null && score.getPlayerMatchStats().getTimePlayed() > 0) {
			// want a number between 20 and 80, inclusive
			int minutesPlayed = (80 < score.getPlayerMatchStats().getTimePlayed()) ? 80 : score.getPlayerMatchStats().getTimePlayed();
			minutesPlayed = (20 > score.getPlayerMatchStats().getTimePlayed()) ? 20 : score.getPlayerMatchStats().getTimePlayed();
			return 80f/(float)minutesPlayed; //(float) Math.log(80 - minutesPlayed + 1) + 1;
		} else {
			return 0;
		}
	}

	//	protected float getMinutesPlayedScale(IPlayerStatShares score) {
	//		if (score.getPlayerMatchStats().getTimePlayed() > 0) {
	//			return 80f/(float)score.getPlayerMatchStats().getTimePlayed() ;
	//		} else {
	//			return 0f;
	//		}
	//	}

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

		// For the World Cup we come across teams that play two matches in a single round. This really hoses up
		// the match series because we process queries for matches that haven't been played yet (because the players
		// for one of the teams have stats from the first match in the round). To guard against this, we check that if
		// a query specifies teams, that the stats contain entries for players from BOTH teams.
		Map<Long,Boolean> teamchecker = null;
		if (query != null && query.getTeamIds() != null && !query.getTeamIds().isEmpty()) {
			teamchecker = new HashMap<Long,Boolean>();
			for (Long teamId : query.getTeamIds()) {
				teamchecker.put(teamId, false);
			}
		}
		
		// we also need to avoid the situation where we are doing a match series and there are two matches for a team in the weekend.
		// in this case 
		if (query != null && query.getRoundIds() != null && query.getRoundIds().size() == 1 && query.getTeamIds() != null && query.getTeamIds().size() == 2 && query.getRatingMatrixId() != null) {
			// it's a match series query
			// find the match ID
		}

		for (IPlayerMatchStats pms : playerStats) {
			if (pms.getPosition() == position.RESERVE || pms.getTimePlayed() == null) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING,"Trying to invoke engine with PlayerMatchStats for " + pms.getName() + " from team " + pms.getTeamAbbr() + " but his position in RESERVE or timePlayed not set. A task was probably missed. Match stats dropped.");
				// what we need is a link to the admin console that allows you to address this:
				// /Admin.html?editPlayerMatchStats=3904809234234
				// or even a RESTful thimgie
				//return false;
			} else if (pms.getPosition() != position.NONE) {
				if (teamchecker != null) {
					teamchecker.put(pms.getTeamId(), true);
				}
				pmsList.add(pms);
			}
		}
		
		// if we didn't find stats for one of the teams, return false
		if (teamchecker != null) {
			for (Long tid : query.getTeamIds()) {
				if (!teamchecker.get(tid)) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "No stats available for team id " + tid + " in query " + query.getLabel() + ". Aborting rating engine initialization.");
					return false;
				}
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
		emailer.setSubject(prettyQuery(query));
		emailer.send();
	}

	@Override
	public String getMetrics() {
		String body = "<p> PlayerMatchStats count: " + pmsList.size() + " TMS count: " + (tmsHomeMap.size() + tmsVisitMap.size()) + "</p><p>";
		body += query.toString() + "</p><p>";
		body += "http://www.rugby.net/Admin.html#PortalPlace::queryId=" + query.getId().toString() + "</p>";

		Iterator<IPlayerRating> iter = mrl.iterator();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i<NUM_TO_STORE; ++i) {
			if (iter.hasNext()) {
				IPlayerRating pr = iter.next();
				int ord = i + 1;
				IPlayerMatchStats stats = playerScoreMap.get(pr.getPlayerId()).get(0).getPlayerMatchStats();
				sb.append("<p><h3>" + ord + ". " + stats.getName() + " (" + stats.getTeamAbbr() + ") &nbsp;" + pr.getRating() + "</h3></p>\n<table width=\"1000px\">\n<tr>\n<th style=\"text-align:left\">Match</th>\n<th style=\"text-align:left\">Score</th>\n<th style=\"text-align:left\">Unscaled</th>\n<th style=\"text-align:left\">Scaled</th>\n<th style=\"text-align:left\">Match Aged</th>\n<th style=\"text-align:left\">Standings</th>\n<th style=\"text-align:left\">Competition</th>\n</tr>\n");
				for (IPlayerStatShares s : playerScoreMap.get(pr.getPlayerId())) {
					sb.append(s.getEmailSummaryRow(scaleTotalNumMap, scaleTotalMap));
				}
				sb.append("</table>\n<hr>\n");
			} else {
				break;
			}
		}

		body += sb.toString();

		if (query.getInstrument()) {
			//		
			body += "<hr> Overall" + "</p><pre>";

			StatisticalSummary ss = new SummaryStatistics();
			for (IPlayerRating r : mrl) {
				((SummaryStatistics)ss).addValue(r.getRating());
			}

			body += ss.toString();

			//		// backs vs. forwards
			//		body += sep + "Backs vs. Forwards" + sep + "\n" + sep + sep + "\n";
			//		StatisticalSummary forwards  = new SummaryStatistics();
			//		StatisticalSummary backs = new SummaryStatistics();
			//		for (IPlayerRating r : mrl) {
			//			if (r.getMatchStats().get(0).isForward()>0) {
			//				((SummaryStatistics)forwards).addValue(r.getRating());
			//			}	else {
			//				((SummaryStatistics)backs).addValue(r.getRating());
			//			}	
			//		}
			//		body += sep + "Backs" + "\n";
			//		body += backs.toString();
			//
			//		body += sep + "Forwards" + "\n";
			//		body += forwards.toString();
			//
			String sep = "<hr>";
			body += sep + "Positions" + sep + "\n" + sep + sep + "\n";

			//		for (int i=1; i<position.values().length-1; ++i) {
			for (position pos : position.values()) {
				if (query.getPositions().size() == 0 || query.getPositions().contains(pos)) {
					body += sep + pos + "\n";
					ss = new SummaryStatistics();
					for (IPlayerRating r : mrl) {
						if (r instanceof PlayerRating) {
							for (RatingComponent rc : r.getRatingComponents()) {
								IPlayerMatchStats pms = pmsf.get(rc.getPlayerMatchStatsId());
								if (pms != null && pms.getPosition().equals(pos)) {
									((SummaryStatistics)ss).addValue(rc.getUnscaledRating());
								}
							}
						}
					}
					if (ss.getN() > 0)
						body += ss.toString();
				}

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

		body += "--";
		return body;

	}



	@Override
	public boolean setQuery(IRatingQuery q) {
		this.query = q;
		boolean retval = false;
		roundPruneList.addAll(q.getRoundIds()); // we will knock these off as we find stats from them, then at the end, update the query to take out any empty rounds

		// if this is an IMPACT query we need to know how many weekends we are looking at to set our minimum participation minutes.
		if (q.getScaleMinutesPlayed()) {
			uRoundOrdList = new ArrayList<Integer>();
			for (Long rid : q.getRoundIds()) {
				IRound r = rf.get(rid);
				if (!uRoundOrdList.contains(r.getUrOrdinal())) {
					uRoundOrdList.add(r.getUrOrdinal());
				}
			}
		}

		List<IPlayerMatchStats> pmsl = pmsf.query(q);

		if (pmsl != null && !pmsl.isEmpty()) {
			retval = addPlayerStats(pmsl);
			
			if (retval) {
				retval = addTeamStats(tmsf.query(q));
				if (!retval) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,"Failed to set team match stats for query " + q.getLabel());
				}
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,"Failed to set player match stats for query " + q.getLabel());
			}
		}
		return retval;
	}


	protected String getMatchLabel(IPlayerMatchStats pms) {
		if (!matchLabelMap.containsKey(pms.getMatchId())) {
			IMatchGroup m = mf.get(pms.getMatchId());
			IRound r = rf.get(m.getRoundId());
			// we need to prune our round list as well
			if (roundPruneList.contains(r.getId())) {
				roundPruneList.remove(r.getId());
			}
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

	protected String prettyQuery(IRatingQuery rq) {


		String retval = "";

		if (rq.getCompIds() != null && !rq.getCompIds().isEmpty()) {
			retval += "Comps: ";
			for (Long compId : rq.getCompIds()) {
				if (compId != -1) {
					ICompetition c = cf.get(compId);
					retval += c.getAbbr() + " ";
				}
			}
		}

		if (rq.getRoundIds() != null && !rq.getRoundIds().isEmpty()) {
			if (rq.getRoundIds().size() > 5) {
				retval += " Rounds: * ";
			} else {
				retval += " Rounds: ";
				for (Long roundId : rq.getRoundIds()) {
					IRound r = rf.get(roundId);
					retval += r.getAbbr() + " ";
				}
			}
		}

		if (rq.getPositions() != null && !rq.getPositions().isEmpty()) {
			retval += " Positions: ";
			for (position p : rq.getPositions()) {
				retval += p.getAbbr() + " ";
			}
		}

		if (rq.getCountryIds() != null && !rq.getCountryIds().isEmpty()) {
			retval += " Countries: ";
			for (Long cId : rq.getCountryIds()) {
				ICountry c = cnf.getById(cId);
				retval += c.getAbbr() + " ";
			}
		}

		if (rq.getTeamIds() != null && !rq.getTeamIds().isEmpty()) {
			retval += " Teams: ";
			for (Long tId : rq.getTeamIds()) {
				ITeamGroup t = tgf.get(tId);
				retval += t.getAbbr() + " ";
			}
		}

		return retval;

	}


}
