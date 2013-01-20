package net.rugby.foundation.core.server.factory.test;


import java.io.Serializable;
import java.util.List;

import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ScrumPlayerMatchStats;

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
	public List<? extends IPlayerMatchStats> getByMatchId(Long matchId) {
		// TODO Auto-generated method stub
		return null;
	}
}
