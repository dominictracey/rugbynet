/**
 * 
 */
package net.rugby.foundation.admin.server.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.shared.IRatingEngineSchema;
import net.rugby.foundation.admin.shared.IV1EngineWeightValues;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingQuery.Status;
import net.rugby.foundation.model.shared.ITeamMatchStats;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;



/**
 * @author dominic
 *
 */



public class ScrumTimeSeriesRatingEngineV100 extends ScrumQueryRatingEngineV100 {
	protected class TimeSeriesPlayerStatShares extends PlayerStatShares implements IPlayerStatShares {

		private float timescale;
		public static final float maxAge = 100f;

		public TimeSeriesPlayerStatShares(IV1EngineWeightValues weights,
				ITeamMatchStats tms, ITeamMatchStats otms,
				IPlayerMatchStats pms, IMatchGroup match, int numPlayers) {
			super(weights, tms, otms, pms, match, numPlayers);

		}

		public void scaleForTime(DateTime origin) {
			LocalDate jorigin = new LocalDate(origin);
			LocalDate jmatch = new LocalDate(match.getDate());
			int days = Days.daysBetween(jorigin, jmatch).getDays();
			timescale = (maxAge+((float)days))/maxAge;
			// linear from 0->maxAge days
			playerScore *= timescale;
		}

		public float getTimeScale() {
			return timescale;
		}
	}

	Map<Long, List<IPlayerStatShares>> playerScoreMap = new HashMap<Long, List<IPlayerStatShares>>();
	private IPlayerRatingFactory prf;
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
			IRatingQueryFactory rqf, ICompetitionFactory cf, IPlayerRatingFactory prf) {
		super(pf, mf, pmrf, rf, sf, pmsf, tmsf, rqf, cf);

		this.prf = prf;
	}

	@Override 
	public List<IPlayerRating> generate(IRatingEngineSchema schema) {

		try {

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

			// now create the rating from the weighted averages of the scores
			float totalscore = 0;
			int numStats = 0;
			for (Long p : playerScoreMap.keySet()) {
				IPlayerRating pr = prf.create();
				pr.setGenerated(DateTime.now().toDate());
				pr.setPlayerId(playerScoreMap.get(p).get(0).getPlayerMatchStats().getPlayerId());
				pr.setQueryId(query.getId());
				pr.setSchemaId(schema.getId());

				float scores = 0f;
				float divisor = 0f;
				for (IPlayerStatShares s : playerScoreMap.get(p)) {
					scores += s.getPlayerScore();
					divisor += ((TimeSeriesPlayerStatShares)s).getTimeScale();
					pr.addMatchStatId(s.getPlayerMatchStats().getId());
					pr.addMatchStats(s.getPlayerMatchStats());
					pr.setDetails(pr.getDetails()+"(o)(o)\n"+s.toString());
					numStats++;
				}
				prf.put(pr);
				mrl.add(pr);
				pr.setRawScore(scores/divisor);
				totalscore += pr.getRawScore();
			}

			for (IPlayerRating r : mrl) {
				Float normalizedSmoothScore = (float) (r.getRawScore()/totalscore);
				r.setRating(Math.round(normalizedSmoothScore * 500 * mrl.size()));
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
