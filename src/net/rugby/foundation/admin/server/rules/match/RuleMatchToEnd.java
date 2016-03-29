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
public class RuleMatchToEnd extends CoreRule<IMatchGroup> {
	
	private static final int END_OFFSET = 100; //number of hours after a match's start time when we should call it over

	/**
	 * @param t
	 */
	public RuleMatchToEnd(IMatchGroup t) {
		super(t);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.IRule#test()
	 */
	@Override
	public Boolean test() {
		boolean retval = false;
		Calendar cal = new GregorianCalendar();
		Date now = new Date();

		if (target != null) {
			cal.setTime(target.getDate());
			cal.add(Calendar.MINUTE, END_OFFSET);
	   		//log = "RuleMatchToEnd for Match " + target.getDisplayName() + " (" + target.getId().toString() + ") time: " + target.getDate().toString() + " offset " + cal.getTime().toString() + " now " + now.toString();

			if (now.after(cal.getTime())) {
				retval = true;
				log += "End needed at " + now.toString();
				Logger.getLogger(this.getClass().getName()).log(Level.INFO,"Ready to end match " + target.getDisplayName() +"("+ target.getId() + ")");
			}
			
			System.out.println(log);
		}

		return retval;
	}
}
