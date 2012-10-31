/**
 * 
 */
package net.rugby.foundation.game1.server.factory.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.rugby.foundation.game1.server.factory.IMatchStatsFactory;
import net.rugby.foundation.game1.shared.IMatchStats;
import net.rugby.foundation.game1.shared.MatchStats;

/**
 * @author home
 *
 */
public class TestMatchStatsFactory implements IMatchStatsFactory {

	private Long matchId;
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IMatchStatsFactory#setMatchId(java.lang.Long)
	 */
	@Override
	public void setMatchId(Long id) {
		this.matchId = id;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IMatchStatsFactory#getMatchStatsShard()
	 */
	@Override
	public IMatchStats getMatchStatsShard() {
		
		return new MatchStats(999876L,matchId,0L,0L);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IMatchStatsFactory#put(net.rugby.foundation.game1.shared.IMatchStats)
	 */
	@Override
	public IMatchStats put(IMatchStats s) {
		return s;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IMatchStatsFactory#delete()
	 */
	@Override
	public Boolean delete() {
		return true;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.IMatchStatsFactory#getAll()
	 */
	@Override
	public List<IMatchStats> getAll() {
		List<IMatchStats> list = new ArrayList<IMatchStats>();
		Random r = new Random();
		
		for (int i=0;i<1;++i) {
			Long numPicks = Math.abs((r.nextLong()) % 1000);
			list.add(new MatchStats(1L,matchId,numPicks,Math.abs((r.nextLong()) % numPicks)));
		}
		return list;
	}

}
