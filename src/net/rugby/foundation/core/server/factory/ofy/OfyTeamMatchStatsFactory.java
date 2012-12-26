package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.ScrumTeamMatchStats;

public class OfyTeamMatchStatsFactory implements ITeamMatchStatsFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9132591058693886799L;
	//private Objectify ofy;


	public OfyTeamMatchStatsFactory() {
		//this.ofy = DataStoreFactory.getOfy();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory#get()
	 */
	@Override
	public ITeamMatchStats getById(Long id) {
		Objectify ofy = DataStoreFactory.getOfy();
		if (id != null)
			return ofy.get(new Key<ScrumTeamMatchStats>(ScrumTeamMatchStats.class,id));
		else
			return new ScrumTeamMatchStats();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory#put(net.rugby.foundation.model.shared.ITeamMatchStats)
	 */
	@Override
	public ITeamMatchStats put(ITeamMatchStats tms) {
		Objectify ofy = DataStoreFactory.getOfy();
		ofy.put(tms);
		return tms;
	}

	
	public Boolean delete(ITeamMatchStats val) {
		Objectify ofy = DataStoreFactory.getOfy();
		ofy.delete(val);
		return true;
	}
}
