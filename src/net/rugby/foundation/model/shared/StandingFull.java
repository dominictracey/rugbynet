package net.rugby.foundation.model.shared;

import java.io.Serializable;


import com.googlecode.objectify.annotation.Subclass;

@Subclass
public class StandingFull extends Standing implements Serializable, IStandingFull {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8101039555938496676L;
		
	protected int gamesPlayed;
	protected int wins;
	protected int draws;
	protected int losses;
	protected int byes;
	
	protected int pointsFor;
	protected int pointsAgainst;
	protected int triesFor;
	protected int triesAgainst;
	
	protected int bonusPointTries;
	protected int bonusPointLosses;
	protected int bonusPoints;
	
	protected int pointsDifferential;
	
	protected int points;
	
	protected String pool;

	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#getGamesPlayed()
	 */
	@Override
	public int getGamesPlayed() {
		return gamesPlayed;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#setGamesPlayed(int)
	 */
	@Override
	public void setGamesPlayed(int gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#getWins()
	 */
	@Override
	public int getWins() {
		return wins;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#setWins(int)
	 */
	@Override
	public void setWins(int wins) {
		this.wins = wins;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#getDraws()
	 */
	@Override
	public int getDraws() {
		return draws;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#setDraws(int)
	 */
	@Override
	public void setDraws(int draws) {
		this.draws = draws;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#getLosses()
	 */
	@Override
	public int getLosses() {
		return losses;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#setLosses(int)
	 */
	@Override
	public void setLosses(int losses) {
		this.losses = losses;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#getByes()
	 */
	@Override
	public int getByes() {
		return byes;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#setByes(int)
	 */
	@Override
	public void setByes(int byes) {
		this.byes = byes;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#getPointsFor()
	 */
	@Override
	public int getPointsFor() {
		return pointsFor;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#setPointsFor(int)
	 */
	@Override
	public void setPointsFor(int pointsFor) {
		this.pointsFor = pointsFor;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#getPointsAgainst()
	 */
	@Override
	public int getPointsAgainst() {
		return pointsAgainst;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#setPointsAgainst(int)
	 */
	@Override
	public void setPointsAgainst(int pointsAgainst) {
		this.pointsAgainst = pointsAgainst;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#getTriesFor()
	 */
	@Override
	public int getTriesFor() {
		return triesFor;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#setTriesFor(int)
	 */
	@Override
	public void setTriesFor(int triesFor) {
		this.triesFor = triesFor;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#getTriesAgainst()
	 */
	@Override
	public int getTriesAgainst() {
		return triesAgainst;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#setTriesAgainst(int)
	 */
	@Override
	public void setTriesAgainst(int triesAgainst) {
		this.triesAgainst = triesAgainst;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#getBonusPointTries()
	 */
	@Override
	public int getBonusPointTries() {
		return bonusPointTries;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#setBonusPointTries(int)
	 */
	@Override
	public void setBonusPointTries(int bonusPointTries) {
		this.bonusPointTries = bonusPointTries;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#getBonusPointLosses()
	 */
	@Override
	public int getBonusPointLosses() {
		return bonusPointLosses;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#setBonusPointLosses(int)
	 */
	@Override
	public void setBonusPointLosses(int bonusPointLosses) {
		this.bonusPointLosses = bonusPointLosses;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#getBonusPoints()
	 */
	@Override
	public int getBonusPoints() {
		return bonusPoints;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#setBonusPoints(int)
	 */
	@Override
	public void setBonusPoints(int bonusPoints) {
		this.bonusPoints = bonusPoints;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#getPointsDifferential()
	 */
	@Override
	public int getPointsDifferential() {
		return pointsDifferential;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#setPointsDifferential(int)
	 */
	@Override
	public void setPointsDifferential(int pointsDifferential) {
		this.pointsDifferential = pointsDifferential;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#getPoints()
	 */
	@Override
	public int getPoints() {
		return points;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IStandingFull#setPoints(int)
	 */
	@Override
	public void setPoints(int points) {
		this.points = points;
	}
	@Override
	public String getPool() {
		return pool;
	}
	@Override
	public void setPool(String pool) {
		this.pool = pool;
	}
}
