/**
 * 
 */
package net.rugby.foundation.admin.server.rules.match;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.IPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.model.IPlayerMatchStatsFetcher;
import net.rugby.foundation.admin.server.rules.CoreRule;
import net.rugby.foundation.admin.server.workflow.fetchstats.FetchMatchStats.Home_or_Visitor;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;

import org.joda.time.DateTime;

/**
 * @author home
 *
 */
public class RuleMatchStatsToFetch extends CoreRule<IMatchGroup> {

	private IPlayerMatchStatsFetcherFactory pmsff;
	private IPlayerFactory pf;

	/**
	 * @param t
	 */
	public RuleMatchStatsToFetch(IMatchGroup t, IPlayerMatchStatsFetcherFactory pmsff, IPlayerFactory pf) {
		super(t);
		this.pmsff = pmsff;
		this.pf = pf;
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
				log += "Rule failed: match must be in FINAL state. Currently in " + target.getWorkflowStatus().toString();
				return false;
			}


			IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(pf.create(), target, Home_or_Visitor.HOME, 0, target.getForeignUrl());
			// make sure we get the latest and greatest
			fetcher.setUrl(target.getForeignUrl());

			if (fetcher.hasFlopped()) {
				fetch = true;
				DateTime now = new DateTime();
				log += "Stats ready at " + now.toString();;
				Logger.getLogger(RuleMatchStatsToFetch.class.getName()).log(Level.INFO,"Ready to look for results for match " + target.getDisplayName() +"("+ target.getId() + ")");

			}

			System.out.println(log);


			return fetch;
		}
		return null;
	}
}
