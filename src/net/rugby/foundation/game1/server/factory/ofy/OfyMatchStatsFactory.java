/**
 * 
 */
package net.rugby.foundation.game1.server.factory.ofy;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.game1.server.factory.IEntryFactory;
import net.rugby.foundation.game1.server.factory.IMatchStatsFactory;
import net.rugby.foundation.game1.shared.IMatchStats;
import net.rugby.foundation.game1.shared.MatchStats;
import net.rugby.foundation.model.shared.DataStoreFactory;

/**
 * @author home
 *
 */
public class OfyMatchStatsFactory implements IMatchStatsFactory {

	private Objectify ofy;
	private Long matchId;
	
	@Inject
	public OfyMatchStatsFactory() {
		this.ofy = DataStoreFactory.getOfy();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IMatchStatsFactory#setMatchId(java.lang.Long)
	 */
	@Override
	public void setMatchId(Long id) {
		matchId = id;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IMatchStatsFactory#getMatchStatsShard()
	 */
	@Override
	public IMatchStats getMatchStatsShard() {
		Query<MatchStats> qms = ofy.query(MatchStats.class).filter("matchId =", matchId);
		
		if (qms.count() == 0) {
			IMatchStats ms = new MatchStats();
			ms.setMatchId(matchId);
			ms.setNumHomePicks(0L);
			ms.setNumPicks(0L);
			put(ms); // do we need to do this now?
			return ms;
		}
		
		// TODO just returning the first (and only right now) 
		return qms.get();
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IMatchStatsFactory#put(net.rugby.foundation.game1.shared.IMatchStats)
	 */
	@Override
	public IMatchStats put(IMatchStats s) {
		ofy.put(s);
		return s;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IMatchStatsFactory#delete()
	 */
	@Override
	public Boolean delete() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IMatchStatsFactory#getAll()
	 */
	@Override
	public List<IMatchStats> getAll() {
		// TODO - why can't we get from qms.list() -> List<IMatchStats>?
		Query<MatchStats> qms = ofy.query(MatchStats.class).filter("matchId =", matchId);
		List<IMatchStats> list = new ArrayList<IMatchStats>();
		list.addAll(qms.list());
		return list;
	}

}
