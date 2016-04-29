package net.rugby.foundation.admin.server.workflow.weekend;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory.RoundRule;
import net.rugby.foundation.admin.server.rules.IRule;
import net.rugby.foundation.admin.server.workflow.RetryRequestException;
import net.rugby.foundation.admin.server.workflow.ratingseries.CheckRatingGroup;
import net.rugby.foundation.admin.server.workflow.ratingseries.ProcessRatingSeries;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS0ProcessMatchResult;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS8Rated;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IRound.WorkflowStatus;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.UniversalRound;

import com.google.appengine.tools.pipeline.Job4;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

/**
 * Once the matches have been fetched and rated for the round, we need to process any comp-specific round rating series with the following RatingModes:
 * 		- BY_POSITIOMN
 * 		- BY_TEAM
 * 		- BY_COMP
 * 
 * TODO this is something of a bummer because it breaks the mold of returning a ResultWithLog-derived Value. We return a List<> of them instead so hope that's ok. Sorry.
 * @author dominictracey
 *
 */
public class RJ4RoundSeriesProcess extends Job4<MS8Rated, Long, RatingMode, String, List<MS0ProcessMatchResult>> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private ICoreRuleFactory crf;
	transient private ISeriesConfigurationFactory scf;
	transient private IRoundFactory rf;

	private IUniversalRoundFactory urf;

	public RJ4RoundSeriesProcess() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}


	@Override
	public Value<MS8Rated> run(Long roundId, RatingMode mode, String label, List<MS0ProcessMatchResult> matchResults) throws RetryRequestException {

		try {

			MS8Rated retval = new MS8Rated();
			retval.targetId = roundId;

			// just let failure cascade to the end so it can finish
			//			if (prior == null || prior.success == false) {
			//				retval.success = false;
			//				return immediate(retval);
			//			}

			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.crf = injector.getInstance(ICoreRuleFactory.class);
			this.scf = injector.getInstance(ISeriesConfigurationFactory.class);
			this.rf = injector.getInstance(IRoundFactory.class);

			IRound round = rf.get(roundId);

			// first guy through needs to check if the matchResults are all good and if so, set the WFS to FETCHED
			// TODO this should be refactored into a post-match or pre-round processing job on its own
			if (round.getWorkflowStatus() == WorkflowStatus.PENDING || round.getWorkflowStatus() == null) {
				boolean ok = true;
				for (MS0ProcessMatchResult pmr : matchResults) {
					if (!pmr.success) {
						ok = false;
						break;
					}
				}

				if (ok) {
					round.setWorkflowStatus(WorkflowStatus.FETCHED);
					rf.put(round);
				}
			}

			WorkflowStatus fromState = WorkflowStatus.FETCHED;
			//WorkflowStatus toState = WorkflowStatus.RATED;  
			IRule<IRound> rule = crf.get(round, RoundRule.ROUND_FETCHED);		

			// first check if we are already further along than this
			if (round.getWorkflowStatus().ordinal() > fromState.ordinal()) {
				return immediate(retval);
			}

			assert (round.getWorkflowStatus().equals(fromState));

			// 			
			if (rule.test()) {
				IRound r = rf.get(round.getId());	
				ISeriesConfiguration sc = null;
				this.urf = injector.getInstance(IUniversalRoundFactory.class);
				UniversalRound ur = urf.get(round);


				if (mode == RatingMode.BY_COMP) {
					// kick off a job for the round list (BY_COMP)
					sc = scf.getByCompAndMode(r.getCompId(), RatingMode.BY_COMP);
					if (sc != null) {
						sc.setTargetRound(ur);
						scf.put(sc);

						Value<Long> roundGroupId = null;

						roundGroupId = futureCall(new CheckRatingGroup(), immediate(sc.getId()), immediate(round.getUrOrdinal()), immediate(round.getName()));
						return futureCall(new ProcessRatingSeries(), immediate(sc.getId()), immediate(0L), roundGroupId); 
					}
				} else if (mode == RatingMode.BY_POSITION) {
					sc = scf.getByCompAndMode(r.getCompId(), RatingMode.BY_POSITION);
					if (sc != null) {
						sc.setTargetRound(ur);
						scf.put(sc);
						Value<Long> positionGroupId = null;

						positionGroupId = futureCall(new CheckRatingGroup(), immediate(sc.getId()), immediate(round.getUrOrdinal()), immediate(round.getName()));
						return futureCall(new ProcessRatingSeries(), immediate(sc.getId()), immediate(0L), positionGroupId); 
					}
				} else if (mode == RatingMode.BY_TEAM) {
					sc = scf.getByCompAndMode(r.getCompId(), RatingMode.BY_TEAM);
					if (sc != null) {
						sc.setTargetRound(ur);
						scf.put(sc);
						Value<Long> teamGroupId = null;

						teamGroupId = futureCall(new CheckRatingGroup(), immediate(sc.getId()), immediate(round.getUrOrdinal()), immediate(round.getName()));
						return futureCall(new ProcessRatingSeries(), immediate(sc.getId()), immediate(0L), teamGroupId); 
					}
				}
				//update round state in RJ9CompileRoundLog
				//round.setWorkflowStatus(WorkflowStatus.RATED);
			} else {
				// we don't try to re-run if the rule says not to rate
				retval.success = false;
				retval.log.add("Rule failed in " + rule.getClass().getCanonicalName());
				return immediate(retval);
			}


		} catch (Exception ex) {			
			if (ex instanceof RetryRequestException) {
				throw ex;
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
				if (roundId != null) {
					// error out the round
					if (injector == null) {
						injector = BPMServletContextListener.getInjectorForNonServlets();
					}

					MS8Rated retval = new MS8Rated();
					retval.targetId = roundId;
					retval.success = false;
					retval.log.add(ex.getLocalizedMessage());
					return immediate(retval);
				}	

			}
		}
		MS8Rated retval = new MS8Rated();
		retval.targetId = roundId;
		retval.success = true;
		retval.log.add("Couldn't find a RatingSeries for " + mode + ". There mustn't be one configured for this comp.");
		return immediate(retval);

	}

}


