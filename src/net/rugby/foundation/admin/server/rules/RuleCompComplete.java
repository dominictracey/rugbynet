/**
 * 
 */
package net.rugby.foundation.admin.server.rules;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;

/**
 * @author home
 *
 */
public class RuleCompComplete extends CoreRule<ICompetition> {

	/**
	 * @param t
	 */
	public RuleCompComplete(ICompetition t) {
		super(t);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.IRule#test()
	 */
	@Override
	public Boolean test() {
		boolean complete = false;
		if (target.getNextRoundIndex() == target.getRounds().size()-1) {
			complete = true;
			for (IMatchGroup m : target.getNextRound().getMatches()) {
				if (m.getSimpleScoreMatchResult() == null)  {
					complete = false;
					break;
				}
			}		
		}
		
		if (complete) {
			Logger.getLogger(RuleCompComplete.class.getName()).log(Level.WARNING,"Competition is complete " + target.getLongName() +"("+ target.getId() + ")");

		}
		return complete;
	}

}
