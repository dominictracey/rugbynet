/**
 * 
 */
package net.rugby.foundation.admin.server.rules;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.model.shared.IMatchGroup;

/**
 * @author home
 *
 */
public class RuleMatchStatsToFetch extends CoreRule<IMatchGroup> {
	private static final int FETCH_OFFSET = 2; //number of hours after a match when we start looking for results.
	private IPlayerMatchStatsFetcherFactory pmsff;

//	@Inject
//	public RuleMatchStatsToFetch(IPlayerMatchStatsFetcherFactory pmsff) {
//		
//	}
	
	/**
	 * @param t
	 */
	public RuleMatchStatsToFetch(IMatchGroup t) {
		super(t);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.IRule#test()
	 */
	@Override
	public Boolean test() {
		boolean fetch = false;
		
		if (target != null) {
			
			String logLine = "RuleMatchStatsToFetch for Match " + target.getDisplayName() + " (" + target.getId().toString() + ")";

			// don't fetch until locked
			if (target.getLocked() == null || target.getLocked() == false) {
	   			return false; 
	   		}
			
			// don't fetch until score downloaded
			if (target.getSimpleScoreMatchResultId() == null) {
	   			return false; 
	   		}
	   		
			if (false) {
				// match results?
				// TODO should we check target.getStatus() instead?

				if (target.getSimpleScoreMatchResult() == null) {
					fetch = true;
					logLine += "**FETCH NEEDED**";
					Logger.getLogger(RuleMatchStatsToFetch.class.getName()).log(Level.WARNING,"Ready to look for results for match " + target.getDisplayName() +"("+ target.getId() + ")");
				}
			}
			System.out.println(logLine);
		}
		
		return fetch;
	}

	public void setFetcherFactory(IPlayerMatchStatsFetcherFactory pmsff) {
		this.pmsff = pmsff;
		
	}
}
