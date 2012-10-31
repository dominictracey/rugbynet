/**
 * 
 */
package net.rugby.foundation.game1.server.factory.ofy;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.game1.server.factory.ILeaderboardRowFactory;
import net.rugby.foundation.game1.shared.ILeaderboardRow;
import net.rugby.foundation.game1.shared.LeaderboardRow;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IAppUser;

/**
 * @author home
 *
 */
public class OfyLeaderBoardRowFactory implements ILeaderboardRowFactory {

	private Long id;
	private IAppUserFactory auf;
	private Objectify ofy;

	
	@Inject
	OfyLeaderBoardRowFactory(IAppUserFactory auf) {
		this.ofy = DataStoreFactory.getOfy();
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
		ILeaderboardRow lbr = null; 
		if (id != null) {
			lbr = ofy.find(new Key<LeaderboardRow>(LeaderboardRow.class, id));
			if (lbr != null) {
				auf.setId(lbr.getAppUserId());
				IAppUser au = auf.get();
				lbr.setAppUserName(au.getNickname());
			}
		}
		return lbr;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.ILeaderboardRowFactory#put(net.rugby.foundation.game1.shared.ILeaderboardRow)
	 */
	@Override
	public ILeaderboardRow put(ILeaderboardRow lbr) {

		assert lbr.getAppUserId() != null;
		
		ofy.put(lbr);
		return lbr;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.ILeaderboardRowFactory#delete()
	 */
	@Override
	public void delete() {
		ofy.delete(new Key<LeaderboardRow>(LeaderboardRow.class, id));
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.ILeaderboardRowFactory#cloneFrom()
	 */
	@Override
	public ILeaderboardRow cloneFrom() {
		if (id == null) {
			return null;
		}
		
		ILeaderboardRow source = get();
		ILeaderboardRow lb = new LeaderboardRow();
		
		lb.setAppUserId(source.getAppUserId());
		lb.setAppUserName(source.getAppUserName());
		lb.setEntryId(source.getEntryId());
		lb.setRank(source.getRank());
		for (int score : source.getScores())
			lb.getScores().add(score);
		lb.setTieBreakerFactor(source.getTieBreakerFactor());
		lb.setTotal(source.getTotal());
		
		return lb;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.ILeaderboardRowFactory#getNew()
	 */
	@Override
	public ILeaderboardRow getNew() {
		return new LeaderboardRow();
	}



}
