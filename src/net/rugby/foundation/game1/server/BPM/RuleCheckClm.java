/**
 * 
 */
package net.rugby.foundation.game1.server.BPM;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.rules.CoreRule;
import net.rugby.foundation.core.server.factory.IClubhouseFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.ILeague;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.ICompetition;

/**
 * @author home
 *
 */
public class RuleCheckClm extends CoreRule<IClubhouseLeagueMap> {


	private ILeagueFactory lf;
	private ICompetition comp;
	private IClubhouseFactory chf;
	/**
	 * @param t
	 */
	public RuleCheckClm(IClubhouseLeagueMap t, ICompetition comp, ILeagueFactory lf, IClubhouseFactory chf) {
		super(t);
		this.comp = comp;
		this.lf = lf;
		this.chf = chf;

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
			Logger.getLogger(RuleCheckClm.class.getName()).log(Level.INFO,"Starting CLMCorrect rule check");

			// 3. check that all CLMs have valid references

			lf.setId(target.getLeagueId());
			ILeague l = lf.get();
			if (l == null) {
				Logger.getLogger(RuleCheckClm.class.getName()).log(Level.WARNING,"test did not find league " + target.getLeagueId() + " referenced in CLM " + target.getId() + " for comp " + comp.getShortName());
				retval =  false;						
			}
			
			// also make sure the CLM belongs to a valid clubhouse
			chf.setId(target.getClubhouseId());
			IClubhouse ch = chf.get();
			if (ch == null || ch.getId() == null) {
				Logger.getLogger(RuleCheckClm.class.getName()).log(Level.WARNING,"test did not find clubhouse " + target.getClubhouseId() + " referenced in CLM " + target.getId() + " for comp " + comp.getShortName());
				retval =  false;				
			}

			return retval;

		} catch (Throwable caught) {	
			Logger.getLogger(RuleCheckClm.class.getName()).log(Level.SEVERE, "test " + caught.getLocalizedMessage(), caught);
			return false;
		}
	}
}
