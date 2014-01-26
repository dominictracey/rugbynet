package net.rugby.foundation.model.shared;

import java.io.Serializable;

import net.rugby.foundation.admin.shared.IRatingEngineSchema;



import com.googlecode.objectify.annotation.Subclass;

@Subclass
public class PlayerMatchRating extends PlayerRating implements IPlayerMatchRating, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4536867681836433478L;
	
	public PlayerMatchRating() {
		super();
	}
	
	public PlayerMatchRating(Integer rating, IPlayer player, IGroup match,
			IRatingEngineSchema schema, IPlayerMatchStats playerMatchStats, String details, IRatingQuery query) {
		super(rating, player, match, schema, query, details);
		
		super.addMatchStats(playerMatchStats);
		
	}

	@Override
	public IPlayerMatchStats getPlayerMatchStats() {
		if (super.getMatchStats() != null) {
			return super.getMatchStats().get(0);
		} else {
			return null;
		}
	}

	@Override
	public void setPlayerMatchStats(IPlayerMatchStats playerMatchStats) {
		super.setMatchStats(null);
		super.setMatchStatIds(null);
		super.addMatchStats(playerMatchStats);
	}
	

	@Override
	public Long getPlayerMatchStatsId() {
		if (super.getMatchStatIds() != null) {
			return super.getMatchStatIds().get(0);
		} else {
			return null;
		}
	}

	@Override
	public void setPlayerMatchStatsId(Long playerMatchStatsId) {
		super.setMatchStats(null);
		super.setMatchStatIds(null);
		super.addMatchStatId(playerMatchStatsId);
	}

	
}
