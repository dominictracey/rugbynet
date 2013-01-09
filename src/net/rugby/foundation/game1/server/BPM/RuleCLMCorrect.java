/**
 * 
 */
package net.rugby.foundation.game1.server.BPM;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.rules.CoreRule;
import net.rugby.foundation.core.server.factory.IClubhouseFactory;
import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.server.factory.IMatchEntryFactory;
import net.rugby.foundation.game1.server.factory.IRoundEntryFactory;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.ILeague;
import net.rugby.foundation.game1.shared.IMatchEntry;
import net.rugby.foundation.game1.shared.IRoundEntry;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.ICompetition;

/**
 * @author home
 *
 */
public class RuleCLMCorrect extends CoreRule<ICompetition> {

	//private ILeaderboardFactory lbf;
	private IClubhouseFactory chf;
	private IClubhouseLeagueMapFactory chlmf;
	private ILeagueFactory lf;
	private IMatchEntryFactory mef;
	private IRoundEntryFactory ref;

	/**
	 * @param t
	 */
	public RuleCLMCorrect(ICompetition t, IClubhouseFactory chf, IClubhouseLeagueMapFactory chlmf, ILeagueFactory lf,
			IMatchEntryFactory mef, IRoundEntryFactory ref) {
		super(t);
		this.chf = chf;
		this.chlmf = chlmf;
		this.lf = lf;
		this.mef = mef;
		this.ref = ref;
	}


	/** 
	 * @return true if:
	 * 		1) Each clubhouse must have a CLM for every comp
	 * 		2) Each CLM belonging to a clubhouse for a competition must have a valid league referenced
	 * 		3) Every CLM must have a valid league reference (encompasses 2?)
	 * 		4) All leagues must have unique Ids (constrained by @Id annotation?)
	 * 		5) There must be one and only one CLM for each league
	 * 		6) Every matchEntry should be in one and only one RoundEntry
	 * 
	 */
	@Override
	public Boolean test() {
		try {

			Boolean retval = true;
			Logger.getLogger(RuleCLMCorrect.class.getName()).log(Level.INFO,"Starting CLMCorrect rule check");

			// 1. check that all Clubhouses have CLMs and those CLMs have leagues
			List<IClubhouse> clubhouses = chf.getAll();
			for (IClubhouse ch : clubhouses) {
				chlmf.setClubhouseAndCompId(target.getId(), ch.getId());
				IClubhouseLeagueMap clm = chlmf.get();
				if (clm == null) {
					Logger.getLogger(RuleCLMCorrect.class.getName()).log(Level.WARNING,"test did not find CLM for clubhouse " + ch.getName() +"(" + ch.getId() + ") for comp " + target.getLongName());
					retval =  false;
				} else {
					// 2.
					lf.setId(clm.getLeagueId());
					ILeague l = lf.get();
					if (l == null) {
						Logger.getLogger(RuleCLMCorrect.class.getName()).log(Level.WARNING,"test did not find league " + clm.getLeagueId() + " for clubhouse " + ch.getName() +"(" + ch.getId() + ") for comp " + target.getLongName());
						retval =  false;						
					}
				}
			}

			Logger.getLogger(RuleCLMCorrect.class.getName()).log(Level.INFO,"1. & 2. all Clubhouses have CLMs and those CLMs have leagues");

			// 3. check that all CLMs have valid references
			chlmf.setCompetitionId(target.getId());
			List<IClubhouseLeagueMap> clms = chlmf.getList();

			for (IClubhouseLeagueMap chlm: clms) {
				lf.setId(chlm.getLeagueId());
				ILeague l = lf.get();
				if (l == null) {
					Logger.getLogger(RuleCLMCorrect.class.getName()).log(Level.WARNING,"test did not find league " + chlm.getLeagueId() + " referenced in CLM " + chlm.getId() + " for comp " + target.getLongName());
					retval =  false;						
				}
			}

			Logger.getLogger(RuleCLMCorrect.class.getName()).log(Level.INFO,"3. all CLMs have valid references");

			// now check for orphaned Leagues
			Set<ILeague> leagues = lf.getAll();
			Set<Long> lids = new HashSet<Long>();
			for (ILeague league: leagues) {
				// 4.
				if (!lids.add(league.getId()))  {
					Logger.getLogger(RuleCLMCorrect.class.getName()).log(Level.WARNING,"test found duplicate league " + league.getId() + " for comp " + target.getLongName());
					retval =  false;					
				}
				chlmf.setLeagueId(league.getId());
				List<IClubhouseLeagueMap> chlml = chlmf.getList();

				//5. 
				if (chlml == null || chlml.isEmpty()) {
					Logger.getLogger(RuleCLMCorrect.class.getName()).log(Level.WARNING,"test did not find CLM for league " + league.getId() + " for comp " + target.getLongName());
					retval =  false;
				} else if (chlml.size() > 1) {
					Logger.getLogger(RuleCLMCorrect.class.getName()).log(Level.WARNING,"test found multiple CLMs for league " + league.getId());
					retval =  false;
				}
			}

			Logger.getLogger(RuleCLMCorrect.class.getName()).log(Level.INFO,"4 & 5. no orphaned or duplicate leagues");

			// 6. Every matchEntry should be in one and only one RoundEntry
			Set<IMatchEntry> mes = mef.getAll();
			Set<IRoundEntry> res = ref.getAll();
			for (IRoundEntry re : res) {
				for (IMatchEntry me: re.getMatchPickMap().values()) {
					IMatchEntry foundObj = null;
					for (IMatchEntry rawMe: mes) {
						if (me.getId().equals(rawMe.getId())) {
							foundObj = rawMe;
							break;
						}
					}

					if (foundObj != null) {
						mes.remove(foundObj);
					} else {
						Logger.getLogger(RuleCLMCorrect.class.getName()).log(Level.WARNING,"test found MatchEntry reference to  " + me.getId() + " in RoundEntry " + re.getId() + " that either didn't exist or was in some other RoundEntry.");
						retval =  false;						
					}
				}
			}

			Logger.getLogger(RuleCLMCorrect.class.getName()).log(Level.INFO,"6. Every matchEntry is in one and only one RoundEntry");


			// 6b. the ones left in mes are orphans
			for (IMatchEntry me : mes) {
				Logger.getLogger(RuleCLMCorrect.class.getName()).log(Level.WARNING,"test found MatchEntry " + me.getId() + " that is not a member of any RoundEntry.");
				retval =  false;						
			}

			Logger.getLogger(RuleCLMCorrect.class.getName()).log(Level.INFO,"6b. the are no MatchEntrys left as orphans");

			return retval;

		} catch (Throwable caught) {	
			Logger.getLogger(RuleCLMCorrect.class.getName()).log(Level.SEVERE, "test " + caught.getLocalizedMessage(), caught);
			return false;
		}
	}
}
