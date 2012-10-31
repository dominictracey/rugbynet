/**
 * 
 */
package net.rugby.foundation.admin.server.rules;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;

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
		boolean all = true;
		for (IMatchGroup m : target.getMatches()) {
			if (m.getSimpleScoreMatchResult() == null)  {
				all = false;
				break;
			}
		}	
		
		if (all) {
			Logger.getLogger(RuleRoundComplete.class.getName()).log(Level.WARNING,"Round is complete " + target.getName() +"("+ target.getId() + ")");

		}
		
		return all;
	}

}
