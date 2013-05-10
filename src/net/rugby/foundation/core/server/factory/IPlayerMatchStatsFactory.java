package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.Position.position;

public interface IPlayerMatchStatsFactory {

	public abstract IPlayerMatchStats getById(Long id);

	public abstract IPlayerMatchStats put(IPlayerMatchStats val);

	public abstract Boolean delete(IPlayerMatchStats val);

	public abstract List<IPlayerMatchStats> getByMatchId(Long matchId);

	public abstract List<IPlayerMatchStats> query(List<Long> matchIds,
			position posi, Long countryId, Long teamId);

}