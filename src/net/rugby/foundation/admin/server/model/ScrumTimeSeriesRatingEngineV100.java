/**
 * 
 */
package net.rugby.foundation.admin.server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.shared.IRatingEngineSchema;
import net.rugby.foundation.admin.shared.IV1EngineWeightValues;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IMatchResult.ResultType;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingQuery.Status;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.PlayerRating;
import net.rugby.foundation.model.shared.PlayerRating.RatingComponent;
import net.rugby.foundation.model.shared.SimpleScoreMatchResult;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;



/**
 * @author dominic
 *
 */



public class ScrumTimeSeriesRatingEngineV100 extends ScrumQueryRatingEngineV100 {
	protected class TimeSeriesPlayerStatShares extends PlayerStatShares implements IPlayerStatShares {

		protected float timescale;
		private Integer computedRating;
		private Integer scaledRating;
		public static final float maxAge = 100f;
		
		public TimeSeriesPlayerStatShares(IV1EngineWeightValues weights,
				ITeamMatchStats tms, ITeamMatchStats otms,
				IPlayerMatchStats pms, IMatchGroup match, int numPlayers) {
			super(weights, tms, otms, pms, match, numPlayers);
			
		}

		public float scaleForTime(DateTime origin) {
			LocalDate jorigin = new LocalDate(origin);
			LocalDate jmatch = new LocalDate(match.getDate());
			int days = Days.daysBetween(jorigin, jmatch).getDays();
			if (days < (int)(maxAge*(-1))) {
				timescale = 0f;
				return 0f;
			}
			timescale = (maxAge+((float)days))/maxAge;
			// linear from 0->maxAge days
			return playerScore * timescale;
		}

		public float getTimeScale() {
			return timescale;
		}
		

		
		public RatingComponent getRatingComponent(float totalScores) {
			computedRating = super.getRating(totalScores);
			scaledRating = getRating(totalScores);
			return new RatingComponent(pms.getName() + "(" + pms.getTeamAbbr() + ")\n" + toString()+"\nTime scale factor: " + timescale + "; numRecords: " + numPlayers + " raw score: " + playerScore + " total raw: " + totalScores + "\n", computedRating, scaledRating, backScore, forwardScore, getPlayerScore(), pms.getId(), getMatchLabel(pms));
			
		}
		
		@Override
		public Integer getRating(float totalScores) {
			Float normalizedSmoothScore = (float) (playerScore*timescale/totalScores);
			return Math.round(normalizedSmoothScore * 500 * numPlayers);  // we use numPlayers and not playersOnField here so they average out to 500.
		}
	}

	Map<Long, List<IPlayerStatShares>> playerScoreMap = new HashMap<Long, List<IPlayerStatShares>>();
	protected final Map<Long, String> matchLabelMap = new HashMap<Long, String>();

