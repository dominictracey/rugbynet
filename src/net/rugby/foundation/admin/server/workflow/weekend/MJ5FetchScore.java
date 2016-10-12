package net.rugby.foundation.admin.server.workflow.weekend;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.admin.server.model.IResultFetcher;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory.MatchRule;
import net.rugby.foundation.admin.server.rules.IRule;
import net.rugby.foundation.admin.server.workflow.ResultWithLog;
import net.rugby.foundation.admin.server.workflow.RetryRequestException;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS6Final;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;
import net.rugby.foundation.model.shared.IMatchResult.ResultType;
import net.rugby.foundation.model.shared.IRound;

import org.joda.time.DateTime;

import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;


public class MJ5FetchScore extends Job3<MS6Final, Long, String, ResultWithLog> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private IMatchGroupFactory mf;
	transient private ICoreRuleFactory crf;
	transient private IResultFetcherFactory srff;

	private IRoundFactory rf;

	public MJ5FetchScore() {
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.INFO);
	}


	@Override
	public Value<MS6Final> run(Long matchId, String label, ResultWithLog prior) throws RetryRequestException {

		try {

			MS6Final retval = new MS6Final();
			retval.matchId = matchId;

			// just let failure cascade to the end so it can finish
			if (prior == null || prior.success == false) {
				retval.log.add(this.getClass().getSimpleName() + " ...FAIL");
				retval.success = false;
				return immediate(retval);
			}

			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.rf = injector.getInstance(IRoundFactory.class);
			this.mf = injector.getInstance(IMatchGroupFactory.class);
			this.srff = injector.getInstance(IResultFetcherFactory.class);
			this.crf = injector.getInstance(ICoreRuleFactory.class);
			
			IMatchGroup match = mf.get(matchId);

			if (match != null) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, this.getJobDisplayName() + ": checking for match result for " + match.getDisplayName());
			}
			
			IRound r = rf.get(match.getRoundId());
			
			WorkflowStatus fromState = WorkflowStatus.OVER;
			WorkflowStatus toState = WorkflowStatus.FINAL;
			IRule<IMatchGroup> rule = crf.get(match, MatchRule.MATCH_TO_FINAL);		

			// first check if we are already further along than this
			if (match.getWorkflowStatus().ordinal() > fromState.ordinal()) {
				retval.log.add(this.getClass().getSimpleName() + " ...OK\n");
				retval.success = true;
				return immediate(retval);
			}

			assert (match.getWorkflowStatus().equals(fromState));

			// 			
			if (rule.test()) {
				// update state
				IResultFetcher fetcher = srff.getResultFetcher(r.getCompId(), null, ResultType.MATCHES);
				fetcher.getResult(match);
				
				match.setWorkflowStatus(toState);
				mf.put(match);
				retval.log.add(rule.getLog());
				retval.log.add(match.getDisplayName() + " final score " + match.getSimpleScoreMatchResult().getHomeScore() + "-" + match.getSimpleScoreMatchResult().getVisitScore() + " collected at " + DateTime.now().toString());
				retval.success = true;
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, this.getJobDisplayName() + ": fetched match result for " + match.getDisplayName());

				return immediate(retval);
			} else {
				throw new RetryRequestException("Still no score available for " + match.getDisplayName() + " at " + DateTime.now().toString());
			}

		} catch (Exception ex) {			
			if (ex instanceof RetryRequestException) {
				throw ex;
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
				if (matchId != null) {
					// error out the match
					if (injector == null) {
						injector = BPMServletContextListener.getInjectorForNonServlets();
					}

					this.mf = injector.getInstance(IMatchGroupFactory.class);
					IMatchGroup match = mf.get(matchId);
					match.setWorkflowStatus(WorkflowStatus.ERROR);
					mf.put(match);
				}	

				MS6Final retval = new MS6Final();
				retval.matchId = matchId;
				retval.success = false;
				retval.log.add(ex.getLocalizedMessage());
				return immediate(retval);
			}
		}
	}

}


