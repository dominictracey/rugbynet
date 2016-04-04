/**
 * 
 */
package net.rugby.foundation.admin.server.rules;

import net.rugby.foundation.admin.shared.ISeriesConfiguration;
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
	enum RoundRule { ROUND_FETCHED, ROUND_RATED, ROUND_COMPLETE};
	IRule<IRound> get(IRound target, RoundRule rule);
	enum MatchRule { MATCH_TO_LOCK, STALE_MATCH_NEED_ATTENTION, STALE_MATCH_TO_MARK_UNREPORTED, MATCH_TO_END, MATCH_TO_FINAL, MATCH_STATS_AVAILABLE, MATCH_STATS_FETCHED, MATCH_STATS_CLEANSED, MATCH_TO_RATE, MATCH_TO_PROMOTE };
	IRule<IMatchGroup> get(IMatchGroup target, MatchRule rule);
	enum SeriesConfigurationRule { READY_TO_PROCESS };
	IRule<ISeriesConfiguration> get(ISeriesConfiguration target, SeriesConfigurationRule rule);
}
