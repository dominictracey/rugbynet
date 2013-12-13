/**
 * 
 */
package net.rugby.foundation.model.shared;

import java.util.Date;
import java.util.List;

/**
 * @author home
 *
 */
public interface ICompetition {

	public enum CompetitionType {HEINEKEN_CUP, AVIVA_PREMIERSHIP, SUPER_RUGBY, AUTUMN_INTERNATIONALS}

	public abstract Long getId();
	public abstract void setId(Long id);

	public abstract CompetitionType getCompType();
	public abstract void setCompType(CompetitionType t);
	
	public abstract String getLongName();
	public abstract void setLongName(String longName);

	public abstract String getShortName();
	public abstract void setShortName(String shortName);

	public abstract String getAbbr();
	public abstract void setAbbr(String abbr);

	public abstract Date getBegin();
	public abstract void setBegin(Date begin);
	public abstract Date getEnd();
	public abstract void setEnd(Date end);

	public abstract Boolean getUnderway();
	public abstract void setUnderway(Boolean underway);
	
	public abstract List<IRound> getRounds();
	public abstract void setRounds(List<IRound> rounds);

	public abstract List<Long> getRoundIds();
	public abstract void setRoundIds(List<Long> rounds);

	public abstract List<ITeamGroup> getTeams();
	public abstract void setTeams(List<ITeamGroup> teams);

	public abstract List<Long> getTeamIds();
	public abstract void setTeamIds(List<Long> teams);
	
	public abstract Long getForeignID();
	public abstract void setForeignID(Long foreignID);
	public abstract String getForeignURL();
	public abstract void setForeignURL(String foreignURL);

	public abstract IRound getNextRound();
	public abstract IRound getPrevRound();
	/**
	 * @param count
	 */
	public abstract void setPrevRoundIndex(int count);
	/**
	 * @param count
	 */
	public abstract void setNextRoundIndex(int count);
	/**
	 * @return
	 */
	int getNextRoundIndex();
	/**
	 * @return
	 */
	int getPrevRoundIndex();
	/**
	 * @return
	 */
	Long getCompClubhouseId();
	/**
	 * @param compClubhouse
	 */
	void setCompClubhouseId(Long compClubhouseId);


	/**
	 * Used for server-side caching porpoises
	 */
	Date getLastSaved();
	
	void setLastSaved(Date lastSaved);

	// don't put getNextRound and getPrevRound in here, see note at bottom of Competition.java
	//public abstract void setNextAndPrevRound();
	
}