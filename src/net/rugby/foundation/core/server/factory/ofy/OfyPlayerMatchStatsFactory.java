package net.rugby.foundation.core.server.factory.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ScrumPlayerMatchStats;

public class OfyPlayerMatchStatsFactory implements IPlayerMatchStatsFactory {
	private Objectify ofy;


	public OfyPlayerMatchStatsFactory() {
		this.ofy = DataStoreFactory.getOfy();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory#get()
	 */
	@Override
	public IPlayerMatchStats getById(Long id) {
		if (id != null)
			return ofy.get(new Key<ScrumPlayerMatchStats>(ScrumPlayerMatchStats.class,id));
		else
			return null;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory#put(net.rugby.foundation.model.shared.IPlayerMatchStats)
	 */
	@Override
	public IPlayerMatchStats put(IPlayerMatchStats tms) {
		ofy.put(tms);
		return tms;
	}

	
	public Boolean delete(IPlayerMatchStats val) {
		ofy.delete(val);
		return true;
	}
}
