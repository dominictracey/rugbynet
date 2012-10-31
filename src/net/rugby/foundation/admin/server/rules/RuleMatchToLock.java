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
public class RuleMatchToLock extends CoreRule<IMatchGroup> {
	
	private static final int LOCK_OFFSET = -1; //number of hours before a match when we should lock it.

	/**
	 * @param t
	 */
	public RuleMatchToLock(IMatchGroup t) {
		super(t);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.IRule#test()
	 */
	@Override
	public Boolean test() {
		boolean lock = false;
		Calendar cal = new GregorianCalendar();
		Date now = new Date();

		if (target != null) {
			cal.setTime(target.getDate());
			cal.add(Calendar.HOUR, LOCK_OFFSET);
	   		String logLine = "RuleMatchToLock for Match " + target.getDisplayName() + " (" + target.getId().toString() + ") time: " + target.getDate().toString() + " offset " + cal.getTime().toString() + " now " + now.toString();

			if (cal.getTime().before(now) && (target.getLocked() == null || !target.getLocked())) {
				lock = true;
				logLine += "**LOCK NEEDED**";
				Logger.getLogger(RuleMatchToFetch.class.getName()).log(Level.WARNING,"Ready to lock match " + target.getDisplayName() +"("+ target.getId() + ")");
			}
			
			System.out.println(logLine);
		}

		return lock;
	}
}
