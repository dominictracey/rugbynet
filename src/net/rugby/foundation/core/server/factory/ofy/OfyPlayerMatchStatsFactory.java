package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.Group;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.PlayerMatchInfo;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.ScrumPlayerMatchStats;
import net.rugby.foundation.model.shared.ScrumTeamMatchStats;

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
		
		// only one per player per match
		ScrumPlayerMatchStats existing = ofy.query(ScrumPlayerMatchStats.class).filter("playerId", tms.getPlayerId()).filter("matchId", tms.getMatchId()).get();

		if (existing != null) {
			ofy.delete(existing);
		}
		
		ofy.put(tms);
		return tms;
	}

	
	public Boolean delete(IPlayerMatchStats val) {
		Objectify ofy = DataStoreFactory.getOfy();
		ofy.delete(val);
		return true;
	}

	@Override
	public List<IPlayerMatchStats> getByMatchId(Long matchId) {
		Objectify ofy = DataStoreFactory.getOfy();
		
		Query<ScrumPlayerMatchStats> qpms = ofy.query(ScrumPlayerMatchStats.class).filter("matchId",matchId).order("teamId").order("slot");
		
		List<IPlayerMatchStats> list = new ArrayList<IPlayerMatchStats>();
		
		for (ScrumPlayerMatchStats spms : qpms) {
			list.add(spms);
		}
		
		return list;
		//return qpms.list();  // wish we could do this
	}

	@Override
	public List<IPlayerMatchStats> query(List<Long> matchIds,
			position posi, Long countryId, Long teamId) {
		Objectify ofy = DataStoreFactory.getOfy();
		
		
		Query<ScrumPlayerMatchStats> qpms = ofy.query(ScrumPlayerMatchStats.class).filter("matchId in",matchIds);
		if (posi != null && !posi.equals(position.NONE)) {
			qpms = qpms.filter("pos",posi);
		}
		
		//TODO should make country.NONE a constant
		if (countryId != null && countryId != 5000L) {
			qpms = qpms.filter("countryId", countryId);
		}
		
		if (teamId != null  && teamId != -1) {
			qpms = qpms.filter("teamId", teamId);
		}
		
		qpms = qpms.order("teamId").order("slot");
		
		List<IPlayerMatchStats> list = new ArrayList<IPlayerMatchStats>();
		
		for (ScrumPlayerMatchStats spms : qpms) {
			list.add(spms);
		}
		
		return list;
	}
}
