package net.rugby.foundation.admin.server.workflow.weekend;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import net.rugby.foundation.admin.server.workflow.weekend.results.MS4Underway;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS5Over;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS6Final;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS7StatsFetched;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS8Rated;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS9Promoted;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS0ProcessMatchResult;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS3LineupsAnnounced;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class MJ0ProcessMatch extends Job3<MS0ProcessMatchResult, Long, Long, String> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private IMatchGroupFactory mf;
	transient private IMatchGroup match;

	public MJ0ProcessMatch() {
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.INFO);
	}


	@Override
	public Value<MS0ProcessMatchResult> run(Long matchId, Long ratingGroupId, String matchName) throws Exception {

		try {
			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.mf = injector.getInstance(IMatchGroupFactory.class);
			
			MS0ProcessMatchResult result = new MS0ProcessMatchResult();

			// valid round?
			match = mf.get(matchId);
			if (match == null) {
				result.matchId = matchId;
				result.log.add("No match could be found matching matchId " + matchId);
				return immediate(result);
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, this.getJobDisplayName() + ": Starting processing for match " + match.getDisplayName());
			}

			String label = match.getDisplayName();

			// we have two types of retry schemes:
			//		- the early steps (currently < MJ7) need to be retried until they succeed or we give up
			//		- the later steps (currently > MJ6) can just try a few times and bail, it probably won't resolve itself.
			
			// wait for retry
			JobSetting waitBackOffFactor = new JobSetting.BackoffFactor(1);
			JobSetting waitBackOffSeconds = new JobSetting.BackoffSeconds(30*60); // retry every 30 minutes
			JobSetting waitMaxAttempts = new JobSetting.MaxAttempts(200); // about 4 days

			// done or fail
			JobSetting nowBackOffFactor = new JobSetting.BackoffFactor(2);
			JobSetting nowBackOffSeconds = new JobSetting.BackoffSeconds(10); // retry at 10, 200 and 4000 seconds?
			JobSetting nowMaxAttempts = new JobSetting.MaxAttempts(7); // 

			// make sure we start at PENDING
			if (match.getWorkflowStatus() == null || match.getWorkflowStatus().ordinal() < WorkflowStatus.PENDING.ordinal()) {
				match.setWorkflowStatus(WorkflowStatus.PENDING);
				mf.put(match);
			}
			
			// if the match is in the past, there is no point in waiting a half hour between retries...
			if (match.getDate().before(DateTime.now().toDate())) {
				waitBackOffFactor = nowBackOffFactor;
				waitBackOffSeconds = nowBackOffSeconds;
				waitMaxAttempts = nowMaxAttempts;
			}
			
			// start the match
			Value<MS3LineupsAnnounced> lineups = futureCall(new MJ2Lineups(), immediate(matchId), immediate(label), waitBackOffFactor, waitBackOffSeconds, waitMaxAttempts);
			
			// start the match
			Value<MS4Underway> underway = futureCall(new MJ3StartMatch(), immediate(matchId), immediate(label), lineups, waitBackOffFactor, waitBackOffSeconds, waitMaxAttempts);
			
			// end the match
			Value<MS5Over> over = futureCall(new MJ4EndMatch(), immediate(matchId), immediate(label), underway, waitBackOffFactor, waitBackOffSeconds, waitMaxAttempts);
			
			// get the score
			Value<MS6Final> finalized = futureCall(new MJ5FetchScore(), immediate(matchId), immediate(label), over, waitBackOffFactor, waitBackOffSeconds, waitMaxAttempts);
			
			// fetch stats
			Value<MS7StatsFetched> fetched = futureCall(new MJ6FetchStats(), immediate(matchId), immediate(label), finalized, waitBackOffFactor, waitBackOffSeconds, waitMaxAttempts);
			
			// run the BY_MATCH series
			Value<MS8Rated> rated = futureCall(new MJ7MatchSeriesProcess(), immediate(matchId), immediate(ratingGroupId), immediate(label), fetched, nowBackOffFactor, nowBackOffSeconds, nowMaxAttempts);
			
			// promote
			Value<MS9Promoted> promoted = futureCall(new MJ8PromoteMatch(), immediate(matchId), immediate(label), rated, nowBackOffFactor, nowBackOffSeconds, nowMaxAttempts);

			// finalization
			FutureValue<MS0ProcessMatchResult> retval = futureCall(new MJ9CompileMatchLog(), underway, over, finalized, fetched, rated, promoted);

			return retval;

		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);

			// rethrow so we get to retry
			throw ex;
		}

	}

}
