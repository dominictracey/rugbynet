/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.IBlurbFactory;
import net.rugby.foundation.admin.server.factory.IDigestEmailFactory;
import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.IQueryRatingEngineFactory;
import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.RatingActions;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.SeriesActions;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.UserActions;
import net.rugby.foundation.admin.shared.IOrchestrationActions;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingMatrixFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.game1.server.BPM.OrchestrationCreateLeaderboard;
import net.rugby.foundation.game1.server.BPM.Game1OrchestrationActions;
import net.rugby.foundation.game1.server.BPM.Game1OrchestrationActions.LeagueActions;
import net.rugby.foundation.game1.server.BPM.Game1OrchestrationActions.MatchEntryActions;
import net.rugby.foundation.game1.server.BPM.Game1OrchestrationActions.RoundEntryActions;
import net.rugby.foundation.game1.server.BPM.OrchestrationDeleteMatchEntry;
import net.rugby.foundation.game1.server.BPM.OrchestrationDeleteMatchEntryFromRoundEntry;
import net.rugby.foundation.game1.server.BPM.OrchestrationFixClm;
import net.rugby.foundation.game1.server.BPM.OrchestrationFixClubhouse;
import net.rugby.foundation.game1.server.BPM.OrchestrationFixLeague;
import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.server.factory.IEntryFactory;
import net.rugby.foundation.game1.server.factory.ILeaderboardFactory;
import net.rugby.foundation.game1.server.factory.ILeaderboardRowFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.server.factory.IMatchEntryFactory;
import net.rugby.foundation.game1.server.factory.IRoundEntryFactory;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.ILeague;
import net.rugby.foundation.game1.shared.IMatchEntry;
import net.rugby.foundation.game1.shared.IRoundEntry;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

/**
 * @author home
 *
 */

