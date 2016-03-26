/**
 * 
 */
package net.rugby.foundation.admin.server.rules.match;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.model.IPlayerMatchStatsFetcher;
import net.rugby.foundation.admin.server.rules.CoreRule;
import net.rugby.foundation.admin.server.workflow.fetchstats.FetchMatchStats.Home_or_Visitor;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;

/**
 * @author home
 *
 */
public class RuleMatchStatsFetched extends CoreRule<IMatchGroup> {

	private IPlayerMatchStatsFetcherFactory pmsff;

	/**
	 * @param t
	 */
	public RuleMatchStatsFetched(IMatchGroup t, IPlayerMatchStatsFetcherFactory pmsff) {
		super(t);
		this.pmsff = pmsff;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.IRule#test()
	 */
	@Override
	public Boolean test() {
		boolean fetch = false;

		if (target != null) {

			log = "RuleMatchStatsToFetch for Match " + target.getDisplayName() + " (" + target.getId().toString() + ")";

			// don't fetch until we have final score
			if (!target.getWorkflowStatus().equals(WorkflowStatus.FINAL)) {
				log += "failed: match must be in FINAL state. Currently in " + target.getWorkflowStatus().toString();
				return false;
			}


			IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(null, target, Home_or_Visitor.HOME, 0, target.getForeignUrl());

			if (fetcher.process()) {
				if (fetcher.hasFlopped()) {

					if (target.getSimpleScoreMatchResult() == null) {
						fetch = true;
						log += "**STATS READY**";
						Logger.getLogger(RuleMatchStatsFetched.class.getName()).log(Level.INFO,"Ready to look for results for match " + target.getDisplayName() +"("+ target.getId() + ")");
					}
				}

				System.out.println(log);
			}

			return fetch;
		}
		return null;
	}
}
