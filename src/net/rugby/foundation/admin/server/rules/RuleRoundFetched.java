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
public class RuleRoundFetched extends CoreRule<IRound> {

	/**
	 * @param t
	 */
	public RuleRoundFetched(IRound t) {
		super(t);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.IRule#test()
	 */
	@Override
	public Boolean test() {
		boolean rated = target.getWorkflowStatus() == WorkflowStatus.FETCHED;
		
		if (rated) {
			log += (target.getName() + " is fully fetched.");
			Logger.getLogger(RuleRoundFetched.class.getName()).log(Level.WARNING,"Round is fully fetched " + target.getName() +"("+ target.getId() + ")");

		}
		
		return rated;
	}

}
