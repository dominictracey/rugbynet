/**
 * 
 */
package net.rugby.foundation.game1.server.BPM;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.rugby.foundation.admin.server.rules.IRule;
import net.rugby.foundation.admin.server.rules.RuleCompComplete;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.game1.server.factory.ILeaderboardFactory;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;

/**
 * @author home
 *
 */
@Singleton
public class CoreRuleFactory implements ICoreRuleFactory {

	private ILeaderboardFactory lbf;
	private ICompetitionFactory compF;

	@Inject 
	public void setFactories(ILeaderboardFactory lbf, ICompetitionFactory compF) {
		this.lbf = lbf;
		this.compF = compF;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.ICoreRuleFactory#get(net.rugby.foundation.model.shared.ICompetition, net.rugby.foundation.admin.server.rules.ICoreRuleFactory.CompRule)
	 */
	@Override
	public IRule<ICompetition> get(ICompetition target, CompRule rule) {
		if (rule == CompRule.COMP_COMPLETE) {
			return new RuleCompComplete(target);
		} 
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.ICoreRuleFactory#get(net.rugby.foundation.model.shared.IRound, net.rugby.foundation.admin.server.rules.ICoreRuleFactory.RoundRule)
	 */
	@Override
	public IRule<IClubhouseLeagueMap> get(IClubhouseLeagueMap target, ClubhouseLeagueMapRule rule) {
		if (rule == ClubhouseLeagueMapRule.LEADERBOARD_NEEDED) {
			return new RuleLeaderboardNeeded(target, lbf, compF);
		} 
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.ICoreRuleFactory#get(net.rugby.foundation.model.shared.IMatchGroup, net.rugby.foundation.admin.server.rules.ICoreRuleFactory.MatchRule)
	 */
	@Override
	public IRule<IMatchGroup> get(IMatchGroup target, MatchRule rule) {

		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.BPM.ICoreRuleFactory#get(net.rugby.foundation.model.shared.IAppUser, net.rugby.foundation.game1.server.BPM.ICoreRuleFactory.AppUserRule)
	 */
	@Override
	public IRule<IEntry> get(IEntry target, EntryRule rule) {
		if (rule == EntryRule.EMAIL_REMINDER) {
			return new RuleEmailReminders(target);
		}  
		 
		return null;
	}
	


}
