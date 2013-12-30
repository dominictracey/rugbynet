package net.rugby.foundation.admin.server.factory;

import java.util.List;

import net.rugby.foundation.admin.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.IRatingQuery;

public interface IPlayerMatchInfoFactory {
	public abstract IPlayerMatchInfo get(Long id);
	public abstract List<IPlayerMatchInfo> getForComp(Long playerId, Long compId);
	public abstract List<IPlayerMatchInfo> getForMatch(Long matchId, IRatingEngineSchema schema);
	public abstract List<IPlayerMatchInfo> query(IRatingQuery query, Long schemaId);
	public abstract IPlayerMatchInfo getForPlayerMatchStats(Long pmsId, IRatingEngineSchema schema);
}
