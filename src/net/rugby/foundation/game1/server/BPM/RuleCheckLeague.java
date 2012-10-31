/**
 * 
 */
package net.rugby.foundation.game1.server.BPM;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.rules.CoreRule;
import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.ILeague;
import net.rugby.foundation.model.shared.ICompetition;

/**
 * @author home
 *
 */
public class RuleCheckLeague extends CoreRule<ILeague> {

	private ICompetition comp;
	private IClubhouseLeagueMapFactory chlmf;
	/**
	 * @param t
	 */
	public RuleCheckLeague(ILeague t, ICompetition comp, IClubhouseLeagueMapFactory chlmf) {
		super(t);
		this.comp = comp;
		this.chlmf = chlmf;

	}


	/** 
	 * @return true if:
	 * 		1) Each clubhouse must have a CLM for the specified comp
	 * 		2) The CLM belonging to the clubhouse for a competition must have a valid league referenced
	 */
	@Override
	public Boolean test() {
		try {

			Boolean retval = true;

			// now check for orphaned Leagues
			chlmf.setLeagueId(target.getId());
			List<IClubhouseLeagueMap> chlml = chlmf.getList();

			//5. 
			if (chlml == null || chlml.isEmpty()) {
				Logger.getLogger(RuleCheckLeague.class.getName()).log(Level.WARNING,"test did not find CLM for league " + target.getId() + " for comp " + comp.getShortName());
				retval =  false;
			} else if (chlml.size() > 1) {
				Logger.getLogger(RuleCheckLeague.class.getName()).log(Level.WARNING,"test found multiple CLMs for league " + target.getId());
				retval =  false;
			}

			return retval;

		} catch (Throwable caught) {	
			Logger.getLogger(RuleCheckLeague.class.getName()).log(Level.SEVERE, "test " + caught.getLocalizedMessage(), caught);
			return false;
		}
	}
}
