package net.rugby.foundation.admin.server.factory;

import java.util.List;

import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.Position.position;

public interface IPlayerMatchInfoFactory {
	public abstract IPlayerMatchInfo get(Long id);
	public abstract List<IPlayerMatchInfo> getForComp(Long playerId, Long compId);
	public abstract List<IPlayerMatchInfo> getForMatch(Long matchId, IMatchRatingEngineSchema schema);
	public abstract List<IPlayerMatchInfo> query(Long compId, Long roundId,
			position posi, Long countryId, Long teamId, Long schemaId);
	public abstract IPlayerMatchInfo getForPlayerMatchStats(Long pmsId, IMatchRatingEngineSchema schema);
}
