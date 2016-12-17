/**
 * 
 */
package net.rugby.foundation.admin.server.rules;

import net.rugby.foundation.admin.server.factory.IPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.ILineupFetcherFactory;
import net.rugby.foundation.admin.server.rules.match.RuleMatchLineupsAvailable;
import net.rugby.foundation.admin.server.rules.match.RuleMatchStatsToFetch;
import net.rugby.foundation.admin.server.rules.match.RuleMatchToEnd;
import net.rugby.foundation.admin.server.rules.match.RuleMatchToFinalScore;
import net.rugby.foundation.admin.server.rules.match.RuleMatchToLock;
import net.rugby.foundation.admin.server.rules.match.RuleMatchToPromote;
import net.rugby.foundation.admin.server.rules.match.RuleMatchToRate;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;

import com.google.inject.Inject;

/**
 * @author home
 *
 */

public class CoreRuleFactory implements ICoreRuleFactory {

	private IPlayerMatchStatsFetcherFactory pmsff;
	private IRoundFactory rf;
	private IResultFetcherFactory srff;
	private ISeriesConfigurationFactory scf;
	private IPlayerFactory pf;
	private IUniversalRoundFactory urf;
	private IConfigurationFactory ccf;
	private ILineupFetcherFactory lff;
	private ICompetitionFactory cf;

	@Inject
	public CoreRuleFactory(IPlayerMatchStatsFetcherFactory pmsff, IRoundFactory rf, IResultFetcherFactory srff, ISeriesConfigurationFactory scf, 
			IPlayerFactory pf, IUniversalRoundFactory urf, IConfigurationFactory ccf, ILineupFetcherFactory lff, ICompetitionFactory cf) {
		this.pmsff = pmsff;
		this.rf = rf;
		this.srff = srff;
		this.scf = scf;
		this.pf = pf;
		this.urf = urf;
		this.ccf = ccf;
		this.lff = lff;
		this.cf = cf;
	}
	
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
		if (rule == RoundRule.ROUND_FETCHED) {
			return new RuleRoundFetched(target);
		} else if (rule == RoundRule.ROUND_RATED) {
			return new RuleRoundRated(target);
		} else if (rule == RoundRule.ROUND_COMPLETE) {
			return new RuleRoundComplete(target);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.rules.ICoreRuleFactory#get(net.rugby.foundation.model.shared.IMatchGroup, net.rugby.foundation.admin.server.rules.ICoreRuleFactory.MatchRule)
	 */
	@Override
	public IRule<IMatchGroup> get(IMatchGroup target, MatchRule rule) {
		if (rule == MatchRule.MATCH_LINEUPS_AVAILABLE) {
			return new RuleMatchLineupsAvailable(target, lff, cf, rf);
		} else if (rule == MatchRule.MATCH_TO_LOCK) {
			return new RuleMatchToLock(target);
		} else if (rule == MatchRule.MATCH_TO_END) {
			return new RuleMatchToEnd(target);
		} else if (rule == MatchRule.MATCH_TO_FINAL) {
			return new RuleMatchToFinalScore(target, rf, srff);
		} else if (rule == MatchRule.MATCH_STATS_AVAILABLE) {
			return new RuleMatchStatsToFetch(target, pmsff, pf);
		} else if (rule == MatchRule.MATCH_TO_RATE) {
			return new RuleMatchToRate(target, rf, scf);
		} else if (rule == MatchRule.MATCH_TO_PROMOTE) {
			return new RuleMatchToPromote(target);
		} 
		return null;
	}
	
	@Override
	public IRule<ISeriesConfiguration> get(ISeriesConfiguration target, SeriesConfigurationRule rule) {
		if (rule == SeriesConfigurationRule.READY_TO_PROCESS) {
			return new RuleSeriesReadyToProcess(target, urf, rf, ccf);
		} 
		return null;
	}

}
