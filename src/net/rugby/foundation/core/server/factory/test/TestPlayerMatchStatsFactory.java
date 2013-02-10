package net.rugby.foundation.core.server.factory.test;


import java.io.Serializable;
import java.util.List;

import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.IPlayerMatchStatTimeLog;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ScrumPlayerMatchStats;
import net.rugby.foundation.model.shared.Position.position;

public class TestPlayerMatchStatsFactory implements IPlayerMatchStatsFactory, Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2975677607806066726L;

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.test.IPlayerMatchFactory#getById(java.lang.Long)
	 */
	@Override
	public IPlayerMatchStats getById(Long id) {
		if (id == null) {
			return new ScrumPlayerMatchStats();
		} 
//		else if (id.equals(1000L)) {
//			return new ScrumPlayerMatchStats(id, 1, 0,
//				 5,  2,  4, 7,
//					65, 1, 0,
//					0, 0, 12,
//					1, 2,
//					0, 1,
//					0, 0, 14, 9001014L,
//					300L, 9001L, position.FLANKER, "McCaw",
//					null, 80);
//			
//		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.test.IPlayerMatchFactory#put(net.rugby.foundation.model.shared.IPlayerMatchStats)
	 */
	@Override
	public IPlayerMatchStats put(IPlayerMatchStats val) {
		return val;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.test.IPlayerMatchFactory#delete(net.rugby.foundation.model.shared.IPlayerMatchStats)
	 */
	@Override
	public Boolean delete(IPlayerMatchStats val) {
		return true;
	}

	@Override
	public List<IPlayerMatchStats> getByMatchId(Long matchId) {
		// TODO Auto-generated method stub
		return null;
	}
}
