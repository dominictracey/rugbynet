package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;

public interface IPlayerMatchRatingFactory {
	IPlayerMatchRating get(Long id);
	List<? extends IPlayerMatchRating> getForMatch(Long matchId);
	IPlayerMatchRating getNew(IPlayer playerId, IMatchGroup matchId, Integer rating, IMatchRatingEngineSchema schemaId, IPlayerMatchStats playerMatchStatsId);
	IPlayerMatchRating put(IPlayerMatchRating pmr);
	IPlayerMatchRating get(IPlayerMatchStats pms, IMatchRatingEngineSchema schema);
	Boolean deleteForSchema(IMatchRatingEngineSchema schema);
}
