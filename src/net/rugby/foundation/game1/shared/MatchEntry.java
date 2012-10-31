/**
 * 
 */
package net.rugby.foundation.game1.shared;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;

import net.rugby.foundation.model.shared.ITeamGroup;

/**
 * @author Dominic Tracey
 *
 */
@Entity
public class MatchEntry implements IMatchEntry, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	private Long matchId;
	private Long teamPickedId;
	
	@Transient
	private ITeamGroup teamPicked;
	public MatchEntry() {
		
	}
	
	public MatchEntry (Long id, Long matchId, Long teamPickedId) {
		this.id = id;
		this.matchId = matchId;
		this.teamPickedId = teamPickedId;
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

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IMatchEntry#getTeamPickedId()
	 */
	@Override
	public Long getTeamPickedId() {
		return teamPickedId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IMatchEntry#getTeamPickedId(java.lang.Long)
	 */
	@Override
	public void setTeamPickedId(Long teamId) {
		this.teamPickedId = teamId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IMatchEntry#getTeamPicked()
	 */
	@Override
	public ITeamGroup getTeamPicked() {
		return teamPicked;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IMatchEntry#getTeamPickedId(net.rugby.foundation.model.shared.ITeamGroup)
	 */
	@Override
	public void setTeamPicked(ITeamGroup team) {
		this.teamPicked = team;
	}

}
