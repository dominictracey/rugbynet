package net.rugby.foundation.admin.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mortbay.log.Log;

import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.Position.position;

public class ScrumMatchRatingEngineV100 extends ScrumMatchRatingEngineV001 implements
IMatchRatingEngine  {

	public ScrumMatchRatingEngineV100(IPlayerFactory pf, IPlayerMatchRatingFactory pmrf) {
		super(pf, pmrf);
	}

	private final Float triesWeight = .2F;
	private final Float tryAssistsWeight = .2F;
	private final Float pointsWeight = .3F;
	private final Float kicksWeight = .05F;
	private final Float passesWeight = .05F;
	private final Float runsWeight = .1F;
	private final Float metersRunWeight = .2F;
	private final Float cleanBreaksWeight = .2F;
	private final Float defendersBeatenWeight = .2F;
	private final Float offloadsWeight = .2F;
	private final Float turnoversWeight = -.2F;
	private final Float tacklesMadeWeight = .3F;
	private final Float tacklesMissedWeight = -.1F;
	private final Float lineoutsWonOnThrowWeight = .2F;
	private final Float lineoutsStolenOnOppThrowWeight = .3F;
	private final Float penaltiesConcededWeight = -.1F;
	private final Float yellowCardsWeight = -.2F;
	private final Float redCardsWeight = -.3F;

	// time-skewed team stats
	private final Float scrumShareWeight = .3F;
	private final Float lineoutShareWeight = .3F;
	private final Float ruckShareWeight = .2F;
	private final Float maulShareWeight = .2F;
	private final Float minutesShareWeight = .2F;
	
	private final Float pointsDifferentialWeight = .3F;

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

		public PlayerStatShares(ITeamMatchStats tms, IPlayerMatchStats pms, IMatchGroup match, int totalTryAssists, int totalLineoutsStolen, int totalMinutes, int numPlayers) {
			assert(tms.getTeamId().equals(pms.getTeamId()));

			Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
			
			this.pms = pms;

			if (!tms.getTries().equals(0)) {
				tries = (float)pms.getTries() / (float)tms.getTries();
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
				kicks = (float) pms.getKicks() / (float)tms.getKicksFromHand();
			} else {
				kicks= 0;
			}			
			if (!tms.getPasses().equals(0)) {
				passes = (float) pms.getPasses() / (float) tms.getPasses();
			} else {
				passes= 0;
			}			
			if (!tms.getRuns().equals(0)) {
				runs = (float) pms.getRuns() /(float) tms.getRuns();
			} else {
				runs= 0;
			}			
			if (!tms.getMetersRun().equals(0)) {
				metersRun = (float) pms.getMetersRun() /(float) tms.getMetersRun();
			} else {
				metersRun= 0;
			}			
			if (!tms.getCleanBreaks().equals(0)) {
				cleanBreaks = (float) pms.getCleanBreaks() /(float) tms.getCleanBreaks();
			} else {
				cleanBreaks= 0;
			}			
			if (!tms.getDefendersBeaten().equals(0)) {
				defendersBeaten = (float) pms.getDefendersBeaten() /(float) tms.getDefendersBeaten();
			} else {
				defendersBeaten= 0;
			}			
			if (!tms.getOffloads().equals(0)) {
				offloads = (float) pms.getOffloads() /(float) tms.getOffloads();
			} else {
				offloads= 0;
			}			
			if (!tms.getTurnoversConceded().equals(0)) {
				turnovers = (float) pms.getTurnovers() /(float) tms.getTurnoversConceded();
			} else {
				turnovers= 0;
			}			
			if (!tms.getTacklesMade().equals(0)) {
				tacklesMade = (float) pms.getTacklesMade() /(float) tms.getTacklesMade();
			} else {
				tacklesMade= 0;
			}			
			if (!tms.getTacklesMissed().equals(0)) {
				tacklesMissed = (float) pms.getTacklesMissed()/(float) tms.getTacklesMissed();
			} else {
				tacklesMissed= 0;
			}			
			if (!tms.getLineoutsWonOnOwnThrow().equals(0)) {
				lineoutsWonOnThrow = (float) pms.getLineoutsWonOnThrow() /(float) tms.getLineoutsWonOnOwnThrow();
			} else {
				lineoutsWonOnThrow= 0;
			}			
			if (totalLineoutsStolen != 0) {
				lineoutsStolenOnOppThrow = (float) pms.getLineoutsStolenOnOppThrow() /(float) totalLineoutsStolen;
			} else {
				lineoutsStolenOnOppThrow= 0;
			}			
			if (!tms.getPenaltiesConceded().equals(0)) {
				penaltiesConceded = (float) pms.getPenaltiesConceded() / (float) tms.getPenaltiesConceded();
			} else {
				penaltiesConceded = 0;
			}			
			if (!tms.getYellowCards().equals(0)) {
				yellowCards = (float) pms.getYellowCards() /(float) tms.getYellowCards();
			} else {
				yellowCards= 0;
			}			
			if (!tms.getRedCards().equals(0)) {
				redCards = (float) pms.getRedCards() /(float) tms.getRedCards();
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
			tries *= triesWeight;                   
			tryAssists *= tryAssistsWeight;              
			points *= pointsWeight;                  
			kicks *= kicksWeight;                   
			passes *= passesWeight;                  
			runs *= runsWeight;                    
			metersRun *= metersRunWeight;               
			cleanBreaks *= cleanBreaksWeight;             
			defendersBeaten *= defendersBeatenWeight;         
			offloads *= offloadsWeight;                
			turnovers *= turnoversWeight;               
			tacklesMade *= tacklesMadeWeight;             
			tacklesMissed *= tacklesMissedWeight;           
			lineoutsWonOnThrow *= lineoutsWonOnThrowWeight;      
			lineoutsStolenOnOppThrow *= lineoutsStolenOnOppThrowWeight;
			penaltiesConceded *= penaltiesConcededWeight;       
			yellowCards *= yellowCardsWeight;             
			redCards *= redCardsWeight;                

			scrumShare *= scrumShareWeight;              
			lineoutShare *= lineoutShareWeight;            
			ruckShare *= ruckShareWeight;               
			maulShare *= maulShareWeight;               
			minutesShare *= minutesShareWeight;            
		}

		private float getBackScore() {
			backScore = (tries + tryAssists + points + kicks + passes + runs + metersRun + cleanBreaks + 
					defendersBeaten + offloads + turnovers + tacklesMade + tacklesMissed + penaltiesConceded +
					yellowCards + redCards + minutesShare) * 500 * playersOnField; // no ruckShare?
							return backScore;
		}

		private float getForwardScore() {
			forwardScore = (lineoutsWonOnThrow + lineoutsStolenOnOppThrow + scrumShare + lineoutShare + ruckShare + maulShare) * 500 * playersOnField;
			return forwardScore;
		}

		private int isForward() {
			if (pms.getPosition().equals(position.PROP) ||
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
			float normalizedPlayerScore = getPlayerScore() / totalPlayerScore;
			float lnPointDiff = (float) Math.log(Math.abs(pointDifferential) + 1);
			if (pointDifferential < 0) {
				lnPointDiff *= -1;
			}
			pointDifferentialWeightedScore = normalizedPlayerScore + (normalizedPlayerScore * pointsDifferentialWeight * lnPointDiff);// (normalizedPlayerScore * 0.2F * pointDifferential);
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

		List<PlayerStatShares> pss = new ArrayList<PlayerStatShares>();
		List<IPlayerMatchRating> mrl = new ArrayList<IPlayerMatchRating>();

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

		for (IPlayerMatchStats pms : homePlayerStats) {
			if (!pms.getPosition().equals(position.NONE)) {
				PlayerStatShares score = new PlayerStatShares(homeTeamStats, pms, match, totalTryAssists, totalLineoutsStolen, totalMinutes, homePlayerStats.size() + visitPlayerStats.size());
				pss.add(score);
				totalPlayerScore += score.getPlayerScore();
			} else {
				// do they get a rating of 0? 
			}
		}

		for (IPlayerMatchStats pms : visitPlayerStats) {
			if (!pms.getPosition().equals(position.NONE)) {
				PlayerStatShares score = new PlayerStatShares(visitTeamStats, pms, match, totalTryAssists, totalLineoutsStolen, totalMinutes, homePlayerStats.size() + visitPlayerStats.size());
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
