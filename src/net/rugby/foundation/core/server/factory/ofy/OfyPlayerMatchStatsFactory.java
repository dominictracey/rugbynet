package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.admin.shared.PlayerMatchInfo;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.Group;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ScrumPlayerMatchStats;

public class OfyPlayerMatchStatsFactory implements IPlayerMatchStatsFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -175156134974074733L;
	//private Objectify ofy;


	public OfyPlayerMatchStatsFactory() {
		//this.ofy = DataStoreFactory.getOfy();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory#get()
	 */
	@Override
	public IPlayerMatchStats getById(Long id) {
		Objectify ofy = DataStoreFactory.getOfy();
		if (id != null)
			return ofy.get(new Key<ScrumPlayerMatchStats>(ScrumPlayerMatchStats.class,id));
		else
			return new ScrumPlayerMatchStats();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory#put(net.rugby.foundation.model.shared.IPlayerMatchStats)
	 */
	@Override
	public IPlayerMatchStats put(IPlayerMatchStats tms) {
		Objectify ofy = DataStoreFactory.getOfy();
		ofy.put(tms);
		return tms;
	}

	
	public Boolean delete(IPlayerMatchStats val) {
		Objectify ofy = DataStoreFactory.getOfy();
		ofy.delete(val);
		return true;
	}

	@Override
	public List<? extends IPlayerMatchStats> getByMatchId(Long matchId) {
		Objectify ofy = DataStoreFactory.getOfy();
		
		Query<ScrumPlayerMatchStats> qpms = ofy.query(ScrumPlayerMatchStats.class).filter("matchId",matchId).order("slot");
		
		return qpms.list();
	}
}
