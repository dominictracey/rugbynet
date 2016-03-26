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
public class RuleMatchToFetch extends CoreRule<IMatchGroup> {
	private static final int FETCH_OFFSET = 2; //number of hours after a match when we start looking for results.

	/**
	 * @param t
	 */
	public RuleMatchToFetch(IMatchGroup t) {
		super(t);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.IRule#test()
	 */
	@Override
	public Boolean test() {
		boolean fetch = false;
		Calendar cal = new GregorianCalendar();
		Date now = new Date();

		if (target != null) {
			cal.setTime(target.getDate());
			cal.add(Calendar.HOUR, FETCH_OFFSET);
			String logLine = "RuleMatchToFetch for Match " + target.getDisplayName() + " (" + target.getId().toString() + ") time: " + target.getDate().toString() + " offset " + cal.getTime().toString() + " now " + now.toString();

			// don't fetch until locked
			if (target.getLocked() == null || target.getLocked() == false) {
	   			return false; 
	   		}
	   		
			if (now.after(cal.getTime())) {
				// match results?
				// TODO should we check target.getStatus() instead?

				if (target.getSimpleScoreMatchResult() == null) {
					fetch = true;
					logLine += "**FETCH NEEDED**";
					Logger.getLogger(RuleMatchToFetch.class.getName()).log(Level.WARNING,"Ready to look for results for match " + target.getDisplayName() +"("+ target.getId() + ")");
				}
			}
			System.out.println(logLine);
		}
		
		return fetch;
	}
}
