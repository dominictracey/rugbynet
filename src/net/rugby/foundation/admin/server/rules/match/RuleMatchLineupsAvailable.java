/**
 * 
 */
package net.rugby.foundation.admin.server.rules.match;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.espnscrum.ILineupFetcherFactory;
import net.rugby.foundation.admin.server.model.ILineupFetcher;
import net.rugby.foundation.admin.server.rules.CoreRule;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ILineupSlot;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;

/**
 * @author home
 *
 */
public class RuleMatchLineupsAvailable extends CoreRule<IMatchGroup> {
	
	private ILineupFetcherFactory lff;
	private ICompetition comp;
	private ICompetitionFactory cf;
	private IRoundFactory rf;

	/**
	 * @param t
	 * @param lff 
	 */
	public RuleMatchLineupsAvailable(IMatchGroup t, ILineupFetcherFactory lff, ICompetitionFactory cf, IRoundFactory rf) {
		super(t);
		this.lff = lff;
		this.cf = cf;
		this.rf = rf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.IRule#test()
	 */
	@Override
	public Boolean test() {
		Boolean retval = false;
		
		if (target != null) {
			IRound round = rf.get(target.getRoundId());
			ICompetition comp = cf.get(round.getCompId());
			ILineupFetcher lf = lff.getLineupFetcher(comp.getCompType());
			lf.setMatch(target);
			lf.setComp(comp);
			List<ILineupSlot> lineupHome = lf.get(true);
			if (lf.getErrorMessage() != null && !lf.getErrorMessage().isEmpty()) {
				return false;
			}
			if (lineupHome == null || lineupHome.size() < 15) {
				return false;
			}
			List<ILineupSlot> lineupVisit = lf.get(false);
			if (lf.getErrorMessage() != null && !lf.getErrorMessage().isEmpty()) {
				return false;
			}
			if (lineupVisit == null || lineupVisit.size() < 15) {
				return false;
			}
			
			
			retval = true;
			log += "lineups available...\n";
			Logger.getLogger(this.getClass().getName()).log(Level.INFO,"Ready to fetch lineups for " + target.getDisplayName() +"("+ target.getId() + ")");
			
			
			System.out.println(log);
		}

		return retval;
	}
}
