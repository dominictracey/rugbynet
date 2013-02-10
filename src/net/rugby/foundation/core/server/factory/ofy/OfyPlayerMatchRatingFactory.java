package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.List;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IGroup;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.PlayerMatchRating;
import net.rugby.foundation.model.shared.PlayerRating;

public class OfyPlayerMatchRatingFactory implements IPlayerMatchRatingFactory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient IPlayerFactory pf;
	private transient IMatchGroupFactory mf;
	private transient IPlayerMatchStatsFactory pmsf;
	
	public OfyPlayerMatchRatingFactory() {

	}
	
	@Inject
	public void setFactories(IPlayerFactory pf, IMatchGroupFactory mf, IPlayerMatchStatsFactory pmsf) {
		this.pf = pf;
		this.mf = mf;
		this.pmsf = pmsf;
	}
	
	@Override
	public IPlayerMatchRating get(Long id) {
		Objectify ofy = DataStoreFactory.getOfy();
		
		// hydrate the transient objects from their stored Ids
		PlayerMatchRating pmr = ofy.get(new Key<PlayerMatchRating>(PlayerMatchRating.class, id));
		if (pf != null) {
			pmr.setPlayer(pf.getById(pmr.getPlayerId()));
		}
		
		if (pmsf != null) {
			pmr.setPlayerMatchStats(pmsf.getById(pmr.getPlayerMatchStatsId()));
			//have to have the PMR to get the match
			if (mf != null) {
				mf.setId(pmr.getPlayerMatchStats().getMatchId());
				pmr.setGroup((IGroup)mf.getGame());
			}
		}
		return pmr;
	}

	@Override
	public List<? extends IPlayerMatchRating> getForMatch(Long matchId) {
		Objectify ofy = DataStoreFactory.getOfy();
		Query<PlayerMatchRating> qpmr = ofy.query(PlayerMatchRating.class).filter("matchId", matchId);
		return qpmr.list();
	}

	@Override
	public IPlayerMatchRating getNew(IPlayer player, IMatchGroup match,
			Integer rating, IMatchRatingEngineSchema schema,
			IPlayerMatchStats playerMatchStats) {
		Objectify ofy = DataStoreFactory.getOfy();
		PlayerMatchRating pmr = new PlayerMatchRating(rating, player, (IGroup)match, schema, playerMatchStats);
		ofy.put(pmr);
		return pmr;
	}
	
	@Override
	public IPlayerMatchRating put(IPlayerMatchRating pmr) {
		Objectify ofy = DataStoreFactory.getOfy();
		// only one per match
		if (pmr != null && pmr.getPlayerMatchStats() != null) {
			IPlayerMatchRating existing = get(pmr.getPlayerMatchStats().getPlayerId(), pmr.getPlayerMatchStats().getMatchId());
			if (existing != null) {
				ofy.delete(existing);
			}
		}
		
		if (pmr != null) {
			ofy.put(pmr);
		}
		
		return pmr;
	}

	@Override
	public IPlayerMatchRating get(Long playerId, Long matchId) {
		Objectify ofy = DataStoreFactory.getOfy();
		Query<PlayerRating> qpmr = ofy.query(PlayerRating.class).filter("playerId", playerId).filter("playerMatchStatsId", matchId);
		IPlayerRating qr = qpmr.get();
		if (qr instanceof IPlayerMatchRating) {
			return (IPlayerMatchRating)qr;
		} else {
			return null;
		}
	}

}
