package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IPlayerMatchStats;

public interface IPlayerMatchStatsFactory {

	public abstract IPlayerMatchStats getById(Long id);

	public abstract IPlayerMatchStats put(IPlayerMatchStats val);

	public abstract Boolean delete(IPlayerMatchStats val);

	public abstract List<? extends IPlayerMatchStats> getByMatchId(Long matchId);

}