public class OrchestrationFactory implements
IOrchestrationFactory {

	private ICompetitionFactory cf;
	private IMatchGroupFactory mf;
	private IOrchestrationConfigurationFactory ocf;
	private IResultFetcherFactory rff;
	private IMatchResultFactory mrf;
	private ILeagueFactory lf;
	private ILeaderboardFactory lbf;
	private IAppUserFactory auf;
	private IEntryFactory ef;
	private IClubhouseMembershipFactory chmf;
	private ILeaderboardRowFactory lbrf;
	private IClubhouseLeagueMapFactory chlmf;
	private IRoundEntryFactory ref;
	private IMatchEntryFactory mef;
	private IMatchRatingEngineSchemaFactory mresf;
	private IQueryRatingEngineFactory qref;
	private IRatingQueryFactory rqf;
	private IAdminTaskFactory atf;
	private IPlayerRatingFactory prf;
	private ISeriesConfigurationFactory scf;
	private ITopTenListFactory ttlf;
	private IBlurbFactory bf;
	private IDigestEmailFactory def;
	private IConfigurationFactory ccf;
	private IRatingGroupFactory rgf;
	private IRatingMatrixFactory rmf;

	//@REX this is probably horrendously inefficient
	@Inject
	public void setFactories(ICompetitionFactory cf, IRoundFactory rf, ITeamGroupFactory tf, IMatchGroupFactory mf, IOrchestrationConfigurationFactory ocf,
			IResultFetcherFactory rff, IMatchResultFactory mrf, 
			//			ILeagueFactory lf, 
			//			ILeaderboardFactory lbf, IEntryFactory ef, 
			IAppUserFactory auf, 
			//			IClubhouseMembershipFactory chmf, ILeaderboardRowFactory lbrf,
			//			IClubhouseLeagueMapFactory chlmf, IMatchEntryFactory mef, IRoundEntryFactory ref, 
			IMatchRatingEngineSchemaFactory mresf, IQueryRatingEngineFactory qref,
			IRatingQueryFactory rqf, IAdminTaskFactory atf, IPlayerRatingFactory prf,
			ISeriesConfigurationFactory scf, ITopTenListFactory ttlf,
			IBlurbFactory bf, IDigestEmailFactory def, IConfigurationFactory ccf,
			 IRatingGroupFactory rgf, IRatingMatrixFactory rmf) {
		this.cf = cf;
		this.mf = mf;
		//this.mf.setFactories(rf, tf);
		this.ocf = ocf;
		this.rff = rff;
		this.mrf = mrf;
		//		this.lf = lf;
		//		this.lbf = lbf;
		//		this.ef = ef;
		this.auf = auf;
		//		this.chmf = chmf;
		//		this.lbrf = lbrf;
		//		this.chlmf = chlmf;
		//		this.ref = ref;
		//		this.mef = mef;
		this.mresf = mresf;
		this.qref = qref;
		this.rqf = rqf;
		this.atf = atf;
		this.prf = prf;
		this.scf = scf;
		this.ttlf = ttlf;
		this.bf = bf;
		this.def = def;
		this.ccf = ccf;
		this.rgf = rgf;
		this.rmf = rmf;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationFactory#get(net.rugby.foundation.model.shared.IMatchGroup, net.rugby.foundation.admin.server.factory.IOrchestrationActions)
	 */
	@Override
	public IOrchestration<IMatchGroup> get(IMatchGroup target, IOrchestrationActions<IMatchGroup> action) {
		if (action.equals(AdminOrchestrationActions.MatchActions.FETCH)) {
			IOrchestration<IMatchGroup> o = new FetchBasicScoreMatchResultOrchestration(rff, cf, mrf, mf, ocf);	
			o.setTarget(target);
			return o;
		} else if (action.equals(AdminOrchestrationActions.MatchActions.LOCK)) {
			IOrchestration<IMatchGroup> o = new LockMatchOrchestration(mf, ocf);
			o.setTarget(target);
			return o;		
		} else if (action.equals(AdminOrchestrationActions.MatchActions.UNLOCK)) {
			IOrchestration<IMatchGroup> o = new UnlockMatchOrchestration(mf, ocf);
			o.setTarget(target);
			return o;		
		} else if (action.equals(AdminOrchestrationActions.MatchActions.MATCH_STALE_NEEDS_ATTENTION)) {
			IOrchestration<IMatchGroup> o = new MatchStaleNeedsAttentionOrchestration(ocf);
			o.setTarget(target);
			return o;
		} else if (action.equals(AdminOrchestrationActions.MatchActions.MATCH_STALE_MARK_UNREPORTED)) {
			IOrchestration<IMatchGroup> o = new MatchStaleMarkUnreportedOrchestration(mf, ocf);
			o.setTarget(target);
			return o;
		} else if (action.equals(AdminOrchestrationActions.MatchActions.FETCHSTATS)) {
			IOrchestration<IMatchGroup> o = new FetchMatchStatsOrchestration(mf, ocf, atf);	
			o.setTarget(target);
			return o;
		}	
		return null;

	} 

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationFactory#get(net.rugby.foundation.model.shared.ICompetition, net.rugby.foundation.admin.server.factory.IOrchestrationActions)
	 */
	@Override
	public IOrchestration<ICompetition> get(ICompetition target,
			IOrchestrationActions<ICompetition> action) {
		if (action.equals(AdminOrchestrationActions.CompActions.UPDATENEXTANDPREV)) {
			IOrchestration<ICompetition> o = new UpdateNextAndPrevOrchestration(cf, ocf);
			o.setTarget(target);
			return o;
		} else if (action.equals(AdminOrchestrationActions.CompActions.COMP_COMPLETE)) {
			//			IOrchestration<ICompetition> o = new CompetitionCompleteOrchestration(cf, wfcf, ocf);
			//			o.setTarget(target);
			//			return o;
		}		

		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationFactory#get(net.rugby.foundation.model.shared.IRound, net.rugby.foundation.admin.server.factory.IOrchestrationActions)
	 */
	@Override
	public IOrchestration<IRound> get(IRound target,
			IOrchestrationActions<IRound> action) {
		// none configured
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationFactory#get(net.rugby.foundation.model.shared.IAppUser, net.rugby.foundation.admin.server.factory.IOrchestrationActions)
	 */
	@Override
	public IOrchestration<IAppUser> get(IAppUser target,
			IOrchestrationActions<IAppUser> action) {
		// none configured
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationFactory#get(net.rugby.foundation.game1.shared.ILeague, net.rugby.foundation.admin.server.factory.IOrchestrationActions)
	 */
	@Override
	public IOrchestration<IClubhouseLeagueMap> get(IClubhouseLeagueMap target,
			IOrchestrationActions<IClubhouseLeagueMap> action) {
		if (action.equals(Game1OrchestrationActions.ClubhouseLeagueMapActions.CREATE_LEADERBOARD)) {
			IOrchestration<IClubhouseLeagueMap> o = new OrchestrationCreateLeaderboard(lf, lbf, ef, auf, cf, chmf, lbrf);
			o.setTarget(target);
			return o;
		} else if (action.equals(Game1OrchestrationActions.ClubhouseLeagueMapActions.FIX)) {
			IOrchestration<IClubhouseLeagueMap> o = new OrchestrationFixClm(chlmf);
			o.setTarget(target);
			return o;
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationFactory#get(net.rugby.foundation.model.shared.IClubhouse, net.rugby.foundation.admin.shared.IOrchestrationActions)
	 */
	@Override
	public IOrchestration<IClubhouse> get(IClubhouse target, IOrchestrationActions<IClubhouse> action) {
		if (action.equals(Game1OrchestrationActions.ClubhouseActions.FIX)) {
			IOrchestration<IClubhouse> o = new OrchestrationFixClubhouse(chlmf, lf, chmf, ef);
			o.setTarget(target);
			return o;
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationFactory#get(net.rugby.foundation.game1.shared.ILeague, net.rugby.foundation.game1.server.BPM.Game1OrchestrationActions.LeagueActions)
	 */
	@Override
	public IOrchestration<ILeague> get(ILeague target, LeagueActions action) {
		if (action.equals(Game1OrchestrationActions.LeagueActions.FIX)) {
			IOrchestration<ILeague> o = new OrchestrationFixLeague(chlmf, lf);
			o.setTarget(target);
			return o;
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationFactory#get(net.rugby.foundation.game1.shared.IMatchEntry, net.rugby.foundation.game1.server.BPM.Game1OrchestrationActions.MatchEntryActions)
	 */
	@Override
	public IOrchestration<IMatchEntry> get(IMatchEntry target, MatchEntryActions action) {
		if (action.equals(Game1OrchestrationActions.MatchEntryActions.DELETE)) {
			IOrchestration<IMatchEntry> o = new OrchestrationDeleteMatchEntry(mef);
			o.setTarget(target);
			return o;
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationFactory#get(net.rugby.foundation.game1.shared.IRoundEntry, net.rugby.foundation.game1.server.BPM.Game1OrchestrationActions.RoundEntryActions)
	 */
	@Override
	public IOrchestration<IRoundEntry> get(IRoundEntry target, RoundEntryActions action) {
		if (action.equals(Game1OrchestrationActions.RoundEntryActions.DELETEMATCHENTRY)) {
			IOrchestration<IRoundEntry> o = new OrchestrationDeleteMatchEntryFromRoundEntry(ref);
			o.setTarget(target);
			return o;
		}
		return null;
	}
	@Override
	public IOrchestration<IRatingQuery> get(IRatingQuery target,
			RatingActions action) {
		if (action.equals(AdminOrchestrationActions.RatingActions.GENERATE)) {
			IOrchestration<IRatingQuery> o = new GenerateRatingsOrchestration(ocf, mresf, qref, rqf, prf, ttlf);
			o.setTarget(target);
			return o;
		} else if (action.equals(AdminOrchestrationActions.RatingActions.CLEANUP)) {
			IOrchestration<IRatingQuery> o = new AdminCleanupOrchestration(ocf, rqf, prf, mf);
			o.setTarget(target);
			return o;
		}  else if (action.equals(AdminOrchestrationActions.RatingActions.RERUN)) {
			IOrchestration<IRatingQuery> o = new RerunRatingOrchestration(rgf, rmf);
			o.setTarget(target);
			return o;
		} 
		return null;
	}
	@Override
	public IOrchestration<ISeriesConfiguration> get(ISeriesConfiguration target,
			SeriesActions action) {
		if (action.equals(AdminOrchestrationActions.SeriesActions.PROCESS)) {
			IOrchestration<ISeriesConfiguration> o = new ProcessSeriesOrchestration(scf, ocf, atf);
			o.setTarget(target);
			return o;
		} //else if (action.equals(AdminOrchestrationActions.SeriesActions.CLEANUP)) {
//			IOrchestration<IRatingQuery> o = new SeriesCleanupOrchestration(ocf, rqf, prf, mf);
//			o.setTarget(target);
//			return o;
//		} 
		return null;
	}
	
	@Override
	public IOrchestration<IAppUser> get(IAppUser target, UserActions action) {
		if (action.equals(AdminOrchestrationActions.UserActions.SEND_DIGEST)) {
			IOrchestration<IAppUser> o = new SendDigestOrchestration(ocf, auf, bf, def, cf, ccf);
			o.setTarget(target);
			return o;
		} 
		return null;
	}
}
