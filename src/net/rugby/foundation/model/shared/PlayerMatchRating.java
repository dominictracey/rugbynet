package net.rugby.foundation.model.shared;

import java.io.Serializable;

import net.rugby.foundation.admin.shared.IRatingEngineSchema;



import com.googlecode.objectify.annotation.Subclass;

@Subclass
public class PlayerMatchRating extends PlayerRating implements IPlayerMatchRating, Serializable, Comparable<IPlayerMatchRating>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4536867681836433478L;
//	Long playerMatchStatsId;
//	@Transient
//	private IPlayerMatchStats playerMatchStats;
	private String details;
	
	public PlayerMatchRating() {
		super();
	}
	
	public PlayerMatchRating(Integer rating, IPlayer player, IGroup match,
			IRatingEngineSchema schema, IPlayerMatchStats playerMatchStats, String details, IRatingQuery query) {
		super(rating, player, match, schema, query);
		
		super.addMatchStats(playerMatchStats);
		//super.addplayerMatchStatsId(playerMatchStats.getId();
		
		this.setDetails(details);
	}

	
//	/* (non-Javadoc)
//	 * @see net.rugby.foundation.model.shared.IPlayerMatchRating#getPlayerMatchStats()
//	 */
//	@Override
//	public IPlayerMatchStats getPlayerMatchStats() {
//		return super.getPlayerMatchStats();
//	}
//	/* (non-Javadoc)
//	 * @see net.rugby.foundation.model.shared.IPlayerMatchRating#setPlayerMatchStats(java.util.List)
//	 */
//	@Override
//	public void setPlayerMatchStats(IPlayerMatchStats playerMatchStats) {
//		//this.playerMatchStats = playerMatchStats;
//		super.setPlayerMatchStats(playerMatchStats);
//	}

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
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
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
