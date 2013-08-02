package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IMatchGroup;

public interface IMatchGroupFactory {
	void setId(Long id);
	
	IMatchGroup getGame();
	
	/**
	 * @return an existing match that matches the attributes of the passed in match (except id). Saves us from creating duplicates on accident.
	 */
	IMatchGroup find(IMatchGroup match);
	IMatchGroup put(IMatchGroup g);

	List<IMatchGroup> getMatchesForRound(Long roundId);

	List<? extends IMatchGroup> getMatchesWithPipelines();

//	void setFactories(IRoundFactory rf, ITeamGroupFactory tf);

	/**
	 * @param rf
	 * @param tf
	 */
	//void setFactories(IRoundFactory rf, ITeamGroupFactory tf);
	
	boolean delete(Long matchId);
}
