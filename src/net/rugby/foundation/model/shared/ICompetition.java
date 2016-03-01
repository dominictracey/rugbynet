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

	public enum CompetitionType {

		HEINEKEN_CUP ("Heineken Cup", "Heineken Cup", "HNK", "HNK", "/resources/comps/HNK/200.png", "/resources/comps/HNK/200R.png", 1.1F, false), 
		AVIVA_PREMIERSHIP ("Aviva Premiership", "Premiership", "AP", "AP", "/resources/comps/AP/200.png", "/resources/comps/AP/200R.png", 1.1F, true), 
		PRO12 ("Guiness PRO12", "PRO12", "PRO12", "PRO12", "/resources/comps/PRO12/200.png", "/resources/comps/PRO12/200R.png", 1.1F, true), 
		TOP14 ("Orange Top 14", "Top 14", "TOP14", "TOP14", "/resources/comps/TOP14/200.png", "/resources/comps/TOP14/200R.png", 1.1F, true), 
		SUPER_RUGBY ("Super Rugby", "Super Rugby", "SR", "SR", "/resources/comps/SR/200.png", "/resources/comps/SR/200R.png", 1.1F, true), 
		CHAMPIONS_CUP ("European Champions Cup", "Champions Cup", "ERCC", "ERCC", "/resources/comps/ERCC/200.png", "/resources/comps/ERCC/200R.png", 1.2F, true), 
		CHALLENGE_CUP ("European Challenge Cup", "Challenge Cup", "ERC", "ERC", "/resources/comps/ERC/200.png", "/resources/comps/ERC/200R.png", 1.2F, true), 
		SIX_NATIONS ("Six Nations", "Six Nations", "6N", "6N", "/resources/comps/6N/200.png", "/resources/comps/6N/200R.png", 2.0F, true), 
		RUGBY_CHAMPIONSHIP ("The Rugby Championship", "Rugby Championship", "TRC", "TRC", "/resources/comps/TRC/200.png", "/resources/comps/NOV/200R.png", 2.0F, true), 
		AUTUMN_INTERNATIONALS ("Novemeber Internationals", "November Tests", "NOV", "NOV", "/resources/comps/TRC/200.png", "/resources/comps/NOV/200R.png", 2.0F, true), 
		JUNE_TOURS ("June Internationals", "June Tests", "JUN", "JUN", "/resources/comps/JUN/200.png", "/resources/comps/JUN/200R.png", 2.0F, true), 
		GLOBAL ("Global", "Global", "GLOBAL", "GLOBAL", "/resources/comps/GLOBAL/200.png", "/resources/comps/GLOBAL/200R.png", 4.0F, false);
		
		private String displayName;
		private String shortName;
		private String abbr;
		private String styleClass;
		private String squareImageUrl;
		private String rectImageUrl;
		private Float weight;
		private Boolean showToClient;
		
		private CompetitionType(String displayName, String shortName, String abbr,
				String styleClass, String squareImageUrl, String rectImageUrl,
				Float weight, Boolean showToClient) {
			this.displayName = displayName;
			this.shortName = shortName;
			this.abbr = abbr;
			this.styleClass = styleClass;
			this.squareImageUrl = squareImageUrl;
			this.rectImageUrl = rectImageUrl;
			this.weight = weight;
			this.showToClient = showToClient;
		}

		public String getDisplayName() {
			return displayName;
		}

		public String getShortName() {
			return shortName;
		}
		public String getAbbr() {
			return abbr;
		}

		public String getStyleClass() {
			return styleClass;
		}

		public String getSquareImageUrl() {
			return squareImageUrl;
		}

		public String getRectImageUrl() {
			return rectImageUrl;
		}

		public Float getWeight() {
			return weight;
		}

		public Boolean getShowToClient() {
			return showToClient;
		}

		
		
	}

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