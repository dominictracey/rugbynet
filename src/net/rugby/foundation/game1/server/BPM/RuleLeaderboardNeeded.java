/**
 * 
 */
package net.rugby.foundation.game1.server.BPM;

import net.rugby.foundation.admin.server.rules.CoreRule;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.game1.server.factory.ILeaderboardFactory;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.model.shared.IRound;

/**
 * @author home
 *
 */
public class RuleLeaderboardNeeded extends CoreRule<IClubhouseLeagueMap> {

	private ILeaderboardFactory lbf;
	private ICompetitionFactory compF;
	/**
	 * @param t
	 */
	public RuleLeaderboardNeeded(IClubhouseLeagueMap t, ILeaderboardFactory lbf, ICompetitionFactory compF) {
		super(t);
		this.lbf = lbf;
		this.compF = compF;
	}
	
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.IRule#test()
	 */
	@Override
	public Boolean test() {
		
		// figure out what the round we need to see if we need to generate for (comp's prevRound)
		compF.setId(target.getCompId());
		IRound prevRound = compF.getCompetition().getPrevRound();
		
		//has the first round finished yet?
		if (prevRound == null) 
			return false;
		
		// Do we have a leaderboard for this round yet?
		lbf.setClmAndRound(target, prevRound);
		return lbf.get() == null;
	}
}
