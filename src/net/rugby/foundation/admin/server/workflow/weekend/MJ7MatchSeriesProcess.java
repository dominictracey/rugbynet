package net.rugby.foundation.admin.server.workflow.weekend;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory.MatchRule;
import net.rugby.foundation.admin.server.rules.IRule;
import net.rugby.foundation.admin.server.workflow.ResultWithLog;
import net.rugby.foundation.admin.server.workflow.RetryRequestException;
import net.rugby.foundation.admin.server.workflow.ratingseries.ProcessRatingSeries;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS8Rated;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.RatingMode;

import com.google.appengine.tools.pipeline.Job4;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;


public class MJ7MatchSeriesProcess extends Job4<MS8Rated, Long, Long, String, ResultWithLog> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private IMatchGroupFactory mf;
	transient private ICoreRuleFactory crf;

	private ISeriesConfigurationFactory scf;

	private IRoundFactory rf;

	public MJ7MatchSeriesProcess() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}


	@Override
	public Value<MS8Rated> run(Long matchId, Long groupId, String label, ResultWithLog prior) throws RetryRequestException {

		try {

			MS8Rated retval = new MS8Rated();
			retval.targetId = matchId;

			// just let failure cascade to the end so it can finish
			if (prior == null || prior.success == false) {
				retval.log.add(this.getClass().getSimpleName() + " ...FAIL");
				retval.success = false;
				return immediate(retval);
			}

			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.mf = injector.getInstance(IMatchGroupFactory.class);
			this.crf = injector.getInstance(ICoreRuleFactory.class);
			this.scf = injector.getInstance(ISeriesConfigurationFactory.class);
			this.rf = injector.getInstance(IRoundFactory.class);
			
			IMatchGroup match = mf.get(matchId);

			WorkflowStatus fromState = WorkflowStatus.FETCHED;
			//WorkflowStatus toState = WorkflowStatus.RATED;  // << set in ratingseries.CompileProcessReport
			IRule<IMatchGroup> rule = crf.get(match, MatchRule.MATCH_TO_RATE);		

			// first check if we are already further along than this
			if (match.getWorkflowStatus().ordinal() > fromState.ordinal()) {
				retval.log.add(this.getClass().getSimpleName() + " ...OK");
				retval.success = true;
				return immediate(retval);
			}

			assert (match.getWorkflowStatus().equals(fromState));

			// 			
			if (rule.test()) {
				IRound r = rf.get(match.getRoundId());			
				ISeriesConfiguration sc = scf.getByCompAndMode(r.getCompId(), RatingMode.BY_MATCH);
				
				return futureCall(new ProcessRatingSeries(), immediate(sc.getId()), immediate(matchId), immediate(groupId)); 
				// update state
//				match.setWorkflowStatus(toState);
//				mf.put(match);
//				retval.log.add(rule.getLog());
//				retval.log.add(match.getDisplayName() + " BY_MATCH rating complete at " + DateTime.now().toString());
//				return immediate(retval);
			} else {
				// we don't try to re-run if the rule says not to rate
				retval.log.add(rule.getLog());
				retval.success = false;
				return immediate(retval);
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

				MS8Rated retval = new MS8Rated();
				retval.targetId = matchId;
				retval.success = false;
				retval.log.add(ex.getLocalizedMessage());
				return immediate(retval);
			}
		}
	}

}


