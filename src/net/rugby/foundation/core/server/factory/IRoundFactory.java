package net.rugby.foundation.core.server.factory;

import net.rugby.foundation.model.shared.IRound;

public interface IRoundFactory {

	void setId(Long id);
	
	IRound getRound();
	
	/**
	 * @return an existing round that matches the attributes of the passed in round (except id). Saves us from creating duplicates on accident.
	 */
	IRound find(IRound round);
	IRound put(IRound r);

	/**
	 * @param roundId - force a memcache refresh
	 */
	void build(Long roundId);

//	void setFactories(ICompetitionFactory ofyCompetitionFactory,
//			IMatchGroupFactory mf);

	/**
	 * @param ofyCompetitionFactory
	 * @param mf
	 */
	//void setFactories(ICompetitionFactory cf, IMatchGroupFactory mf);
}
