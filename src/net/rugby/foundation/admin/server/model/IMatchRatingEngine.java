package net.rugby.foundation.admin.server.model;

import java.util.List;

import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ITeamMatchStats;

public interface IMatchRatingEngine {
	void setTeamStats(ITeamMatchStats homeStats, ITeamMatchStats visitStats);
	void setPlayerStats(List<IPlayerMatchStats> homeStats, List<IPlayerMatchStats> visitStats);
	List<IPlayerMatchRating> generate(IMatchRatingEngineSchema schema, IMatchGroup match);
	List<IMatchRatingEngineSchema> getSupportedSchemas();
}
