/**
 * 
 */
package net.rugby.foundation.admin.server.rules.match;

import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.admin.server.model.IResultFetcher;
import net.rugby.foundation.admin.server.rules.CoreRule;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchResult.ResultType;
import net.rugby.foundation.model.shared.IRound;

import org.joda.time.DateTime;

/**
 * @author home
 *
 */

public class RuleMatchToFinalScore extends CoreRule<IMatchGroup> {

	private IRoundFactory rf;
	private IResultFetcherFactory srff;


	/**
	 * @param t
	 */
	public RuleMatchToFinalScore(IMatchGroup t, IRoundFactory rf, IResultFetcherFactory srff) {
		super(t);
		this.rf = rf;
		this.srff = srff;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.IRule#test()
	 */
	@Override
	public Boolean test() {
		boolean retval = false;

		if (target != null) {
						
			IRound r = rf.get(target.getRoundId());
			
			IResultFetcher fetcher = srff.getResultFetcher(r.getCompId(), null, ResultType.MATCHES);
			retval = fetcher.isAvailable(target);
			if (retval) {
				DateTime now = new DateTime();
				log += "Stats available at " + now.toString();
			}
		}
		
		return retval;
	}
}
