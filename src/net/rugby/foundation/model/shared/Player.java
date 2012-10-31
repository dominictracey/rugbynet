package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

import net.rugby.foundation.model.shared.MatchPopupData;
import net.rugby.foundation.model.shared.Position;



/*
import com.google.gwt.sample.Players.client.ClientSerializable;
import com.google.gwt.sample.Players.client.ClientSerializableField;
*/

@SuppressWarnings("serial")
@Entity
public class Player implements Serializable /*, ClientSerializable*/ {
  
	public enum movement { UP, DOWN, UNCHANGED }
	
	@Id
	private Long id;
	private Long teamID;
	private String teamName;
	private String teamAbbr;
	private String pool;
	
	protected String givenName;
	protected String surName;
	protected String displayName;
	protected Position.position position;
	protected Position.position[] altposition = null;
	protected Date dateOfBirth;
	protected Integer height;   //cm
	protected Integer weight;   //kg
	protected boolean active;

	protected Integer numberCaps;
	protected String club;
	protected String bioSnippet;
	protected String bioURL;
	protected String bioCredit;
	protected boolean thumbnail;   // at resources/teamid/playerid_t.jpg
	protected boolean picture;	   // at resources/teamid/playerid.jpg
	
	// some sort of RSS reader
	protected String newsUrl; 
	protected String photosUrl;
	protected String videoUrl;
	
	//ratings
	protected Long origRating;
	protected Long positionRating;
	protected Long classRating;  //forward/back
	protected Long overallRating;
	protected Long lastOverallRating;
	protected Long poolStageRating;
	
	// hoarked from IRB 
	protected Integer M;
	protected Integer TRS;
	protected Integer CON;
	protected Integer PEN;
	protected Integer DG;
	protected Integer PTS;
	protected Integer YC;
	protected Integer RC;
	protected Integer irbPlayerID;
	protected Integer irbTeamID;
	
	// dynamic IRB data
	protected int averageLineBreaksPerMatch;
	protected int totalMinutesPlayed;
	protected int matchesPlayed;
	protected int averageTackles;
	protected int averageOffloads;
	protected float pgSuccessRate;
	protected float conversionSuccessRate;
	
	// rating support data
	protected Long lastMatchRating;
	protected Integer lastMatchOpponentRanking;	
	  
	public Player() {}
	
	public Player(Long id, String givenName, String surName, Position.position pos, Position.position[] altpos) {
		this.id = id;
	    this.givenName = givenName;
	    this.surName = surName;
		this.position = pos;
		this.altposition = altpos;
	}
	
	public Player(Long id, Long teamID, String teamName, String teamAbbr, String pool,
			String givenName, String surName, String displayName,
			net.rugby.foundation.model.shared.Position.position position,
			net.rugby.foundation.model.shared.Position.position[] altposition,
			Date dateOfBirth, Integer height, Integer weight,
			Integer numberCaps, String club, String bioSnippet, String bioURL,
			String bioCredit, boolean thumbnail, boolean picture,
			String newsUrl, String photosUrl, String videoUrl, Long origRating,
			Long positionRating, Long classRating, Long overallRating,
			Long lastOverallRating) {
		super();
		this.id = id;
		this.teamID = teamID;
		this.teamName = teamName;
		this.teamAbbr = teamAbbr;
		this.pool = pool;
		this.givenName = givenName;
		this.surName = surName;
		this.displayName = displayName;
		this.position = position;
		this.altposition = altposition;
		this.dateOfBirth = dateOfBirth;
		this.height = height;
		this.weight = weight;
		this.numberCaps = numberCaps;
		this.club = club;
		this.bioSnippet = bioSnippet;
		this.bioURL = bioURL;
		this.bioCredit = bioCredit;
		this.thumbnail = thumbnail;
		this.picture = picture;
		this.newsUrl = newsUrl;
		this.photosUrl = photosUrl;
		this.videoUrl = videoUrl;
		this.origRating = origRating;
		this.positionRating = positionRating;
		this.classRating = classRating;
		this.overallRating = overallRating;
		this.lastOverallRating = lastOverallRating;
	}

