package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.admin.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IRatingQuery;

public interface IPlayerMatchRatingFactory {
	IPlayerMatchRating get(Long id);
	List<? extends IPlayerMatchRating> getForMatch(Long matchId);
	IPlayerMatchRating getNew(IPlayer playerId, IMatchGroup matchId, Integer rating, IRatingEngineSchema schemaId, IPlayerMatchStats playerMatchStatsId, String details, IRatingQuery query);
	IPlayerMatchRating put(IPlayerMatchRating pmr);
	IPlayerMatchRating get(IPlayerMatchStats pms, IRatingEngineSchema schema);
	Boolean deleteForSchema(IRatingEngineSchema schema);
	boolean deleteForMatch(IMatchGroup m);
	boolean deleteForQuery(IRatingQuery rq);
}
