/**
 * 
 */
package net.rugby.foundation.admin.server.rules;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;

/**
 * @author DPT
 *
 *	If all the matches have been fetched or marked unreported we may declare this round closed.
 *
 * Note that in the test environment this is called repeatedly from TestCompetitionFactory to get the Next and Prev Round pointers set right.
 */
public class RuleCompIncrementNextRound extends CoreRule<ICompetition> {

	/**
	 * @param t
	 */
	public RuleCompIncrementNextRound(ICompetition t) {
		super(t);
	}

	/** 
	 * 
	 * @see net.rugby.foundation.admin.server.rules.IRule#test()
	 */
	@Override
	public Boolean test() {
		boolean bump = true;
		
		IRound next = target.getNextRound();
		
		// if comp completed next is null
		if (next == null)  {
			return false;
		}
		
		for (IMatchGroup m : next.getMatches()) {
			if (m.getSimpleScoreMatchResult() == null && m.getStatus() != IMatchGroup.Status.UNREPORTED) {
				bump = false;
				break;
			}
		}
		
		if (bump) {
//			Logger.getLogger(RuleCompIncrementNextRound.class.getName()).log(Level.WARNING,"Competition is ready to have round incremented " + target.getLongName() +"("+ target.getId() + ")");
		}
		return bump;
	}

}
