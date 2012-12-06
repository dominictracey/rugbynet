package net.rugby.foundation.core.server.factory.test;


import java.io.Serializable;

import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.ScrumTeamMatchStats;

public class TestTeamMatchStatsFactory implements ITeamMatchStatsFactory, Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6305034768143121512L;

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.test.ITeamMatchFactory#getById(java.lang.Long)
	 */
	@Override
	public ITeamMatchStats getById(Long id) {
		if (id == null) {
			return new ScrumTeamMatchStats();
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.test.ITeamMatchFactory#put(net.rugby.foundation.model.shared.ITeamMatchStats)
	 */
	@Override
	public ITeamMatchStats put(ITeamMatchStats val) {
		return val;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.test.ITeamMatchFactory#delete(net.rugby.foundation.model.shared.ITeamMatchStats)
	 */
	@Override
	public Boolean delete(ITeamMatchStats val) {
		return true;
	}
}
