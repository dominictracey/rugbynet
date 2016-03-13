/**
 * 
 */
package net.rugby.foundation.admin.server.rules;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;

/**
 * @author home
 *
 */
public interface ICoreRuleFactory {
	enum CompRule { COMP_COMPLETE, COMP_INCREMENT_NEXT_ROUND  };
	IRule<ICompetition> get(ICompetition target, CompRule rule);
	enum RoundRule { ROUND_COMPLETE };
	IRule<IRound> get(IRound target, RoundRule rule);
	enum MatchRule { MATCH_TO_LOCK, MATCH_TO_FETCH, STALE_MATCH_NEED_ATTENTION, STALE_MATCH_TO_MARK_UNREPORTED, MATCH_STATS_TO_FETCH };
	IRule<IMatchGroup> get(IMatchGroup target, MatchRule rule);
	
}
