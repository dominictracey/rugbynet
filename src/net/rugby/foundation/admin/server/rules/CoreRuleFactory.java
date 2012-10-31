/**
 * 
 */
package net.rugby.foundation.admin.server.rules;

import com.google.inject.Singleton;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;

/**
 * @author home
 *
 */
@Singleton
public class CoreRuleFactory implements ICoreRuleFactory {

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.ICoreRuleFactory#get(net.rugby.foundation.model.shared.ICompetition, net.rugby.foundation.admin.server.rules.ICoreRuleFactory.CompRule)
	 */
	@Override
	public IRule<ICompetition> get(ICompetition target, CompRule rule) {
		if (rule == CompRule.COMP_COMPLETE) {
			return new RuleCompComplete(target);
		} else if (rule == CompRule.COMP_INCREMENT_NEXT_ROUND) {
			return new RuleCompIncrementNextRound(target);
		} 
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.ICoreRuleFactory#get(net.rugby.foundation.model.shared.IRound, net.rugby.foundation.admin.server.rules.ICoreRuleFactory.RoundRule)
	 */
	@Override
	public IRule<IRound> get(IRound target, RoundRule rule) {
		if (rule == RoundRule.ROUND_COMPLETE) {
			return new RuleRoundComplete(target);
		} 
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.ICoreRuleFactory#get(net.rugby.foundation.model.shared.IMatchGroup, net.rugby.foundation.admin.server.rules.ICoreRuleFactory.MatchRule)
	 */
	@Override
	public IRule<IMatchGroup> get(IMatchGroup target, MatchRule rule) {
		if (rule == MatchRule.MATCH_TO_LOCK) {
			return new RuleMatchToLock(target);
		} else if (rule == MatchRule.MATCH_TO_FETCH) {
			return new RuleMatchToFetch(target);
		} else if (rule == MatchRule.STALE_MATCH_NEED_ATTENTION) {
			return new RuleMatchStaleNeedsAttention(target);
		} else if (rule == MatchRule.STALE_MATCH_TO_MARK_UNREPORTED) {
			return new RuleMatchStaleMarkUnreported(target);
		} 
		 
		return null;
	}
	


}
