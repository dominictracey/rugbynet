package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class Competition implements Serializable, ICompetition {

	private static final long serialVersionUID = 1L;
	@Id
	private Long id;
	private String longName;
	private String shortName;
	private String abbr;
	private Date begin;
	private Date end;
	private Boolean underway;
	private Long foreignID;
	private String foreignURL;
	private Long compClubhouse;
	private Date lastSaved;
	
	private int nextRoundIndex;
	private int prevRoundIndex;

	
	List<Long> roundIDs = new ArrayList<Long>();
	@Transient
	List<IRound> rounds = new ArrayList<IRound>();
	
	List<Long> teamIDs = new ArrayList<Long>();
	@Transient 
	List<ITeamGroup> teams = new ArrayList<ITeamGroup>();
	private CompetitionType compType;
	
	public Long getCompClubhouse() {
		return compClubhouse;
	}

	public void setCompClubhouse(Long compClubhouse) {
		this.compClubhouse = compClubhouse;
	}

	public List<Long> getTeamIds() {
		return teamIDs;
	}

	public void setTeamIds(List<Long> teamIDs) {
		this.teamIDs = teamIDs;
	}

	public List<ITeamGroup> getTeams() {
		return teams;
	}

	public void setTeams(List<ITeamGroup> teams) {
		this.teams = teams;
	}

	
	public List<Long> getRoundIDs() {
		return roundIDs;
	}

	public void setRoundIDs(List<Long> roundIDs) {
		this.roundIDs = roundIDs;
	}
	
	@Override
	public int getNextRoundIndex() {
		return nextRoundIndex;
	}

	public void setNextRoundIndex(int nextRoundIndex) {
		if (nextRoundIndex < -1 || nextRoundIndex > this.roundIDs.size()) {
			System.out.println("Bad nextRoundIndex, must be between -1 (for not set) and number of rounds.");
		}

		this.nextRoundIndex = nextRoundIndex;
	}

	@Override
	public int getPrevRoundIndex() {
		return prevRoundIndex;
	}
	
	@Override
	public void setPrevRoundIndex(int prevRoundIndex) {
		if (prevRoundIndex < -1 || prevRoundIndex > this.roundIDs.size()) {
			System.out.println("Bad prevRoundIndex, must be between -1 (for not set) and number of rounds.");
		}
		this.prevRoundIndex = prevRoundIndex;
	}

	public Competition() {
		
	}

	public Competition(Long id, String longName, String shortName, String abbr,
			Date begin, Date end, Boolean underway, ArrayList<Long> rounds) {
		super();
		this.id = id;
		this.longName = longName;
		this.shortName = shortName;
		this.abbr = abbr;
		this.begin = begin;
		this.end = end;
		this.underway = underway;
		this.roundIDs = rounds;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#getLongName()
	 */
	@Override
	public String getLongName() {
		return longName;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#setLongName(java.lang.String)
	 */
	@Override
	public void setLongName(String longName) {
		this.longName = longName;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#getShortName()
	 */
	@Override
	public String getShortName() {
		return shortName;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#setShortName(java.lang.String)
	 */
	@Override
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#getAbbr()
	 */
	@Override
	public String getAbbr() {
		return abbr;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#setAbbr(java.lang.String)
	 */
	@Override
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#getBegin()
	 */
	@Override
	public Date getBegin() {
		return begin;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#setBegin(java.util.Date)
	 */
	@Override
	public void setBegin(Date begin) {
		this.begin = begin;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#getEnd()
	 */
	@Override
	public Date getEnd() {
		return end;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#setEnd(java.util.Date)
	 */
	@Override
	public void setEnd(Date end) {
		this.end = end;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#getUnderway()
	 */
	@Override
	public Boolean getUnderway() {
		return underway;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#setUnderway(java.lang.Boolean)
	 */
	@Override
	public void setUnderway(Boolean underway) {
		this.underway = underway;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#getRounds()
	 */
	@Override
	public List<Long> getRoundIds() {
		return roundIDs;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#setRounds(java.util.ArrayList)
	 */
	@Override
	public void setRoundIds(List<Long> rounds) {
		this.roundIDs = rounds;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#addRoundID(java.lang.Long)
	 */
//	@Override
//	public void addRoundID(Long id2) {
//		roundIDs.add(id2);
//		
//	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#getForeignID()
	 */
	@Override
	public Long getForeignID() {
		return foreignID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#setForeignID(java.lang.Long)
	 */
	@Override
	public void setForeignID(Long foreignID) {
		this.foreignID = foreignID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#getForeignURL()
	 */
	@Override
	public String getForeignURL() {
		return foreignURL;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#setForeignURL(java.lang.String)
	 */
	@Override
	public void setForeignURL(String foreignURL) {
		this.foreignURL = foreignURL;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#getRounds()
	 */
	@Override
	public List<IRound> getRounds() {
		return rounds;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#setRounds(java.util.ArrayList)
	 */
	@Override
	public void setRounds(List<IRound> rounds) {
		this.rounds = rounds;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#getNextRound()
	 */
	@Override
	public IRound getNextRound() {
		if (nextRoundIndex == -1) {
			return null;
		} else {
			return this.rounds.get(nextRoundIndex);
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#getPrevRound()
	 */
	@Override
	public IRound getPrevRound() {
		if (prevRoundIndex == -1) {
			return null;
		} else {
			return this.rounds.get(prevRoundIndex);
		}
	}
	@Override
	public Long getCompClubhouseId() {
		return compClubhouse;
	}
	@Override
	public void setCompClubhouseId(Long compClubhouse) {
		this.compClubhouse = compClubhouse;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#getLastSaved()
	 */
	@Override
	public Date getLastSaved() {
		return lastSaved;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ICompetition#setLastSaved(java.util.Date)
	 */
	@Override
	public void setLastSaved(Date lastSaved) {
		this.lastSaved = lastSaved;	
	}

	@Override
	public CompetitionType getCompType() {
		return compType;
	}

	@Override
	public void setCompType(CompetitionType t) {
		this.compType = t;
	}




	/*
	 * ARE YOU BACK HERE PUTTING IN SETNEXTROUND AND SETPREVROUND AGAIN?! WELL CUT IT OUT, THAT CODE DOESN'T
	 * NEED TO GET PUSHED TO THE CLIENT. JUST DO IT IN THE FACTORY OR MAYBE THE ORCHESTRATION.
	 */

}
