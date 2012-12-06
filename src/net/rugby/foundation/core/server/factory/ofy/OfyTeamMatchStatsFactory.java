package net.rugby.foundation.core.server.factory.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.ScrumTeamMatchStats;

public class OfyTeamMatchStatsFactory implements ITeamMatchStatsFactory {
	private Objectify ofy;


	public OfyTeamMatchStatsFactory() {
		this.ofy = DataStoreFactory.getOfy();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory#get()
	 */
	@Override
	public ITeamMatchStats getById(Long id) {
		if (id != null)
			return ofy.get(new Key<ScrumTeamMatchStats>(ScrumTeamMatchStats.class,id));
		else
			return null;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory#put(net.rugby.foundation.model.shared.ITeamMatchStats)
	 */
	@Override
	public ITeamMatchStats put(ITeamMatchStats tms) {
		ofy.put(tms);
		return tms;
	}

	
	public Boolean delete(ITeamMatchStats val) {
		ofy.delete(val);
		return true;
	}
}
