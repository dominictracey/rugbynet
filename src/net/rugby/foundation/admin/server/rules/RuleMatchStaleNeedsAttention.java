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
public class RuleMatchStaleNeedsAttention extends CoreRule<IMatchGroup> {
	
	//number of hours after a match when we should start getting worried
	public static final int STALE_NEED_ATTENTION_OFFSET = 5; 

	/**
	 * @param t
	 */
	public RuleMatchStaleNeedsAttention(IMatchGroup t) {
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
		cal.add(Calendar.HOUR, STALE_NEED_ATTENTION_OFFSET);
 
		if (cal.getTime().before(now) && target.getSimpleScoreMatchResult() == null) {
			stale = true;
			Logger.getLogger(RuleMatchStaleNeedsAttention.class.getName()).log(Level.WARNING,"Haven't found result for match " + target.getDisplayName() +"("+ target.getId() + ")");
		}

		return stale;
	}
}
