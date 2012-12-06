package net.rugby.foundation.model.shared;

import java.io.Serializable;

import javax.persistence.Id;

import net.rugby.foundation.model.shared.Position.position;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class ScrumPlayerMatchStats implements Serializable, IPlayerMatchStats {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5287287099345934745L;
	
	@Id
	private Long id;
	
	private Integer tries;
	private Integer tryAssists;
	private Integer points;
	private Integer kicks;
	private Integer passes;
	private Integer runs;
	private Integer metersRun;
	private Integer cleanBreaks;
	private Integer defendersBeaten;
	private Integer offloads;
	private Integer turnovers;
	private Integer tacklesMade;
	private Integer tacklesMissed;
	private Integer lineoutsWonOnThrow;
	private Integer lineoutsStolenOnOppThrow;
	private Integer penaltiesConceded;
	private Integer yellowCards;
	private Integer redCards;

	private Long playerId;

	private Long matchId;

	private Long teamId;

	private position pos;
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getTries()
	 */
	@Override
	public Integer getTries() {
		return tries;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setTries(java.lang.Integer)
	 */
	@Override
	public void setTries(Integer tries) {
		this.tries = tries;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getTryAssists()
	 */
	@Override
	public Integer getTryAssists() {
		return tryAssists;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setTryAssists(java.lang.Integer)
	 */
	@Override
	public void setTryAssists(Integer tryAssists) {
		this.tryAssists = tryAssists;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getPoints()
	 */
	@Override
	public Integer getPoints() {
		return points;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setPoints(java.lang.Integer)
	 */
	@Override
	public void setPoints(Integer points) {
		this.points = points;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getKicks()
	 */
	@Override
	public Integer getKicks() {
		return kicks;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setKicks(java.lang.Integer)
	 */
	@Override
	public void setKicks(Integer kicks) {
		this.kicks = kicks;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getPasses()
	 */
	@Override
	public Integer getPasses() {
		return passes;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setPasses(java.lang.Integer)
	 */
	@Override
	public void setPasses(Integer passes) {
		this.passes = passes;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getRuns()
	 */
	@Override
	public Integer getRuns() {
		return runs;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setRuns(java.lang.Integer)
	 */
	@Override
	public void setRuns(Integer runs) {
		this.runs = runs;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getMetersRun()
	 */
	@Override
	public Integer getMetersRun() {
		return metersRun;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setMetersRun(java.lang.Integer)
	 */
	@Override
	public void setMetersRun(Integer metersRun) {
		this.metersRun = metersRun;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getCleanBreaks()
	 */
	@Override
	public Integer getCleanBreaks() {
		return cleanBreaks;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setCleanBreaks(java.lang.Integer)
	 */
	@Override
	public void setCleanBreaks(Integer cleanBreaks) {
		this.cleanBreaks = cleanBreaks;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getDefendersBeaten()
	 */
	@Override
	public Integer getDefendersBeaten() {
		return defendersBeaten;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setDefendersBeaten(java.lang.Integer)
	 */
	@Override
	public void setDefendersBeaten(Integer defendersBeaten) {
		this.defendersBeaten = defendersBeaten;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getOffloads()
	 */
	@Override
	public Integer getOffloads() {
		return offloads;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setOffloads(java.lang.Integer)
	 */
	@Override
	public void setOffloads(Integer offloads) {
		this.offloads = offloads;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getTurnovers()
	 */
	@Override
	public Integer getTurnovers() {
		return turnovers;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setTurnovers(java.lang.Integer)
	 */
	@Override
	public void setTurnovers(Integer turnovers) {
		this.turnovers = turnovers;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getTacklesMade()
	 */
	@Override
	public Integer getTacklesMade() {
		return tacklesMade;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setTacklesMade(java.lang.Integer)
	 */
	@Override
	public void setTacklesMade(Integer tacklesMade) {
		this.tacklesMade = tacklesMade;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getTacklesMissed()
	 */
	@Override
	public Integer getTacklesMissed() {
		return tacklesMissed;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setTacklesMissed(java.lang.Integer)
	 */
	@Override
	public void setTacklesMissed(Integer tacklesMissed) {
		this.tacklesMissed = tacklesMissed;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getLineoutsWonOnThrow()
	 */
	@Override
	public Integer getLineoutsWonOnThrow() {
		return lineoutsWonOnThrow;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setLineoutsWonOnThrow(java.lang.Integer)
	 */
	@Override
	public void setLineoutsWonOnThrow(Integer lineoutsWonOnThrow) {
		this.lineoutsWonOnThrow = lineoutsWonOnThrow;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getLineoutsStolenOnOppThrow()
	 */
	@Override
	public Integer getLineoutsStolenOnOppThrow() {
		return lineoutsStolenOnOppThrow;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setLineoutsStolenOnOppThrow(java.lang.Integer)
	 */
	@Override
	public void setLineoutsStolenOnOppThrow(Integer lineoutsStolenOnOppThrow) {
		this.lineoutsStolenOnOppThrow = lineoutsStolenOnOppThrow;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getPenaltiesConceded()
	 */
	@Override
	public Integer getPenaltiesConceded() {
		return penaltiesConceded;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setPenaltiesConceded(java.lang.Integer)
	 */
	@Override
	public void setPenaltiesConceded(Integer penaltiesConceded) {
		this.penaltiesConceded = penaltiesConceded;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getYellowCards()
	 */
	@Override
	public Integer getYellowCards() {
		return yellowCards;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setYellowCards(java.lang.Integer)
	 */
	@Override
	public void setYellowCards(Integer yellowCards) {
		this.yellowCards = yellowCards;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#getRedCards()
	 */
	@Override
	public Integer getRedCards() {
		return redCards;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStats#setRedCards(java.lang.Integer)
	 */
	@Override
	public void setRedCards(Integer redCards) {
		this.redCards = redCards;
	}
	@Override
	public Long getPlayerId() {
		return playerId;
	}
	@Override
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}
	@Override
	public Long getMatchId() {
		return matchId;
	}
	@Override
	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}
	@Override
	public Long getTeamId() {
		return teamId;
	}
	@Override
	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}
	@Override
	public position getPosition() {
		return pos;
	}
	@Override
	public void setPosition(Position.position pos) {
		this.pos = pos;
	}
	
}
