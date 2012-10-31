/**
 * 
 */
package net.rugby.foundation.game1.server.factory.test;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.game1.server.factory.ILeaderboardFactory;
import net.rugby.foundation.game1.server.factory.ILeaderboardRowFactory;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.ILeaderboard;
import net.rugby.foundation.game1.shared.Leaderboard;
import net.rugby.foundation.model.shared.IRound;

/**
 * @author home
 *
 */
public class TestLeaderboardFactory implements ILeaderboardFactory {

	private Long id;
	private ILeaderboardRowFactory lbrf;
	private ICompetitionFactory cf;
	private IClubhouseLeagueMap clm;
	private IRound round;
	
	@Inject
	TestLeaderboardFactory(ILeaderboardRowFactory lbrf, ICompetitionFactory cf) {
		this.lbrf = lbrf;
		this.cf = cf;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.ILeaderboardFactory#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
		this.clm = null;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.ILeaderboardFactory#get()
	 */
	@Override
	public ILeaderboard get() {
		
		return null;
		
//		// This basically doesn't work...
//		
//		if (clm == null && id == null) {
//			return new Leaderboard(); //building new
//		}
//		
//		 // this is the test case where they are checking if they need to build. YES!
////		if (clm.getRoundId() == 3L) {
////			return null;
////		}
//		
//		ILeaderboard lb = new Leaderboard();
//		lb.setId(id);
//		
//		lb.setCompId(1L);
//		cf.setId(1L);
//		lb.setComp(cf.getCompetition());
//		
//		for (Long i=12007L; i<12013L; ++i) {
//			lbrf.setId(i);
//			lb.getRowIds().add(i);
//			lb.getRows().add(lbrf.get());
//		}
//		
//		return lb;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.ILeaderboardFactory#put(net.rugby.foundation.game1.shared.ILeaderboard)
	 */
	@Override
	public ILeaderboard put(ILeaderboard lb) {
		if (lb.sanityCheck())
			return lb;
		else
			return null;
	}



	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.ILeaderboardFactory#setClmAndRound(net.rugby.foundation.game1.shared.IClubhouseLeagueMap, net.rugby.foundation.model.shared.IRound)
	 */
	@Override
	public void setClmAndRound(IClubhouseLeagueMap clm, IRound round) {
		this.clm = clm;		
		this.id = null;
		this.round = round;
				
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.ILeaderboardFactory#cloneFrom()
	 */
	@Override
	public ILeaderboard cloneFrom() {
		// TODO Auto-generated method stub
		return null;
	}
	

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.ILeaderboardFactory#getNew()
	 */
	@Override
	public ILeaderboard getNew() {
		return new Leaderboard();
	}

}
