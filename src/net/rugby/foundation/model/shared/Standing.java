package net.rugby.foundation.model.shared;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class Standing implements Serializable, IStanding {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2506411242133263344L;
	
	@Id
	private Long id;
	private Long roundId;
	@Transient
	private IRound round;
	private Long teamId;
	@Transient 
	private ITeamGroup team;
	private Integer standing;
	private Long foreignId;

	public Standing() {
		
	}
	
	@Override
	public String toString() {
		return "Standing [roundId=" + roundId + ", teamId=" + teamId
				+ ", standing=" + standing + "]";
	}

	public Standing(Long roundId, Long teamId, Integer standing) {
		super();
		this.roundId = roundId;
		this.teamId = teamId;
		this.standing = standing;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStanding#getRoundId()
	 */
	@Override
	public Long getRoundId() {
		return roundId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStanding#setRoundId(java.lang.Long)
	 */
	@Override
	public void setRoundId(Long roundId) {
		this.roundId = roundId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStanding#getTeamId()
	 */
	@Override
	public Long getTeamId() {
		return teamId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStanding#setTeamId(java.lang.Long)
	 */
	@Override
	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStanding#getStanding()
	 */
	@Override
	public Integer getStanding() {
		return standing;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStanding#setStanding(java.lang.Integer)
	 */
	@Override
	public void setStanding(Integer standing) {
		this.standing = standing;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
		
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public IRound getRound() {
		return round;
	}

	@Override
	public void setRound(IRound round) {
		this.round = round;
	}

	@Override
	public ITeamGroup getTeam() {
		return team;
	}

	@Override
	public void setTeam(ITeamGroup team) {
		this.team = team;
	}
	@Override
	public Long getForeignId() {
		return foreignId;
	}
	@Override
	public void setForeignId(Long foreignId) {
		this.foreignId = foreignId;
	}
}
