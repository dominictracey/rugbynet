/**
 * 
 */
package net.rugby.foundation.admin.server.rules.match;

import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.server.rules.CoreRule;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.RatingMode;

/**
 * @author home
 *
 */

public class RuleMatchToRate extends CoreRule<IMatchGroup> {

	private IRoundFactory rf;
	private ISeriesConfigurationFactory scf;


	/**
	 * @param t
	 */
	public RuleMatchToRate(IMatchGroup t, IRoundFactory rf, ISeriesConfigurationFactory scf) {
		super(t);
		this.rf = rf;
		this.scf = scf;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.IRule#test()
	 */
	@Override
	public Boolean test() {
		boolean retval = false;

		if (target != null) {
						
			IRound r = rf.get(target.getRoundId());
			
			ISeriesConfiguration sc = scf.getByCompAndMode(r.getCompId(), RatingMode.BY_MATCH);
			
			if (sc != null) {
				log = "Series " + sc.getDisplayName() + " ready to process.";
			}
			
			return sc != null;
		}
		
		return retval;
	}
}
