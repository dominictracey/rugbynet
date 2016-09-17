package net.rugby.foundation.admin.server.workflow.weekend;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.rules.ICoreRuleFactory;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory.MatchRule;
import net.rugby.foundation.admin.server.rules.IRule;
import net.rugby.foundation.admin.server.workflow.RetryRequestException;
import net.rugby.foundation.admin.server.workflow.fetchstats.ESPN2FetchLineups;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS3LineupsAnnounced;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;
import org.joda.time.DateTime;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class MJ2Lineups extends Job2<MS3LineupsAnnounced, Long, String> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private IMatchGroupFactory mf;
	transient private ICompetitionFactory cf;
	transient private ICoreRuleFactory crf;

	public MJ2Lineups() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}

	
	@Override
	public Value<MS3LineupsAnnounced> run(Long matchId, String label) throws RetryRequestException {

		try {
			
			// this is currently the first child job, so no need to check prior - if we add jobs before this, be sure to add in code here:
//			MS3LineupsAnnounced retval = new MS3LineupsAnnounced();
//			retval.matchId = matchId;
//
//			// just let failure cascade to the end so it can finish
//			if (prior == null || prior.success == false) {
//				retval.success = false;
//				return immediate(retval);
//			}
			
			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.mf = injector.getInstance(IMatchGroupFactory.class);
			this.crf = injector.getInstance(ICoreRuleFactory.class);

			
			IMatchGroup match = mf.get(matchId);

			
			WorkflowStatus fromState = WorkflowStatus.PENDING;
//			WorkflowStatus toState = WorkflowStatus.LINEUPS;
			IRule<IMatchGroup> rule = crf.get(match, MatchRule.LINEUPS_AVAILABLE);		
			
			MS3LineupsAnnounced retval = new MS3LineupsAnnounced();

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
				Value<MS3LineupsAnnounced> val = futureCall(new ESPN2FetchLineups(), immediate(match.getId()));
				retval.log.add(rule.getLog());
				retval.log.add("Fetch match stats initiated at " + DateTime.now().toString());
				// this happens inside fetchstats.CompileLineups
//				match.setWorkflowStatus(toState);
//				mf.put(match);
//				
//				retval.log.add(rule.getLog());
//				retval.log.add(match.getDisplayName() + " fetched stats " + DateTime.now().toString());
				return val;
			} else {
				// if it was more than 3 days ago, give up and mark the match NO_STATS
				DateTime now = new DateTime();
				now = now.minusDays(3);
				if (now.isAfter(new DateTime(match.getDate()))) {
					match.setWorkflowStatus(WorkflowStatus.NO_STATS);
					mf.put(match);
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "No stats available for " + match.getDisplayName() + " giving up. NO_STATS");
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

				MS3LineupsAnnounced retval = new MS3LineupsAnnounced();
				retval.matchId = matchId;
				retval.success = false;
				retval.log.add(ex.getLocalizedMessage());
				return immediate(retval);
			}
		}

	}

}
