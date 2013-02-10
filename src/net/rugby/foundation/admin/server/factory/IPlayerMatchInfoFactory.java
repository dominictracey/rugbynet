package net.rugby.foundation.admin.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IPlayerMatchInfo;

public interface IPlayerMatchInfoFactory {
	public abstract IPlayerMatchInfo get(Long id);
	public abstract List<IPlayerMatchInfo> getForComp(Long playerId, Long compId);
	public abstract List<IPlayerMatchInfo> getForMatch(Long matchId);
	public abstract IPlayerMatchInfo getForPlayerMatchStats(Long pmsId);
}
