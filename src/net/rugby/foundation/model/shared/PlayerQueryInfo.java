package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;


@Entity
public class PlayerQueryInfo implements Serializable, IPlayerQueryInfo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5627204923630726747L;
	
	
	@Id
	private Long id;
	
	private List<IPlayerMatchStats> playerMatchStats; // this is bad when we don't mark it transient
	private IPlayerMatchRating matchRating;
	
	public PlayerQueryInfo() {
		
		
	}
	
	public PlayerQueryInfo(List<IPlayerMatchStats> playerMatchStats,
			IPlayerMatchRating matchRating) {
		super();
		this.playerMatchStats = playerMatchStats;
		this.matchRating = matchRating;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IPlayerMatchInfo#getPlayerMatchStats()
	 */
	@Override
	public List<IPlayerMatchStats> getPlayerMatchStats() {
		return playerMatchStats;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IPlayerMatchInfo#setPlayerMatchStats(net.rugby.foundation.model.shared.IPlayerMatchStats)
	 */
	@Override
	public void setPlayerMatchStats(List<IPlayerMatchStats> playerMatchStats) {
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
