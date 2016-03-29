package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IMatchGroup;

public interface IMatchGroupFactory  extends ICachingFactory<IMatchGroup> {

	/**
	 * @return an existing match that matches the attributes of the passed in match (except id). Saves us from creating duplicates on accident.
	 */
	IMatchGroup find(IMatchGroup match);

	List<IMatchGroup> getMatchesForRound(Long roundId);

	List<? extends IMatchGroup> getMatchesWithPipelines();

	List<IMatchGroup> getMatchesForVirualComp(int ordinal, Long virtualCompId);

	List<IMatchGroup> getFutureMatchesForTeam(Long id);

}
