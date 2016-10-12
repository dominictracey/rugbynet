package net.rugby.foundation.admin.server.workflow.weekend;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.rules.ICoreRuleFactory;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory.MatchRule;
import net.rugby.foundation.admin.server.rules.IRule;
import net.rugby.foundation.admin.server.workflow.ResultWithLog;
import net.rugby.foundation.admin.server.workflow.RetryRequestException;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS4Underway;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;

import org.joda.time.DateTime;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class MJ3StartMatch extends Job3<MS4Underway, Long, String, ResultWithLog> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private IMatchGroupFactory mf;
	transient private ICoreRuleFactory crf;

	public MJ3StartMatch() {
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.INFO);
	}

	
	@Override
	public Value<MS4Underway> run(Long matchId, String label, ResultWithLog prior) throws RetryRequestException {

		try {
			
			MS4Underway retval = new MS4Underway();
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
			
			if (match != null) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, this.getJobDisplayName() + ": checking for match start for " + match.getDisplayName());
			}
			
			WorkflowStatus fromState = WorkflowStatus.LINEUPS;
			WorkflowStatus toState = WorkflowStatus.UNDERWAY;
			IRule<IMatchGroup> rule = crf.get(match, MatchRule.MATCH_TO_LOCK);		
			
			retval.matchId = matchId;
			
			// first check if we are already further along than this
			if (match.getWorkflowStatus().ordinal() > fromState.ordinal()) {
				retval.log.add(this.getClass().getSimpleName() + " ...OK");
				retval.success = true;
				return immediate(retval);
			}
			
			assert (match.getWorkflowStatus().equals(fromState));
			
			// 			
			if (rule.test()) {
				// update state
				match.setLocked(true);
				match.setWorkflowStatus(toState);
				mf.put(match);
				retval.log.add(rule.getLog());
				retval.log.add(match.getDisplayName() + " locked at " + DateTime.now().toString());
				retval.success = true;
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, this.getJobDisplayName() + ": match has started: " + match.getDisplayName());

				return immediate(retval);
			} else {
				throw new RetryRequestException(match.getDisplayName() + " not started at " + DateTime.now().toString() + ". Waiting for kickoff at " + match.getDate().toString());
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

				MS4Underway retval = new MS4Underway();
				retval.matchId = matchId;
				retval.success = false;
				retval.log.add(ex.getLocalizedMessage());
				return immediate(retval);
			}
		}

	}

}
