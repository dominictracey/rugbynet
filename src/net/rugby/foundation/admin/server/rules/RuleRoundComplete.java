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
public class RuleRoundComplete extends CoreRule<IRound> {

	/**
	 * @param t
	 */
	public RuleRoundComplete(IRound t) {
		super(t);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.IRule#test()
	 */
	@Override
	public Boolean test() {
		boolean rated = target.getWorkflowStatus() == WorkflowStatus.COMPLETE;
		
		if (rated) {
			Logger.getLogger(RuleRoundComplete.class.getName()).log(Level.WARNING,"Round is fully processed " + target.getName() +"("+ target.getId() + ")");

		}
		
		return rated;
	}

}
