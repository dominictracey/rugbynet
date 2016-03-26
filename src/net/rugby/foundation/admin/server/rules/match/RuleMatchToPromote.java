/**
 * 
 */
package net.rugby.foundation.admin.server.rules.match;

import net.rugby.foundation.admin.server.rules.CoreRule;
import net.rugby.foundation.model.shared.IMatchGroup;

/**
 * @author home
 *
 */
public class RuleMatchToPromote extends CoreRule<IMatchGroup> {
	

	/**
	 * @param t
	 */
	public RuleMatchToPromote(IMatchGroup t) {
		super(t);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.IRule#test()
	 */
	@Override
	public Boolean test() {
	
		return target.getGuid() != null && !target.getGuid().isEmpty();
	}
}
