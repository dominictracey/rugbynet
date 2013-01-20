package net.rugby.foundation.admin.shared;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;

import net.rugby.foundation.model.shared.IMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;

@Entity
public class PlayerMatchInfo implements Serializable, IPlayerMatchInfo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5627204923630726747L;
	@Transient
	IPlayerMatchStats playerMatchStats;
//	@Transient
//	IMatchRating matchRating;
	
	public PlayerMatchInfo() {
		
		
	}
	
	@Id
	private Long id;
	
	public PlayerMatchInfo(IPlayerMatchStats playerMatchStats) {//,
			//IMatchRating matchRating) {
		super();
		this.playerMatchStats = playerMatchStats;
		//this.matchRating = matchRating;
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
//	/* (non-Javadoc)
//	 * @see net.rugby.foundation.admin.shared.IPlayerMatchInfo#getMatchRating()
//	 */
//	@Override
//	public IMatchRating getMatchRating() {
//		return matchRating;
//	}
//	/* (non-Javadoc)
//	 * @see net.rugby.foundation.admin.shared.IPlayerMatchInfo#setMatchRating(net.rugby.foundation.model.shared.IMatchRating)
//	 */
//	@Override
//	public void setMatchRating(IMatchRating matchRating) {
//		this.matchRating = matchRating;
//	}

	
	
}
