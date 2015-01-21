/**
 * 
 */
package net.rugby.foundation.model.shared;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author home
 *
 */
public interface ICompetition extends IHasId {

	public enum CompetitionType {HEINEKEN_CUP, AVIVA_PREMIERSHIP, SUPER_RUGBY, AUTUMN_INTERNATIONALS, GLOBAL}

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

	/**
	 * 
	 * @return the weightingFactor allows us to compare across competitions. 1F is the 
	 * Aviva Premiership and other comps vary from this. So NZL vs AUS might be 2F and
	 * USA Superleague might be .3F...
	 */
	public abstract Float getWeightingFactor();
	public abstract void setWeightingFactor(Float weightingFactor);
	public abstract void setTwitter(String twitter);
	public abstract String getTwitter();
	void setTwitterChannel2(String twitterChannel2);
	String getTwitterChannel2();
	void setTwitterChannel1(String twitterChannel1);
	String getTwitterChannel1();
	void setImageUrl(String imageUrl);
	String getImageUrl();
	HashMap<RatingMode, Long> getSeriesMap();
	public abstract String getTTLTitleDesc();
	void setTTLTitleDesc(String tTLTitleDesc);
	void setSponsor(Sponsor sponsor);
	Sponsor getSponsor();
	void setSponsorId(Long sponsorId);
	Long getSponsorId();
	List<Long> getComponentCompIds();
	void setComponentCompIds(List<Long> componentCompIds);
	void setShowToClient(Boolean showToClient);
	Boolean getShowToClient();
	
	// don't put getNextRound and getPrevRound in here, see note at bottom of Competition.java
	//public abstract void setNextAndPrevRound();
	
}