package net.rugby.foundation.admin.server.workflow.weekend;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.rules.ICoreRuleFactory;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory.MatchRule;
import net.rugby.foundation.admin.server.rules.IRule;
import net.rugby.foundation.admin.server.workflow.ResultWithLog;
import net.rugby.foundation.admin.server.workflow.RetryRequestException;
import net.rugby.foundation.admin.server.workflow.fetchstats.FetchMatchStats;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS7StatsFetched;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;

import org.joda.time.DateTime;

import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;


public class MJ6FetchStats extends Job3<MS7StatsFetched, Long, String, ResultWithLog> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private IMatchGroupFactory mf;
	transient private ICoreRuleFactory crf;

	public MJ6FetchStats() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}


	@Override
	public Value<MS7StatsFetched> run(Long matchId, String label, ResultWithLog prior) throws RetryRequestException {

		try {

			MS7StatsFetched retval = new MS7StatsFetched();
			
			retval.matchId = matchId;

			// just let failure cascade to the end so it can finish
			if (prior == null || prior.success == false) {
				retval.success = false;
				return immediate(retval);
			}

			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.mf = injector.getInstance(IMatchGroupFactory.class);
			this.crf = injector.getInstance(ICoreRuleFactory.class);

			IMatchGroup match = mf.get(matchId);

			WorkflowStatus fromState = WorkflowStatus.FINAL;
			//WorkflowStatus toState = WorkflowStatus.FETCHED;   // << set in fetchstats.CompileMatchStats
			IRule<IMatchGroup> rule = crf.get(match, MatchRule.MATCH_STATS_AVAILABLE);		

			// first check if we are already further along than this
			if (match.getWorkflowStatus().ordinal() > fromState.ordinal()) {
				retval.log.add("OK");
				retval.success = true;
				return immediate(retval);
			}

			assert (match.getWorkflowStatus().equals(fromState));

			// 			
			if (rule.test()) {
				// update state
				Value<MS7StatsFetched> val = futureCall(new FetchMatchStats(), immediate(match.getId()));
				
				// this happens inside fetchstats.CompileMatchStats
//				match.setWorkflowStatus(toState);
//				mf.put(match);
//				
//				retval.log.add(rule.getLog());
//				retval.log.add(match.getDisplayName() + " fetched stats " + DateTime.now().toString());
				return val;
			} else {
				// if it was more than 3 days ago, give up and mark the match NO_STATS
				DateTime now = new DateTime();
				now.minusDays(3);
				if (now.isAfter(new DateTime(match.getDate()))) {
					match.setWorkflowStatus(WorkflowStatus.NO_STATS);
					mf.put(match);
					retval.log.add("No stats available for " + match.getDisplayName() + " giving up. NO_STATS");
					retval.success = false;
					return immediate(retval);
				} else {
					throw new RetryRequestException(match.getDisplayName() + " still waiting for stats at " + DateTime.now().toString());
				}
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

				MS7StatsFetched retval = new MS7StatsFetched();
				retval.matchId = matchId;
				retval.success = false;
				retval.log.add(ex.getLocalizedMessage());
				return immediate(retval);
			}
		}
	}

}


