/**
 * 
 */
package net.rugby.foundation.admin.server.rules.match;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.rules.CoreRule;
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
		assert (target != null);
		
		boolean lock = false;
		Calendar cal = new GregorianCalendar();
		Date now = new Date();

		if (target != null) {
			cal.setTime(target.getDate());
			cal.add(Calendar.HOUR, LOCK_OFFSET);
	   		//log = "RuleMatchToLock for Match " + target.getDisplayName() + " (" + target.getId().toString() + ") time: " + target.getDate().toString() + " offset " + cal.getTime().toString() + " now " + now.toString();

			if (now.after(cal.getTime()) && (target.getLocked() == null || !target.getLocked())) {
				lock = true;
				log += "lock needed at " + now.toString();
				Logger.getLogger(this.getClass().getName()).log(Level.INFO,"Ready to lock match " + target.getDisplayName() +"("+ target.getId() + ")");
			}
			
			System.out.println(log);
		}

		return lock;
	}
}
