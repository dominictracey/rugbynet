/**
 * 
 */
package net.rugby.foundation.game1.server.BPM;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.rules.CoreRule;
import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.model.shared.IAppUser;

/**
 * @author home
 *
 */
public class RuleEmailReminders extends CoreRule<IEntry> {


	private IAppUserFactory auf;
	/**
	 * @param t
	 */
	public RuleEmailReminders(IEntry t) {
		super(t);
	}

	@Inject
	public void setFactories(IAppUserFactory auf)
	{
		this.auf = auf;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.IRule#test()
	 */
	@Override
	public Boolean test() {
		// have they opted out of getting email?
		auf.setId(target.getOwnerId());
		IAppUser u = auf.get();
		if (u != null) {
			return u.getOptOut();
		} else {
			return false;
		}
	}

}
