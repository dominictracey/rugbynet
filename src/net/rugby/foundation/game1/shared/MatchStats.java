/**
 * 
 */
package net.rugby.foundation.game1.shared;

import java.io.Serializable;

import javax.persistence.Id;
import com.googlecode.objectify.annotation.Entity;

/**
 * @author Dominic Tracey
 *
 */
@Entity
public class MatchStats implements IMatchStats, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	private Long matchId;
	private Long numPicks;
	private Long numHomePicks;
	
	public MatchStats() {
		
	}
	
	public MatchStats (Long id, Long matchId, Long numPicks, Long numHomePicks) {
		this.id = id;
		this.matchId = matchId;
		this.numPicks = numPicks;
		this.numHomePicks = numHomePicks;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IMatchEntry#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IMatchEntry#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IMatchEntry#getMatchId()
	 */
	@Override
	public Long getMatchId() {
		return matchId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IMatchEntry#setMatchId(java.lang.Long)
	 */
	@Override
	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}

	@Override
	public Long getNumPicks() {
		return numPicks;
	}

	@Override
	public void setNumPicks(Long numPicks) {
		this.numPicks = numPicks;
	}

	@Override
	public Long getNumHomePicks() {
		return numHomePicks;
	}
	
	@Override
	public void setNumHomePicks(Long numHomePicks) {
		this.numHomePicks = numHomePicks;
	}

}
