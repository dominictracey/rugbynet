package net.rugby.foundation.admin.server.workflow.weekend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.IAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.server.workflow.ratingseries.CheckRatingGroup;
import net.rugby.foundation.admin.server.workflow.ratingseries.UpdateGraphRoundNodes;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS0ProcessMatchResult;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS8Rated;
import net.rugby.foundation.admin.server.workflow.weekend.results.R0ProcessRoundResult;
import net.rugby.foundation.admin.server.workflow.weekend.results.RS3StandingsResult;
import net.rugby.foundation.admin.server.workflow.weekend.results.RS5UpdateNextAndPreviousRoundsResult;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.RatingMode;

import com.google.appengine.tools.pipeline.FutureList;
import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class RJ0ProcessRound extends Job1<R0ProcessRoundResult, Long> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private IRoundFactory rf;
	transient private IRound round;
	transient private ISeriesConfigurationFactory scf;

	private IAdminTaskFactory atf;

	public RJ0ProcessRound() {
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.INFO);
	}

	
	@Override
	public Value<R0ProcessRoundResult> run(Long roundId) {

		try {
			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.rf = injector.getInstance(IRoundFactory.class);
			this.scf = injector.getInstance(ISeriesConfigurationFactory.class);
			this.atf = injector.getInstance(IAdminTaskFactory.class);
			
			// valid round?
			round = rf.get(roundId);
			if (round == null) {
				R0ProcessRoundResult result = new R0ProcessRoundResult();
				result.roundId = roundId;
				result.log.add("No round could be found matching roundId " + roundId);
				result.success = false;
				return immediate(result);
			}
			
			List<Value<MS0ProcessMatchResult>> _matchResults = new ArrayList<Value<MS0ProcessMatchResult>>();
			FutureList<MS0ProcessMatchResult> matchResults = new FutureList<MS0ProcessMatchResult>(_matchResults);
			
			ISeriesConfiguration sc = scf.getByCompAndMode(round.getCompId(), RatingMode.BY_MATCH);
			Value<Long> groupId = null;
			if (sc != null) {
				groupId = futureCall(new CheckRatingGroup(), immediate(sc.getId()), immediate(round.getUrOrdinal()), immediate(round.getName()));
			} else {
				R0ProcessRoundResult result = new R0ProcessRoundResult();
				result.roundId = roundId;
				result.log.add("No BY_MATCH rating series could be found matching roundId " + roundId);
				result.success = false;
				return immediate(result);
			}
			
			JobSetting backOffFactor = new JobSetting.BackoffFactor(1);
			JobSetting backOffSeconds = new JobSetting.BackoffSeconds(10*60); // retry every 10 minutes
			JobSetting maxAttempts = new JobSetting.MaxAttempts(600); // about 4 days
			
			// kick off a ProcessMatch job for each match
			for (IMatchGroup m : round.getMatches()) {
				_matchResults.add(futureCall(new MJ0ProcessMatch(), immediate(m.getId()), groupId, immediate(m.getDisplayName()),  backOffFactor, backOffSeconds, maxAttempts));
			}
			
			List<Value<MS8Rated>> seriesResults = new ArrayList<Value<MS8Rated>>();
			//Value<MS8Rated> roundSeriesResults = 
			seriesResults.add(futureCall(new RJ4RoundSeriesProcess(), immediate(roundId), immediate(RatingMode.BY_COMP), immediate(round.getName()),  matchResults));
			Value<MS8Rated> positionSeriesOutput = futureCall(new RJ4RoundSeriesProcess(), immediate(roundId), immediate(RatingMode.BY_POSITION), immediate(round.getName()), matchResults);
			seriesResults.add(positionSeriesOutput);
			seriesResults.add(futureCall(new RJ4RoundSeriesProcess(), immediate(roundId), immediate(RatingMode.BY_TEAM), immediate(round.getName()), matchResults));
			
			// once the position series is updated, also update the graphs
			FutureValue<List<String>> deleteOldGraphOutput = futureCall(new UpdateGraphRoundNodes(), immediate(round.getCompId()), immediate("delete"), immediate("delete old position graphs for " + round.getName()), waitFor(positionSeriesOutput));
			FutureValue<List<String>> updateGraphOutput = futureCall(new UpdateGraphRoundNodes(), immediate(round.getCompId()), immediate("create"), immediate("create new position graph for " + round.getName()), waitFor(deleteOldGraphOutput));

			IRound r = null;
			// get the standings for the next round when they are done
			// might have to skip some real world weeks to get to the next comp round 
			for (int i = 1; i < 15; ++i) {
				r = rf.getForUR(round.getCompId(), round.getUrOrdinal()+i);
				if (r != null) {
					break;
				}
			}
			
			Value<RS3StandingsResult> standingsResult = null;
			
			if (r != null) {
				standingsResult = futureCall(new RJ2FetchStandings(), immediate(r.getId()), matchResults, futureList(seriesResults));
				
			} else {
				RS3StandingsResult sr = new RS3StandingsResult();
				sr.log.add("Couldn't find next round.");
				sr.roundId = roundId;
				sr.success = false;
				standingsResult = immediate(sr);
			}
			
			Value<RS5UpdateNextAndPreviousRoundsResult> nextAndPrevResult = null;
			nextAndPrevResult = futureCall(new RJ5UpdateNextAndPreviousRounds(), immediate(round.getId()), waitFor(standingsResult));
			
			// compile the log to email to the admins in the WeekendFinalizeServlet
			FutureValue<R0ProcessRoundResult> retval = futureCall(new RJ9CompileRoundLog(), immediate(roundId), matchResults, futureList(seriesResults), standingsResult, nextAndPrevResult, updateGraphOutput);
			
			return retval;
			
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			return null;
		}

	}

}