	private IPlayerRatingFactory prf;
	private IMatchResultFactory mrf;
	/**
	 * @param pf
	 * @param mf
	 * @param pmrf
	 * @param rf
	 * @param sf
	 * @param pmsf
	 * @param tmsf
	 * @param rqf
	 */
	public ScrumTimeSeriesRatingEngineV100(IPlayerFactory pf,
			IMatchGroupFactory mf, IPlayerMatchRatingFactory pmrf,
			IRoundFactory rf, IStandingFactory sf,
			IPlayerMatchStatsFactory pmsf, ITeamMatchStatsFactory tmsf,
			IRatingQueryFactory rqf, ICompetitionFactory cf, 
			IPlayerRatingFactory prf, IMatchResultFactory mrf) {
		super(pf, mf, pmrf, rf, sf, pmsf, tmsf, rqf, cf);

		this.prf = prf;
		this.mrf = mrf;
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINEST);
	}

	protected String getMatchLabel(IPlayerMatchStats pms) {
		if (!matchLabelMap.containsKey(pms.getMatchId())) {
			IMatchGroup m = mf.get(pms.getMatchId());
			rf.setId(m.getRoundId());
			IRound r = rf.getRound();
			int hScore = 0;
			int vScore = 0;
			if (m.getSimpleScoreMatchResultId() != null) {
				IMatchResult mr = mrf.get(m.getSimpleScoreMatchResultId());
				if (mr != null) {
					hScore = ((SimpleScoreMatchResult)mr).getHomeScore();
					vScore = ((SimpleScoreMatchResult)mr).getVisitScore();
				}
			}
			matchLabelMap.put(pms.getMatchId(), m.getHomeTeam().getAbbr() + " " + hScore + " - " + m.getVisitingTeam().getAbbr() + " " + vScore + " R(" + r.getAbbr() + ")");
		}
		
		return matchLabelMap.get(pms.getMatchId());
	}
	
	@Override 
	public List<IPlayerRating> generate(IRatingEngineSchema schema) {

		try {
			Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINEST);
			List<IPlayerStatShares> pss = new ArrayList<IPlayerStatShares>();
			mrl = new ArrayList<IPlayerRating>();

			pss = populate(schema, pss);
			DateTime now = DateTime.now();
			// group the scores by player (since in a time series one player may have multiple matches)
			for (IPlayerStatShares score : pss) {
				if (!playerScoreMap.containsKey(score.getPlayerMatchStats().getPlayerId())) {
					playerScoreMap.put(score.getPlayerMatchStats().getPlayerId(), new ArrayList<IPlayerStatShares>());
				}
				playerScoreMap.get(score.getPlayerMatchStats().getPlayerId()).add(score);

				((TimeSeriesPlayerStatShares)score).scaleForTime(now);
			}

			// for each player, create the rating from the weighted averages of the scores
			float totalscore = 0;
			int numStats = 0;
			for (Long p : playerScoreMap.keySet()) {
				IPlayerRating pr = prf.create();
				pr.setGenerated(DateTime.now().toDate());
				pr.setPlayerId(playerScoreMap.get(p).get(0).getPlayerMatchStats().getPlayerId());
				pr.setQueryId(query.getId());
				pr.setSchemaId(schema.getId());
				pr.setDetails("Aggregated player rating:\n");
				float scores = 0f;
				Float divisor = 0f;
				for (IPlayerStatShares s : playerScoreMap.get(p)) {
					// only use this match if it isn't too old
					float timescale = ((TimeSeriesPlayerStatShares)s).getTimeScale();
					if (timescale > 0f) {
						scores += s.getPlayerScore()*timescale;  // back score [+ forward score]
						divisor += timescale;
						numStats++;
						pr.setDetails(pr.getDetails() + getMatchLabel(s.getPlayerMatchStats()) + "** score: " + s.getPlayerScore() + " timeScale " + timescale + "\n");
					}
					pr.addMatchStatId(s.getPlayerMatchStats().getId());
					pr.addMatchStats(s.getPlayerMatchStats());					
				}
				
				// watch divide by 0!
				if (!divisor.equals(0f)) {
					pr.setRawScore(scores/(divisor+1));
					pr.setDetails(pr.getDetails() + "*****\n** Contributing Scores Sum: " + scores + "\n** Cumulative TimeScale" + divisor + " (+1)\n** Aggregate Raw Score: " + pr.getRawScore() + "\n");

				} else {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE, "Setting raw score to 0 for: " + ((PlayerRating)pr).getMatchStats().get(0).getName() + " time scaling sum is 0 ");
					pr.setRawScore(0f);
				}
				
				// if they don't have any score, don't include them in the query results.
				if (pr.getRawScore() > 0f) {
					prf.put(pr);
					mrl.add(pr);
					totalscore += pr.getRawScore();
				} else {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE, "Dropping player score for: " + ((PlayerRating)pr).getMatchStats().get(0).getName() + " because raw score is 0 ");

				}
			}

			for (IPlayerRating r : mrl) {
				Float normalizedSmoothScore = (float) (r.getRawScore()/totalscore);
				r.setRating(Math.round(normalizedSmoothScore * 500 * mrl.size()));
				r.setDetails(r.getDetails() + "*****\n** Final normalizedSmoothScore (rawscore/totalRawScore): " + normalizedSmoothScore + "\n** Player Rating:" + r.getRating() + "\n");
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Setting Rating for: " + ((PlayerRating)r).getMatchStats().get(0).getName() + " :  normScore=" + normalizedSmoothScore + " rawScore=" + r.getRawScore() + " rating=" + Math.round(normalizedSmoothScore * 500 * mrl.size()));
				// go back and calculate the component ratings as well. For Tom.
				for (IPlayerStatShares s : playerScoreMap.get(r.getPlayerId())) {
					r.addRatingComponent(((TimeSeriesPlayerStatShares)s).getRatingComponent(totalscore));
					//Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Adding Rating Component for: " + ((PlayerRating)r).getMatchStats().get(0).getName() + " :  " + s.toString());
				}
				prf.put(r);
			}

			sendReport();

			// mark query complete
			query.setStatus(Status.COMPLETE);
			rqf.put(query);

			return mrl;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Time series engine threw a rod", e);
			// mark query errored
			query.setStatus(Status.ERROR);
			rqf.put(query);
			return null;
		}
	}

	@Override 
	protected IPlayerStatShares getNewStatShares(IV1EngineWeightValues schema, ITeamMatchStats tms, ITeamMatchStats otms, IPlayerMatchStats pms, IMatchGroup m) {
		return new TimeSeriesPlayerStatShares(schema, tms, otms, pms, m, pmsList.size());
	}

}
