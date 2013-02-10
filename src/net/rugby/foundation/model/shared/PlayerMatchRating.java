package net.rugby.foundation.model.shared;

import javax.persistence.Transient;
import java.io.Serializable;

import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;



import com.googlecode.objectify.annotation.Subclass;

@Subclass
public class PlayerMatchRating extends PlayerRating implements IPlayerMatchRating, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4536867681836433478L;
	Long playerMatchStatsId;
	@Transient
	private IPlayerMatchStats playerMatchStats;
	
	public PlayerMatchRating() {
		super();
	}
	
	public PlayerMatchRating(Integer rating, IPlayer player, IGroup match,
			IMatchRatingEngineSchema schema, IPlayerMatchStats playerMatchStats) {
		super(rating, player, match, schema);
		
		this.playerMatchStats = playerMatchStats;
		this.playerMatchStatsId = playerMatchStats.getId();
	}

	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchRating#getPlayerMatchStats()
	 */
	@Override
	public IPlayerMatchStats getPlayerMatchStats() {
		return playerMatchStats;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchRating#setPlayerMatchStats(java.util.List)
	 */
	@Override
	public void setPlayerMatchStats(IPlayerMatchStats playerMatchStats) {
		this.playerMatchStats = playerMatchStats;
	}

	public Long getPlayerMatchStatsId() {
		return playerMatchStatsId;
	}

	public void setPlayerMatchStatsId(Long playerMatchStatsId) {
		this.playerMatchStatsId = playerMatchStatsId;
	}

	
}
