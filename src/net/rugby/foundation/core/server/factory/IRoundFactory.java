package net.rugby.foundation.core.server.factory;

import net.rugby.foundation.model.shared.IRound;



public interface IRoundFactory extends ICachingFactory<IRound> {
	/**
	 * @return an existing round that matches the attributes of the passed in round (except id). Saves us from creating duplicates on accident.
	 */
	IRound find(IRound round);
	void invalidate(Long roundId);
	IRound getForUR(Long compId, int uROrdinal);
}
