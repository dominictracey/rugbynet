package net.rugby.foundation.admin.server.orchestration;

import net.rugby.foundation.admin.shared.AdminOrchestrationActions.RatingActions;
import net.rugby.foundation.admin.shared.IOrchestrationActions;
import net.rugby.foundation.game1.server.BPM.Game1OrchestrationActions.LeagueActions;
import net.rugby.foundation.game1.server.BPM.Game1OrchestrationActions.MatchEntryActions;
import net.rugby.foundation.game1.server.BPM.Game1OrchestrationActions.RoundEntryActions;
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

public interface IOrchestrationFactory {
		
	IOrchestration<IMatchGroup> get(IMatchGroup target, IOrchestrationActions<IMatchGroup> action);
	IOrchestration<ICompetition> get(ICompetition target, IOrchestrationActions<ICompetition> action);
	IOrchestration<IRound> get(IRound target, IOrchestrationActions<IRound> action);
	IOrchestration<IAppUser> get(IAppUser target, IOrchestrationActions<IAppUser> action);
	/**
	 * @param target
	 * @param action
	 * @return
	 */
	IOrchestration<IClubhouseLeagueMap> get(IClubhouseLeagueMap target,
			IOrchestrationActions<IClubhouseLeagueMap> action);

	IOrchestration<IClubhouse> get(IClubhouse target, IOrchestrationActions<IClubhouse> action);
	/**
	 * @param iLeague
	 * @param valueOf
	 * @return
	 */
	IOrchestration<ILeague> get(ILeague iLeague, LeagueActions valueOf);
	/**
	 * @param matchEntry
	 * @param valueOf
	 * @return
	 */
	IOrchestration<IMatchEntry> get(IMatchEntry target, MatchEntryActions action);
	/**
	 * @param roundEntry
	 * @param valueOf
	 * @return
	 */
	IOrchestration<IRoundEntry> get(IRoundEntry target, RoundEntryActions action);
	
	IOrchestration<IRatingQuery> get(IRatingQuery target, RatingActions action);

}
