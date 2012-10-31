/**
 * 
 */
package net.rugby.foundation.admin.server.rules;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.model.shared.IMatchGroup;

/**
 * @author home
 *
 */
public class RuleMatchStaleMarkUnreported extends CoreRule<IMatchGroup> {
	
	//number of hours after a match starts when we should give up on trying to get a score
	private static final int STALE_MARK_UNREPORTED_OFFSET = 24; 

	/**
	 * @param t
	 */
	public RuleMatchStaleMarkUnreported(IMatchGroup t) {
		super(t);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.IRule#test()
	 */
	@Override
	public Boolean test() {
		boolean stale = false;
		Calendar cal = new GregorianCalendar();
		Date now = new Date();

		cal.setTime(target.getDate());
		cal.add(Calendar.HOUR, STALE_MARK_UNREPORTED_OFFSET);
 
		if (cal.getTime().before(now) && target.getSimpleScoreMatchResult() == null) {
			stale = true;
			Logger.getLogger(RuleMatchStaleMarkUnreported.class.getName()).log(Level.WARNING,"Should give up on finding result for match " + target.getDisplayName() +"("+ target.getId() + ")");
		}

		return stale;
	}
}
