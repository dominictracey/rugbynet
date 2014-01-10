package net.rugby.foundation.model.shared;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;


@Entity
public class PlayerMatchInfo implements Serializable, IPlayerMatchInfo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5627204923630726747L;
	
	
	@Id
	private Long id;
	
	private IPlayerMatchStats playerMatchStats; // this is bad when we don't mark it transient
	private IPlayerMatchRating matchRating;
	
	public PlayerMatchInfo() {
		
		
	}
	
	public PlayerMatchInfo(IPlayerMatchStats playerMatchStats,
			IPlayerMatchRating matchRating) {
		super();
		this.playerMatchStats = playerMatchStats;
		this.matchRating = matchRating;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IPlayerMatchInfo#getPlayerMatchStats()
	 */
	@Override
	public IPlayerMatchStats getPlayerMatchStats() {
		return playerMatchStats;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IPlayerMatchInfo#setPlayerMatchStats(net.rugby.foundation.model.shared.IPlayerMatchStats)
	 */
	@Override
	public void setPlayerMatchStats(IPlayerMatchStats playerMatchStats) {
		this.playerMatchStats = playerMatchStats;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IPlayerMatchInfo#getMatchRating()
	 */
	@Override
	public IPlayerMatchRating getMatchRating() {
		return matchRating;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IPlayerMatchInfo#setMatchRating(net.rugby.foundation.model.shared.IMatchRating)
	 */
	@Override
	public void setMatchRating(IPlayerMatchRating matchRating) {
		this.matchRating = matchRating;
	}

	
	
}