	public PlayerRowData getPlayerRowData(TeamGroup team) {
		
		movement move = movement.UNCHANGED;
		if (overallRating != null && lastOverallRating != null) {
			
			if (overallRating > lastOverallRating) 
				move = movement.UP;
			else if (overallRating < lastOverallRating)
				move = movement.DOWN;
		}

		return new PlayerRowData(id, displayName, team.getAbbr(), team.getId(),
				pool, origRating, overallRating, positionRating,
				classRating, lastOverallRating, poolStageRating, move, position);
	}
	
	public PlayerPopupData getPlayerPopupData(TeamGroup team) {
		
		PlayerPopupData ppd = new PlayerPopupData(getId(),thumbnail,team.getDisplayName(), new ArrayList<MatchPopupData>(),getPlayerRowData(team));
				
		return ppd;
		
	}


	//@ClientSerializableField("id")
	 public Long getId() { return id; }
	
	//@ClientSerializableField("id")
	public void setId(Long id) { this.id = id; }
	
	public Long getTeamID() {
		return teamID;
	}
	
	public void setTeamID(Long teamID) {
		this.teamID = teamID;
	}
	
	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getTeamAbbr() {
		return teamAbbr;
	}

	public void setTeamAbbr(String teamAbbr) {
		this.teamAbbr = teamAbbr;
	}

	public String getPool() {
		return pool;
	}
	
	public void setPool(String pool) {
		this.pool = pool;
	}

	public Position.position getPosition() {
		return position;
	}
	
	public void setPosition(Position.position position) {
		this.position = position;
	}	
	
	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getSurName() {
		return surName;
	}

	public void setSurName(String surName) {
		this.surName = surName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Position.position[] getAltposition() {
		return altposition;
	}

	public void setAltposition(Position.position[] altposition) {
		this.altposition = altposition;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getHeightInFeet() {
		double totalinches = height/2.54;  
		double feet = java.lang.Math.floor(totalinches/12.0);  
		double inches = totalinches-feet*12.0;  
		return feet + "'" + inches + "\"";
	}

	public String getWeightInPounds() {
		return weight*2.2 + "lb";
	}
	
	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	
	public Integer getNumberCaps() {
		return numberCaps;
	}

	public void setNumberCaps(Integer numberCaps) {
		this.numberCaps = numberCaps;
	}

	public String getClub() {
		return club;
	}

	public void setClub(String club) {
		this.club = club;
	}

	public String getBioSnippet() {
		return bioSnippet;
	}

	public void setBioSnippet(String bioSnippet) {
		this.bioSnippet = bioSnippet;
	}

	public String getBioURL() {
		return bioURL;
	}

	public void setBioURL(String bioURL) {
		this.bioURL = bioURL;
	}

	public String getBioCredit() {
		return bioCredit;
	}

	public void setBioCredit(String bioCredit) {
		this.bioCredit = bioCredit;
	}

	public boolean isThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(boolean thumbnail) {
		this.thumbnail = thumbnail;
	}

	public boolean isPicture() {
		return picture;
	}

	public void setPicture(boolean picture) {
		this.picture = picture;
	}

	public String getNewsUrl() {
		return newsUrl;
	}

	public void setNewsUrl(String newsUrl) {
		this.newsUrl = newsUrl;
	}

	public String getPhotosUrl() {
		return photosUrl;
	}

	public void setPhotosUrl(String photosUrl) {
		this.photosUrl = photosUrl;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public Long getOrigRating() {
		return origRating;
	}

	public void setOrigRating(Long origRating) {
		this.origRating = origRating;
	}

	public Long getPositionRating() {
		return positionRating;
	}

	public void setPositionRating(Long positionRating) {
		this.positionRating = positionRating;
	}

	public Long getClassRating() {
		return classRating;
	}

	public void setClassRating(Long classRating) {
		this.classRating = classRating;
	}

	public Long getOverallRating() {
		return overallRating;
	}

	public void setOverallRating(Long overallRating) {
		this.overallRating = overallRating;
	}

	public Long getLastOverallRating() {
		return lastOverallRating;
	}

	public void setLastOverallRating(Long lastOverallRating) {
		this.lastOverallRating = lastOverallRating;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Long getPoolStageRating() {
		return poolStageRating;
	}

	public void setPoolStageRating(Long poolStageRating) {
		this.poolStageRating = poolStageRating;
	}


}
