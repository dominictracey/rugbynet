package net.rugby.foundation.admin.server.workflow.midweek;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory;
import net.rugby.foundation.admin.server.rules.IRule;
import net.rugby.foundation.admin.server.workflow.RetryRequestException;
import net.rugby.foundation.admin.server.workflow.ratingseries.CheckRatingGroup;
import net.rugby.foundation.admin.server.workflow.ratingseries.ProcessRatingSeries;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS8Rated;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.UniversalRound;

import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

public class VJ2ProcessVirtualCompSeries extends Job3<MS8Rated, Long, RatingMode, String> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private ICoreRuleFactory crf;
	transient private ISeriesConfigurationFactory scf;
	transient private IUniversalRoundFactory urf;

	public VJ2ProcessVirtualCompSeries() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}


	@Override
	public Value<MS8Rated> run(Long compId, RatingMode mode, String label) throws RetryRequestException {

		try {

			MS8Rated retval = new MS8Rated();
			retval.targetId = compId;
			retval.log.add("**" + label + " " + mode.name() + "**");
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

			ISeriesConfiguration sc = null;
			if (mode == RatingMode.BY_COMP) {
				// kick off a job for the round list (BY_COMP)
				sc = scf.getByCompAndMode(compId, RatingMode.BY_COMP);
			} else if (mode == RatingMode.BY_POSITION) {
				sc = scf.getByCompAndMode(compId, RatingMode.BY_POSITION);
			} else if (mode == RatingMode.BY_TEAM) {	
				sc = scf.getByCompAndMode(compId, RatingMode.BY_TEAM);
			}
			
			if (sc != null) {
				IRule<ISeriesConfiguration> rule = crf.get(sc, ICoreRuleFactory.SeriesConfigurationRule.READY_TO_PROCESS);		
				
				if (rule.test()) {	
	
					this.urf = injector.getInstance(IUniversalRoundFactory.class);
					UniversalRound ur = urf.getCurrent();
	
					if (sc != null) {
						sc.setTargetRound(ur);
						scf.put(sc);
	
						Value<Long> roundGroupId = null;
						roundGroupId = futureCall(new CheckRatingGroup(), immediate(sc.getId()), immediate(ur.ordinal), immediate(ur.shortDesc));
						
						return futureCall(new ProcessRatingSeries(), immediate(sc.getId()), immediate(0L), roundGroupId); 
					}
					
					//update round state in RJ9CompileRoundLog
					//round.setWorkflowStatus(WorkflowStatus.RATED);
				} else {
					// we don't try to re-run if the rule says not to rate
					retval.success = false;
					retval.log.add(rule.getLog());
					retval.log.add("Rule failed in " + rule.getClass().getCanonicalName());
					return immediate(retval);
				}
			}

		} catch (Exception ex) {			
			if (ex instanceof RetryRequestException) {
				throw ex;
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
				if (compId != null) {
					// error out the round
					if (injector == null) {
						injector = BPMServletContextListener.getInjectorForNonServlets();
					}

					MS8Rated retval = new MS8Rated();
					retval.targetId = compId;
					retval.success = false;
					retval.log.add(ex.getLocalizedMessage());
					return immediate(retval);
				}	

			}
		}
		MS8Rated retval = new MS8Rated();
		retval.targetId = compId;
		retval.success = true;

		retval.log.add("Couldn't find a RatingSeries for " + mode + ". There mustn't be one configured for this comp.");
		return immediate(retval);

	}

}


