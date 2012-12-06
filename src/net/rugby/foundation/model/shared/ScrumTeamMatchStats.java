package net.rugby.foundation.model.shared;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class ScrumTeamMatchStats implements Serializable, ITeamMatchStats {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7786879877414979677L;

	@Id
	private Long id;
	
	private Integer tries;
	
	private Integer conversionsAttempted;
	private Integer conversionsMade;
	
	private Integer penaltiesAttempted;
	private Integer penaltiesMade;
	
	private Integer dropGoalsAttempted;
	private Integer dropGoalsMade;
	
	private Integer kicksFromHand;
	private Integer passes;
	private Integer runs;
	private Integer metersRun;
	
	private Float possesion;
	private Float territory;
	
	private Integer cleanBreaks;
	private Integer defendersBeaten;
	private Integer offloads;
	
	private Integer rucks;
	private Integer rucksWon;
	
	private Integer mauls;
	private Integer maulsWon;
	
	private Integer turnoversConceded;
	
	private Integer tacklesMade;
	private Integer tacklesMissed;
	
	private Integer scrumsPutIn;
	private Integer scrumsWonOnOwnPut;
	
	private Integer lineoutsThrownIn;
	private Integer lineoutsWonOnOwnThrow;
	
	private Integer penaltiesConceded;
	private Integer yellowCards;
	private Integer redCards;
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getTries()
	 */
	@Override
	public Integer getTries() {
		return tries;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setTries(java.lang.Integer)
	 */
	@Override
	public void setTries(Integer tries) {
		this.tries = tries;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getConversionsAttempted()
	 */
	@Override
	public Integer getConversionsAttempted() {
		return conversionsAttempted;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setConversionsAttempted(java.lang.Integer)
	 */
	@Override
	public void setConversionsAttempted(Integer conversionsAttempted) {
		this.conversionsAttempted = conversionsAttempted;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getConversionsMade()
	 */
	@Override
	public Integer getConversionsMade() {
		return conversionsMade;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setConversionsMade(java.lang.Integer)
	 */
	@Override
	public void setConversionsMade(Integer conversionsMade) {
		this.conversionsMade = conversionsMade;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getPenaltiesAttempted()
	 */
	@Override
	public Integer getPenaltiesAttempted() {
		return penaltiesAttempted;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setPenaltiesAttempted(java.lang.Integer)
	 */
	@Override
	public void setPenaltiesAttempted(Integer penaltiesAttempted) {
		this.penaltiesAttempted = penaltiesAttempted;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getPenaltiesMade()
	 */
	@Override
	public Integer getPenaltiesMade() {
		return penaltiesMade;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setPenaltiesMade(java.lang.Integer)
	 */
	@Override
	public void setPenaltiesMade(Integer penaltiesMade) {
		this.penaltiesMade = penaltiesMade;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getDropGoalsAttempted()
	 */
	@Override
	public Integer getDropGoalsAttempted() {
		return dropGoalsAttempted;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setDropGoalsAttempted(java.lang.Integer)
	 */
	@Override
	public void setDropGoalsAttempted(Integer dropGoalsAttempted) {
		this.dropGoalsAttempted = dropGoalsAttempted;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getDropGoalsMade()
	 */
	@Override
	public Integer getDropGoalsMade() {
		return dropGoalsMade;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setDropGoalsMade(java.lang.Integer)
	 */
	@Override
	public void setDropGoalsMade(Integer dropGoalsMade) {
		this.dropGoalsMade = dropGoalsMade;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getKicksFromHand()
	 */
	@Override
	public Integer getKicksFromHand() {
		return kicksFromHand;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setKicksFromHand(java.lang.Integer)
	 */
	@Override
	public void setKicksFromHand(Integer kicksFromHand) {
		this.kicksFromHand = kicksFromHand;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getPasses()
	 */
	@Override
	public Integer getPasses() {
		return passes;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setPasses(java.lang.Integer)
	 */
	@Override
	public void setPasses(Integer passes) {
		this.passes = passes;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getRuns()
	 */
	@Override
	public Integer getRuns() {
		return runs;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setRuns(java.lang.Integer)
	 */
	@Override
	public void setRuns(Integer runs) {
		this.runs = runs;
	}
	public Integer getMetersRun() {
		return metersRun;
	}
	public void setMetersRun(Integer metersRun) {
		this.metersRun = metersRun;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getPossesion()
	 */
	@Override
	public Float getPossesion() {
		return possesion;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setPossesion(java.lang.Float)
	 */
	@Override
	public void setPossesion(Float possesion) {
		this.possesion = possesion;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getTerritory()
	 */
	@Override
	public Float getTerritory() {
		return territory;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setTerritory(java.lang.Float)
	 */
	@Override
	public void setTerritory(Float territory) {
		this.territory = territory;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getCleanBreaks()
	 */
	@Override
	public Integer getCleanBreaks() {
		return cleanBreaks;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setCleanBreaks(java.lang.Integer)
	 */
	@Override
	public void setCleanBreaks(Integer cleanBreaks) {
		this.cleanBreaks = cleanBreaks;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getDefendersBeaten()
	 */
	@Override
	public Integer getDefendersBeaten() {
		return defendersBeaten;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setDefendersBeaten(java.lang.Integer)
	 */
	@Override
	public void setDefendersBeaten(Integer defendersBeaten) {
		this.defendersBeaten = defendersBeaten;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getOffloads()
	 */
	@Override
	public Integer getOffloads() {
		return offloads;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setOffloads(java.lang.Integer)
	 */
	@Override
	public void setOffloads(Integer offloads) {
		this.offloads = offloads;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getRucks()
	 */
	@Override
	public Integer getRucks() {
		return rucks;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setRucks(java.lang.Integer)
	 */
	@Override
	public void setRucks(Integer rucks) {
		this.rucks = rucks;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getRucksWon()
	 */
	@Override
	public Integer getRucksWon() {
		return rucksWon;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setRucksWon(java.lang.Integer)
	 */
	@Override
	public void setRucksWon(Integer rucksWon) {
		this.rucksWon = rucksWon;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getMauls()
	 */
	@Override
	public Integer getMauls() {
		return mauls;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setMauls(java.lang.Integer)
	 */
	@Override
	public void setMauls(Integer mauls) {
		this.mauls = mauls;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getMaulsWon()
	 */
	@Override
	public Integer getMaulsWon() {
		return maulsWon;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setMaulsWon(java.lang.Integer)
	 */
	@Override
	public void setMaulsWon(Integer maulsWon) {
		this.maulsWon = maulsWon;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getTurnoversConceded()
	 */
	@Override
	public Integer getTurnoversConceded() {
		return turnoversConceded;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setTurnoversConceded(java.lang.Integer)
	 */
	@Override
	public void setTurnoversConceded(Integer turnoversConceded) {
		this.turnoversConceded = turnoversConceded;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getTacklesMade()
	 */
	@Override
	public Integer getTacklesMade() {
		return tacklesMade;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setTacklesMade(java.lang.Integer)
	 */
	@Override
	public void setTacklesMade(Integer tacklesMade) {
		this.tacklesMade = tacklesMade;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getTacklesMissed()
	 */
	@Override
	public Integer getTacklesMissed() {
		return tacklesMissed;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setTacklesMissed(java.lang.Integer)
	 */
	@Override
	public void setTacklesMissed(Integer tacklesMissed) {
		this.tacklesMissed = tacklesMissed;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getScrumsPutIn()
	 */
	@Override
	public Integer getScrumsPutIn() {
		return scrumsPutIn;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setScrumsPutIn(java.lang.Integer)
	 */
	@Override
	public void setScrumsPutIn(Integer scrumsPutIn) {
		this.scrumsPutIn = scrumsPutIn;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getScrumsWonOnOwnPut()
	 */
	@Override
	public Integer getScrumsWonOnOwnPut() {
		return scrumsWonOnOwnPut;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setScrumsWonOnOwnPut(java.lang.Integer)
	 */
	@Override
	public void setScrumsWonOnOwnPut(Integer scrumsWonOnOwnPut) {
		this.scrumsWonOnOwnPut = scrumsWonOnOwnPut;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getLineoutsThrownIn()
	 */
	@Override
	public Integer getLineoutsThrownIn() {
		return lineoutsThrownIn;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setLineoutsThrownIn(java.lang.Integer)
	 */
	@Override
	public void setLineoutsThrownIn(Integer lineoutsThrownIn) {
		this.lineoutsThrownIn = lineoutsThrownIn;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getLineoutsWonOnOwnThrow()
	 */
	@Override
	public Integer getLineoutsWonOnOwnThrow() {
		return lineoutsWonOnOwnThrow;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setLineoutsWonOnOwnThrow(java.lang.Integer)
	 */
	@Override
	public void setLineoutsWonOnOwnThrow(Integer lineoutsWonOnOwnThrow) {
		this.lineoutsWonOnOwnThrow = lineoutsWonOnOwnThrow;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getPenaltiesConceded()
	 */
	@Override
	public Integer getPenaltiesConceded() {
		return penaltiesConceded;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setPenaltiesConceded(java.lang.Integer)
	 */
	@Override
	public void setPenaltiesConceded(Integer penaltiesConceded) {
		this.penaltiesConceded = penaltiesConceded;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getYellowCards()
	 */
	@Override
	public Integer getYellowCards() {
		return yellowCards;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setYellowCards(java.lang.Integer)
	 */
	@Override
	public void setYellowCards(Integer yellowCards) {
		this.yellowCards = yellowCards;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#getRedCards()
	 */
	@Override
	public Integer getRedCards() {
		return redCards;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamMatchStats#setRedCards(java.lang.Integer)
	 */
	@Override
	public void setRedCards(Integer redCards) {
		this.redCards = redCards;
	}
	
}
