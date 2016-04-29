/**
 * 
 */
package net.rugby.foundation.admin.server.rules;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IRound.WorkflowStatus;

/**
 * @author home
 *
 */
public class RuleRoundRated extends CoreRule<IRound> {

	/**
	 * @param t
	 */
	public RuleRoundRated(IRound t) {
		super(t);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.IRule#test()
	 */
	@Override
	public Boolean test() {
		boolean complete = target.getWorkflowStatus() == WorkflowStatus.RATED;
		
		if (complete) {
			Logger.getLogger(RuleRoundRated.class.getName()).log(Level.WARNING,"Round's series have been processed " + target.getName() +"("+ target.getId() + ")");
			log += target.getName() + " has had its series processed.";
		}
		
		return complete;
	}

}
