package net.rugby.foundation.admin.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;
import net.rugby.foundation.admin.shared.IV1EngineWeightValues;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.Position.position;

public class ScrumMatchRatingEngineV100 extends ScrumMatchRatingEngineBase implements
IMatchRatingEngine  {

	private ITeamMatchStatsFactory tmsf;

	public ScrumMatchRatingEngineV100(IPlayerFactory pf, IPlayerMatchRatingFactory pmrf, ITeamMatchStatsFactory tmsf) {
		super(pf, pmrf);
		this.tmsf = tmsf;
	}



	// this is odd... it's different than the number of people who get on the field during the game (numPlayers below)
	// need to think more about what this means. 
	// The danger we want to avoid is a team that uses subs "diluting" its players' rankings.
	private final int playersOnField = 30;

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
		private float minutesShare;

		private float pointDifferential;

		private float backScore;
		private float forwardScore;

		private int numPlayers;

		private float playerScore = 0F;

		private float pointDifferentialWeightedScore;

		private double smoothScore;

		private IV1EngineWeightValues weights;

		public PlayerStatShares(IV1EngineWeightValues weights, ITeamMatchStats tms, ITeamMatchStats combined, IPlayerMatchStats pms, IMatchGroup match, int totalTryAssists, int totalLineoutsStolen, int totalMinutes, int numPlayers) {
			assert(tms.getTeamId().equals(pms.getTeamId()));

			Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINEST);
			
			this.pms = pms;
			this.weights = weights;
			
			if (!tms.getTries().equals(0)) {
				tries = (float)pms.getTries() / (float)combined.getTries();
			} else {
				tries = 0;
			}
			if (totalTryAssists != 0) {
				tryAssists = (float) pms.getTryAssists() / (float) totalTryAssists;
			} else {
				tryAssists= 0;
			}			
			if ((match.getSimpleScoreMatchResult().getHomeScore() + match.getSimpleScoreMatchResult().getVisitScore()) != (0)) {
				points = (float) pms.getPoints() / (float)(match.getSimpleScoreMatchResult().getHomeScore() + match.getSimpleScoreMatchResult().getVisitScore());
			} else {
				points= 0;
			}			
			if (!tms.getKicksFromHand().equals(0)) {
				kicks = (float) pms.getKicks() / (float)combined.getKicksFromHand();
			} else {
				kicks= 0;
			}			
			if (!tms.getPasses().equals(0)) {
				passes = (float) pms.getPasses() / (float) combined.getPasses();
			} else {
				passes= 0;
			}			
			if (!tms.getRuns().equals(0)) {
				runs = (float) pms.getRuns() /(float) combined.getRuns();
			} else {
				runs= 0;
			}			
			if (!tms.getMetersRun().equals(0)) {
				metersRun = (float) pms.getMetersRun() /(float) combined.getMetersRun();
			} else {
				metersRun= 0;
			}			
			if (!tms.getCleanBreaks().equals(0)) {
				cleanBreaks = (float) pms.getCleanBreaks() /(float) combined.getCleanBreaks();
			} else {
				cleanBreaks= 0;
			}			
			if (!tms.getDefendersBeaten().equals(0)) {
				defendersBeaten = (float) pms.getDefendersBeaten() /(float) combined.getDefendersBeaten();
			} else {
				defendersBeaten= 0;
			}			
			if (!tms.getOffloads().equals(0)) {
				offloads = (float) pms.getOffloads() /(float) combined.getOffloads();
			} else {
				offloads= 0;
			}			
			if (!tms.getTurnoversConceded().equals(0)) {
				turnovers = (float) pms.getTurnovers() /(float) combined.getTurnoversConceded();
			} else {
				turnovers= 0;
			}			
			if (!tms.getTacklesMade().equals(0)) {
				tacklesMade = (float) pms.getTacklesMade() /(float) combined.getTacklesMade();
			} else {
				tacklesMade= 0;
			}			
			if (!tms.getTacklesMissed().equals(0)) {
				tacklesMissed = (float) pms.getTacklesMissed()/(float) combined.getTacklesMissed();
			} else {
				tacklesMissed= 0;
			}			
			if (!tms.getLineoutsWonOnOwnThrow().equals(0)) {
				lineoutsWonOnThrow = (float) pms.getLineoutsWonOnThrow() /(float) combined.getLineoutsWonOnOwnThrow();
			} else {
				lineoutsWonOnThrow= 0;
			}			
			if (totalLineoutsStolen != 0) {
				lineoutsStolenOnOppThrow = (float) pms.getLineoutsStolenOnOppThrow() /(float) totalLineoutsStolen;
			} else {
				lineoutsStolenOnOppThrow= 0;
			}			
			if (!tms.getPenaltiesConceded().equals(0)) {
				penaltiesConceded = (float) pms.getPenaltiesConceded() / (float) combined.getPenaltiesConceded();
			} else {
				penaltiesConceded = 0;
			}			
			if (!tms.getYellowCards().equals(0)) {
				yellowCards = (float) pms.getYellowCards() /(float) combined.getYellowCards();
			} else {
				yellowCards= 0;
			}			
			if (!tms.getRedCards().equals(0)) {
				redCards = (float) pms.getRedCards() /(float) combined.getRedCards();
			} else {
				redCards= 0;
			}			

			if (totalMinutes != 0) {
				minutesShare = (float) pms.getTimePlayed() / (float) totalMinutes;
			} else {
				minutesShare= 0;
			}
			if (tms.getScrumsPutIn() != 0F) {
				scrumShare = ((float) tms.getScrumsWonOnOwnPut() / (float) tms.getScrumsPutIn()) * minutesShare;
			} else {
				scrumShare = 0;
			}
			if (tms.getLineoutsThrownIn() != 0F) {
				lineoutShare = ((float) tms.getLineoutsWonOnOwnThrow() / (float) tms.getLineoutsThrownIn()) * minutesShare;
			} else {
				lineoutShare= 0;
			}
			if (tms.getRucks() != 0F) {
				ruckShare =  ((float) tms.getRucksWon() / (float) tms.getRucks()) * minutesShare;
			} else {
				ruckShare = 0;
			}
			if (!tms.getMauls().equals(0)) {
				maulShare = ((float) tms.getMaulsWon() / (float) tms.getMauls()) * minutesShare;
			} else {
				maulShare= 0;
			}
			this.numPlayers = numPlayers;
			Normalize();
			CalculatePointDifferential(match);

		}

		private void CalculatePointDifferential(IMatchGroup match) {
			pointDifferential = (match.getSimpleScoreMatchResult().getHomeScore() - match.getSimpleScoreMatchResult().getVisitScore());

			if (!pms.getTeamId().equals(match.getHomeTeamId())) {
				pointDifferential *= -1;
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
			
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST, pms.getName() +" T:"+tries+" TA:"+tryAssists+" Pts:"+points+" K:"+kicks+" R:"+runs+" P:"+passes+" MR:"+metersRun+
					cleanBreaks+" DB:"+defendersBeaten+" OL:"+offloads+" TO:"+turnovers+" T:"+tacklesMade+" TM:"+tacklesMissed+" LO:"+lineoutsWonOnThrow+
					" LOStolen:"+lineoutsStolenOnOppThrow+" P:"+penaltiesConceded+" YC:"+yellowCards+" RC: "+redCards+" ScrumShare:"+scrumShare+
					" LOShare:"+lineoutShare+" RuckShare:"+ruckShare+" MSh:"+ maulShare+" MinShare:"+ minutesShare);
			
		}

		private float getBackScore() {
			backScore = (tries + tryAssists + points + kicks + passes + runs + metersRun + cleanBreaks + 
					defendersBeaten + offloads + turnovers + tacklesMade + tacklesMissed + penaltiesConceded +
					yellowCards + redCards + minutesShare); // * 500 * playersOnField; // no ruckShare?
							return backScore;
		}

		private float getForwardScore() {
			forwardScore = (lineoutsWonOnThrow + lineoutsStolenOnOppThrow + scrumShare + lineoutShare + ruckShare + maulShare); // * 500 * playersOnField;
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
				playerScore = (getBackScore() + (isForward() * getForwardScore()))/(1+isForward());
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE, "player score for: " + pms.getName() + " is " + playerScore + " (forward score: " + forwardScore + ", back score: " + backScore + ")" );
			}
			return playerScore;
		}

		public IPlayerMatchStats getPlayerMatchStats() {
			return pms;
		}

		public float getPointDifferentialWeightedScore(float totalPlayerScore) {
			
			// the final of the last World Cup was decided by one point, making this negligible.
			// Something needs to be said for winning the game so we'll just add some to it to make sure it is a significant amount
//			if (pointDifferential > 0) {
//				pointDifferential += 10;
//			} else if (pointDifferential < 0) {
//				pointDifferential -= 10;
//			}
			
			float normalizedPlayerScore = getPlayerScore() / totalPlayerScore;
			float lnPointDiff = (float) Math.log(Math.abs(pointDifferential) + 1);
			if (pointDifferential < 0) {
				lnPointDiff *= -1;
			}
			pointDifferentialWeightedScore = normalizedPlayerScore + (normalizedPlayerScore * weights.getPointsDifferentialWeight() * lnPointDiff);// (normalizedPlayerScore * 0.2F * pointDifferential);
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE, "pointDifferentialWeightedScore for: " + pms.getName() + " is " + pointDifferentialWeightedScore);
			return pointDifferentialWeightedScore;
		}

		public double getLogarithmSmoothedScore(float totalPointDifferentialWeightedScores) {
			float factor = ((((pointDifferentialWeightedScore /totalPointDifferentialWeightedScores)*500*playersOnField)) + 1);
			if (factor < 0F) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "pointDifferentialWeightedScore too low for : " + pms.getName() + " factor we want to ln is " + factor);
				factor = 1F;
			}
			smoothScore = Math.log(factor);
			return smoothScore;
		}

		public Integer getRating(float totalLogarithmSmoothedScores) {
			Float normalizedSmoothScore = (float) (smoothScore /totalLogarithmSmoothedScores);
			return Math.round(normalizedSmoothScore * 500 * numPlayers);  // we use numPlayers and not playersOnField here so they average out to 500.
		}

	}

	@Override
	public List<IPlayerMatchRating> generate(IMatchRatingEngineSchema schema, IMatchGroup match) {

		// Have to have these weight values available in the schema to work
		assert (schema instanceof IV1EngineWeightValues);
		
		List<PlayerStatShares> pss = new ArrayList<PlayerStatShares>();
		List<IPlayerMatchRating> mrl = new ArrayList<IPlayerMatchRating>();

		// these three we have to total across all the players because they aren't available in the TeamMatchStats
		// like, say, tries or tackles.
		int totalTryAssists = 0;
		int totalLineoutsStolen = 0;
		int totalMinutes = 0;

		for (IPlayerMatchStats pms : homePlayerStats) {
			totalTryAssists += pms.getTryAssists();
			totalLineoutsStolen += pms.getLineoutsStolenOnOppThrow();
			totalMinutes += pms.getTimePlayed();
		}

		for (IPlayerMatchStats pms : visitPlayerStats) {
			totalTryAssists += pms.getTryAssists();
			totalLineoutsStolen += pms.getLineoutsStolenOnOppThrow();
			totalMinutes += pms.getTimePlayed();
		}

		float totalPlayerScore = 0F;

		ITeamMatchStats combined = tmsf.getById(null); //emtpy one
		combined.add(homeTeamStats);
		combined.add(visitTeamStats);
		
		
		for (IPlayerMatchStats pms : homePlayerStats) {
			if (!pms.getPosition().equals(position.NONE)) {
				PlayerStatShares score = new PlayerStatShares((IV1EngineWeightValues)schema, homeTeamStats, combined, pms, match, totalTryAssists, totalLineoutsStolen, totalMinutes, homePlayerStats.size() + visitPlayerStats.size());
				pss.add(score);
				totalPlayerScore += score.getPlayerScore();
			} else {
				// do they get a rating of 0? 
			}
		}

		for (IPlayerMatchStats pms : visitPlayerStats) {
			if (!pms.getPosition().equals(position.NONE)) {
				PlayerStatShares score = new PlayerStatShares((IV1EngineWeightValues)schema, visitTeamStats, combined, pms, match, totalTryAssists, totalLineoutsStolen, totalMinutes, homePlayerStats.size() + visitPlayerStats.size());
				pss.add(score);
				totalPlayerScore += score.getPlayerScore();
			} else {
				// do they get a rating of 0? 
			}
		}

		float totalPointDifferentialWeightedScores = 0F;
		for (PlayerStatShares score : pss) {
			totalPointDifferentialWeightedScores += score.getPointDifferentialWeightedScore(totalPlayerScore);
		}

		float totalLogarithmSmoothedScores = 0F;
		for (PlayerStatShares score : pss) {
			totalLogarithmSmoothedScores += score.getLogarithmSmoothedScore(totalPointDifferentialWeightedScores);
		}

		for (PlayerStatShares score : pss) {
			IPlayerMatchRating pmr = pmrf.getNew(pf.getById(score.getPlayerMatchStats().getPlayerId()), match, score.getRating(totalLogarithmSmoothedScores), schema, score.getPlayerMatchStats());
			pmrf.put(pmr);
			mrl.add(pmr);
		}

		return mrl;
	}

}
