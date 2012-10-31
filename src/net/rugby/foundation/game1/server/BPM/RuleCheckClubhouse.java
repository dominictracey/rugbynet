/**
 * 
 */
package net.rugby.foundation.game1.server.BPM;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.rules.CoreRule;
import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.ILeague;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.ICompetition;

/**
 * @author home
 *
 */
public class RuleCheckClubhouse extends CoreRule<IClubhouse> {

	private IClubhouseLeagueMapFactory chlmf;
	private ILeagueFactory lf;
	private ICompetition comp;
	/**
	 * @param t
	 */
	public RuleCheckClubhouse(IClubhouse t, ICompetition comp, IClubhouseLeagueMapFactory chlmf, ILeagueFactory lf) {
		super(t);
		this.comp = comp;

		this.chlmf = chlmf;
		this.lf = lf;

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
			Logger.getLogger(RuleCheckClubhouse.class.getName()).log(Level.INFO,"Starting CLMCorrect rule check");

			// 1. check that Clubhouse has CLM and the CLM has a league

			chlmf.setClubhouseAndCompId(comp.getId(), target.getId());
			List<IClubhouseLeagueMap> clms = chlmf.getList();
			if (clms == null || clms.size() != 1) {
				Logger.getLogger(RuleCheckClubhouse.class.getName()).log(Level.WARNING,"test did not find CLM for clubhouse " + target.getName() +"(" + target.getId() + ") for comp " + comp.getLongName());
				retval =  false;
			} else {
				// 2.
				lf.setId(clms.get(0).getLeagueId());
				ILeague l = lf.get();
				if (l == null || l.getId() == null) {
					Logger.getLogger(RuleCheckClubhouse.class.getName()).log(Level.WARNING,"test did not find league " + clms.get(0).getLeagueId() + " for clubhouse " + target.getName() +"(" + target.getId() + ") for comp " + comp.getLongName());
					retval =  false;						
				}
			}


			Logger.getLogger(RuleCheckClubhouse.class.getName()).log(Level.INFO,"1. & 2. all Clubhouses have CLMs and those CLMs have leagues");

			return retval;

		} catch (Throwable caught) {	
			Logger.getLogger(RuleCheckClubhouse.class.getName()).log(Level.SEVERE, "test " + caught.getLocalizedMessage(), caught);
			return false;
		}
	}
}
