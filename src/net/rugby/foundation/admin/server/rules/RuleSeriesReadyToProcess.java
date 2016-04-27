package net.rugby.foundation.admin.server.rules;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IRound.WorkflowStatus;
import net.rugby.foundation.model.shared.UniversalRound;
/**
 * 
 * return true if all of the of the component comps for this seriesConfiguration have their last round before the next universalRound (i.e. current UR or before)
 * 	in a IRound.Workflow.FETCHED or better state (but not Workflow.ERROR).
 *
 * @author dominictracey
 *
 */
public class RuleSeriesReadyToProcess extends CoreRule<ISeriesConfiguration >implements IRule<ISeriesConfiguration> {

	private IUniversalRoundFactory urf;
	private IRoundFactory rf;
	private IConfigurationFactory ccf;

	public RuleSeriesReadyToProcess(ISeriesConfiguration t, IUniversalRoundFactory urf, IRoundFactory rf, IConfigurationFactory ccf) {
		super(t);

		this.urf = urf;
		this.rf = rf;
		this.ccf = ccf;
	}

	@Override
	public Boolean test() {
		try {
		boolean ready = true;
		if (target != null) {
		
			for (Long compId : target.getCompIds()) {
				log += "Checking " + compId + "</br>";
				if (!isReady(compId)) {					
					ready = false;
					break;
				}
			}
			
			if (ready) {
				log += (target.getDisplayName()+ " is ready to be processed.</br>");
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,target.getDisplayName() + " is ready to have its series processed.<br/>");
	
			}
		}		
		return ready;
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, target.getDisplayName() + " has errored out.<br/>", e);
			return false;
		}
	}

	private boolean isReady(Long compId) {
		boolean retval = false; 

		UniversalRound now = urf.getCurrent();
		int ordinal = now.ordinal - 1;  //somehow this seemed one ahead
		int counter = 0;
		
		boolean found = false;
		IRound r = null;
		while (!found && counter < 52) {
			//log += "   ... checking UR ordinal " + ordinal;
			r = rf.getForUR(compId, ordinal);
			if (r != null) {
				found = true;
				//log += " FOUND <br/>";
				break;
			} else {
				//log += " NOT FOUND <br/>";
			}
			ordinal--;
			counter++;
		}
		
		if (found) {
			if (r.getWorkflowStatus() == null || (r.getWorkflowStatus().ordinal() > WorkflowStatus.PENDING.ordinal() && r.getWorkflowStatus() != WorkflowStatus.ERROR)) {
				retval = true;
			} else {
				// if the comp ended more than two months ago, just ignore the WorkflowStatus as it is recent work
				if (counter > 8) {
					log += ccf.get().getCompetitionMap().get(compId) + " has no rounds in the last two months so proceed.<br/>";
					retval = true;
				}
			}
		} else {
			// didn't find a round in the last year, must be old so just let it go through. 
			log += ccf.get().getCompetitionMap().get(compId) + " has no rounds in the last year so is irrelevant.<br/>";
			retval = true;
		}
		
		if (!retval) {
			String roundNR = " Bad round is ";
			if (r != null) {
				roundNR += r.getName() + " with status " + r.getWorkflowStatus().name();
			} else {
				roundNR += "null";
			}
			log += ccf.get().getCompetitionMap().get(compId) + " is not ready to be processed. " + roundNR + "<br/>";
		}
		return retval;
	}

}
