/**
 * 
 */
package net.rugby.foundation.game1.server.BPM;

import net.rugby.foundation.admin.server.rules.IRule;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;

/**
 * @author home
 *
 */
public interface ICoreRuleFactory {
	enum CompRule { COMP_COMPLETE  };
	IRule<ICompetition> get(ICompetition target, CompRule rule);
	enum ClubhouseLeagueMapRule { LEADERBOARD_NEEDED };
	IRule<IClubhouseLeagueMap> get(IClubhouseLeagueMap target, ClubhouseLeagueMapRule rule);
	enum MatchRule {  };
	IRule<IMatchGroup> get(IMatchGroup target, MatchRule rule);
	enum EntryRule { EMAIL_REMINDER, EMAIL_ROUND_RESULTS };
	IRule<IEntry> get(IEntry target, EntryRule rule);
	
}
