package net.rugby.foundation.admin.server.workflow.weekend;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.rules.ICoreRuleFactory;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory.MatchRule;
import net.rugby.foundation.admin.server.rules.IRule;
import net.rugby.foundation.admin.server.workflow.ResultWithLog;
import net.rugby.foundation.admin.server.workflow.RetryRequestException;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS9Promoted;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlaceFactory;
import net.rugby.foundation.core.server.promote.IPromoter;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IServerPlace;

import org.joda.time.DateTime;

import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;


public class MJ8PromoteMatch extends Job3<MS9Promoted, Long, String, ResultWithLog> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private IMatchGroupFactory mf;
	transient private ICoreRuleFactory crf;
	transient private IPlaceFactory spf;
	transient private IPromoter promoter;

	public MJ8PromoteMatch() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}


	@Override
	public Value<MS9Promoted> run(Long matchId, String label, ResultWithLog prior) throws RetryRequestException {

		try {

			MS9Promoted retval = new MS9Promoted();
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

			this.mf = injector.getInstance(IMatchGroupFactory.class);
			this.crf = injector.getInstance(ICoreRuleFactory.class);
			this.spf = injector.getInstance(IPlaceFactory.class);
			this.promoter = injector.getInstance(IPromoter.class);
			
			IMatchGroup match = mf.get(matchId);

			WorkflowStatus fromState = WorkflowStatus.RATED;
			WorkflowStatus toState = WorkflowStatus.PROMOTED;
			IRule<IMatchGroup> rule = crf.get(match, MatchRule.MATCH_TO_PROMOTE);		

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
				IServerPlace place = spf.getForGuid(match.getGuid());
				if (place != null && place.getListId() != null) {
					List<IPlayer> result = promoter.promoteList(place.getListId());
					
					//TODO Create admin tasks for players without twitter handles. Should probably implement an IPlayer.hasTwitterHandle field first
					
					for (IPlayer p : result) {
						if (p.getTwitterHandle() != null && !p.getTwitterHandle().isEmpty()) {
							retval.log.add("Match List tweet to " + p.getDisplayName());
						} else {
							retval.log.add("Need twitter handle for " + p.getDisplayName());
						}
					}
				}
				
				match.setWorkflowStatus(toState);
				mf.put(match);
				retval.log.add(rule.getLog());
				retval.log.add(match.getDisplayName() + " promotion complete at " + DateTime.now().toString());
				retval.success = true;
				return immediate(retval);
			} else {
				throw new RetryRequestException(match.getDisplayName() + " still underway at " + DateTime.now().toString());
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

				MS9Promoted retval = new MS9Promoted();
				retval.matchId = matchId;
				retval.success = false;
				retval.log.add(ex.getLocalizedMessage());
				return immediate(retval);
			}
		}
	}

}


