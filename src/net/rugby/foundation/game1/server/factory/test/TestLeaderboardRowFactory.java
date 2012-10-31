/**
 * 
 */
package net.rugby.foundation.game1.server.factory.test;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.game1.server.factory.ILeaderboardRowFactory;
import net.rugby.foundation.game1.shared.ILeaderboardRow;
import net.rugby.foundation.game1.shared.LeaderboardRow;
import net.rugby.foundation.model.shared.IAppUser;

/**
 * @author Dominic Tracey
 * 
 * This factory expects to get called with Ids 12000L-12014L as there are 7 test users and 2 test rounds complete. So
 * <ul>
 * <li> 12000L-12006L are for userIDs 7000L-7006L for round 1 (round Id 2L)
 * <li> 12007L-120014L are for round 2 (round Id 3L);
 * <li> and so forth
 * </ul>
 * 
 * The points scored and ranks are fake.
 */
public class TestLeaderboardRowFactory implements ILeaderboardRowFactory {

	private Long id;
	private IAppUserFactory auf;
	
	@Inject
	TestLeaderboardRowFactory(IAppUserFactory auf) {
		this.auf = auf;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.ILeaderboardRowFactory#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.ILeaderboardRowFactory#get()
	 */
	@Override
	public ILeaderboardRow get() {
		ILeaderboardRow lbr = new LeaderboardRow();
		
		// each user (7000L-7006L) has two round entries (12000L-12014L) so we have to see whether
		// the LBR id is even (round 1 is RoundID 2L) or odd (round 2 is RoundId 3L).
		if (id < 12007L)
			lbr = configureLBR(lbr, id-5000L, 2L);
		else
			lbr = configureLBR(lbr, id-5007L, 3L);

		return lbr;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.ILeaderboardRowFactory#put(net.rugby.foundation.game1.shared.ILeaderboardRow)
	 */
	@Override
	public ILeaderboardRow put(ILeaderboardRow lbr) {
		
		return lbr;
	}
	
	private ILeaderboardRow configureLBR(ILeaderboardRow lbr, Long appUserId, Long roundId) {
		lbr.setAppUserId(appUserId);
		auf.setId(appUserId);
		IAppUser au = auf.get();
		lbr.setAppUserName(au.getNickname());

		lbr.setId(id);
		
		lbr.setRank((int) (appUserId - 6999L));
		lbr.getScores().add(2);
		lbr.getScores().add(2);
		lbr.getScores().add(null);
		lbr.getScores().add(null);
		
		lbr.setTotal(7);
		
		return lbr;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.ILeaderboardRowFactory#delete()
	 */
	@Override
	public void delete() {
		//no op
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.ILeaderboardRowFactory#cloneFrom()
	 */
	@Override
	public ILeaderboardRow cloneFrom() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.ILeaderboardRowFactory#getNew()
	 */
	@Override
	public ILeaderboardRow getNew() {
		return new LeaderboardRow();
	}

}
