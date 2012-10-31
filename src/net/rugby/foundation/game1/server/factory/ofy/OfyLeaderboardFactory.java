/**
 * 
 */
package net.rugby.foundation.game1.server.factory.ofy;

import java.util.ArrayList;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.game1.server.factory.ILeaderboardFactory;
import net.rugby.foundation.game1.server.factory.ILeaderboardRowFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.ILeaderboard;
import net.rugby.foundation.game1.shared.ILeaderboardRow;
import net.rugby.foundation.game1.shared.Leaderboard;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IRound;

/**
 * @author home
 *
 */
public class OfyLeaderboardFactory implements ILeaderboardFactory {

	private Long id;
	private Objectify ofy;
	ILeaderboardRowFactory lbrf;
	ICompetitionFactory cf;
	IRoundFactory rf;

	private ILeagueFactory lf;
	private IClubhouseLeagueMap clm;
	private IRound round;
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.ILeaderboardFactory#setId(java.lang.Long)
	 */
	@Inject
	public OfyLeaderboardFactory(ILeaderboardRowFactory lbrf, ICompetitionFactory cf, IRoundFactory rf, ILeagueFactory lf) {
		this.ofy = DataStoreFactory.getOfy();
		this.lbrf = lbrf;
		this.cf = cf;
		this.rf = rf;
		this.lf = lf;
	}
	
	@Override
	public void setId(Long id) {
		this.id = id;
		this.round = null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.ILeaderboardFactory#get()
	 */
	@Override
	public ILeaderboard get() {
		ILeaderboard lb = null;

		if (id != null) {
			lb = ofy.get(new Key<Leaderboard>(Leaderboard.class,id));
		} else if (clm != null) {
			lb = ofy.query(Leaderboard.class).filter("roundId",round.getId()).filter("leagueId", clm.getLeagueId()).get();
			if (lb == null)
				return null; // RuleLeaderboardNeeded checks this to see if it is true
		}

		if (lb != null) {
			cf.setId(lb.getCompId());
			lb.setComp(cf.getCompetition());

			rf.setId(lb.getRoundId());
			lb.setRound(rf.getRound());

			lf.setId(lb.getLeagueId()); 
			lb.setLeague(lf.get());

			for (Long lbrid : lb.getRowIds()) {
				lbrf.setId(lbrid);
				lb.getRows().add(lbrf.get());
			}
			return lb;
		} else {
			return new Leaderboard();
		}

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.ILeaderboardFactory#put(net.rugby.foundation.game1.shared.ILeaderboard)
	 */
	@Override
	public ILeaderboard put(ILeaderboard lb) {
		if (lb.sanityCheck()) {
		
			// delete any LBRs that currently exist
			for (ILeaderboardRow lbr : lb.getRows()) {
				if (lbr.getId() != null) {
					lbrf.setId(lbr.getId());
					lbrf.delete();
				}
			}
			
			//now save them - preserving the order
			lb.setRowIds(new ArrayList<Long>());
			for (ILeaderboardRow lbr : lb.getRows()) {
				lbr = lbrf.put(lbr);
				lb.getRowIds().add(lbr.getId());
			}
			
			ofy.put(lb);
			
			
			return lb;
		} else {
			return null;
		}
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
		if (id == null)
			return null;
		
		ILeaderboard lb = new Leaderboard();
		ILeaderboard source = get();
		lb.setComp(source.getComp());
		lb.setCompId(source.getCompId());
		lb.setLeague(source.getLeague());
		lb.setLeagueId(source.getLeagueId());
		lb.setRound(source.getRound());
		lb.setRoundId(source.getRoundId());
		lb.setRoundNames(source.getRoundNames());
		
		for (Long lbrid : source.getRowIds()) {
			lbrf.setId(lbrid);
			lb.getRows().add(lbrf.cloneFrom());
		}
		
		
		return lb;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.server.factory.ILeaderboardFactory#getNew()
	 */
	@Override
	public ILeaderboard getNew() {
		return new Leaderboard();
	}

}
