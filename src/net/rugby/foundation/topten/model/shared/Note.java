package net.rugby.foundation.topten.model.shared;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

import net.rugby.foundation.model.shared.IHasId;

@Entity
public class Note implements Serializable, IHasId, INote {

//	Player1
//	Player2
//	Player3
//	Team
//	Round
//	Match
//	Comp
//	Movement (expressed as a positive or negative integer)
//	Duration (expressed in number of weeks)
//  Details
//	Place
//	ContextList - The list the note is attached to
//	Link - GUID
//	LinkType -  [Match, Round, Position, Comp, Team]
//	Encoding - i18n
//	Comment
//	Significance - in the grand scheme of things… [0-100]

	public static final String PLAYER1 = "[P1]";
	public static final String PLAYER2 = "[P2]";
	public static final String PLAYER3 = "[P3]";
	public static final String TEAM = "[TEAM]";
	public static final String ROUND = "[ROUND]";
	public static final String MATCH = "[MATCH]";
	public static final String COMP = "[COMP]";
	public static final String DETAILS = "[DETAILS]";
	public static final String MOVEMENT = "[MOVEMENT]";
	public static final String DURATION = "[DURATION]";
	public static final String PLACE = "[PLACE]";
	public static final String CONTEXT = "[CONTEXT]";
	public static final String LINK = "[LINK]";
	public static final String LIST = "[LIST]";

	/**
	 * 
	 */
	private static final long serialVersionUID = -393804380440831046L;
	

	@Id
	Long id;

	Long player1Id;
	@Unindexed
	Long player2Id;
	@Unindexed
	Long player3Id;

	Long teamId;
	int round;  // universal round ordinal
	Long matchId;
	Long compId;
	@Unindexed
	String details;
	@Unindexed
	int movement; // (expressed as a positive or negative integer)
	@Unindexed
	int duration; // (expressed in number of weeks)
	Long placeId;
	Long contextListId; // - The list the note is attached to
	String Link; // GUID of contextList
	LinkType type; // -  [Match, Round, Position, Comp, Team]
	@Unindexed
	String templateSelector; //- i18n
	@Unindexed
	String text;
	int significance;
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#getPlayer1Id()
	 */
	@Override
	public Long getPlayer1Id() {
		return player1Id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#setPlayer1Id(java.lang.Long)
	 */
	@Override
	public void setPlayer1Id(Long player1Id) {
		this.player1Id = player1Id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#getPlayer2Id()
	 */
	@Override
	public Long getPlayer2Id() {
		return player2Id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#setPlayer2Id(java.lang.Long)
	 */
	@Override
	public void setPlayer2Id(Long player2Id) {
		this.player2Id = player2Id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#getPlayer3Id()
	 */
	@Override
	public Long getPlayer3Id() {
		return player3Id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#setPlayer3Id(java.lang.Long)
	 */
	@Override
	public void setPlayer3Id(Long player3Id) {
		this.player3Id = player3Id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#getTeamId()
	 */
	@Override
	public Long getTeamId() {
		return teamId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#setTeamId(java.lang.Long)
	 */
	@Override
	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#getRoundId()
	 */
	@Override
	public int getRound() {
		return round;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#setRoundId(java.lang.Long)
	 */
	@Override
	public void setRound(int round) {
		this.round = round;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#getMatchId()
	 */
	@Override
	public Long getMatchId() {
		return matchId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#setMatchId(java.lang.Long)
	 */
	@Override
	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#getCompId()
	 */
	@Override
	public Long getCompId() {
		return compId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#setCompId(java.lang.Long)
	 */
	@Override
	public void setCompId(Long compId) {
		this.compId = compId;
	}
	@Override
	public String getDetails() {
		return details;
	}
	@Override
	public void setDetails(String details) {
		this.details = details;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#getMovement()
	 */
	@Override
	public int getMovement() {
		return movement;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#setMovement(int)
	 */
	@Override
	public void setMovement(int movement) {
		this.movement = movement;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#getDuration()
	 */
	@Override
	public int getDuration() {
		return duration;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#setDuration(int)
	 */
	@Override
	public void setDuration(int duration) {
		this.duration = duration;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#getPlaceId()
	 */
	@Override
	public Long getPlaceId() {
		return placeId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#setPlaceId(java.lang.Long)
	 */
	@Override
	public void setPlaceId(Long placeId) {
		this.placeId = placeId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#getContextListId()
	 */
	@Override
	public Long getContextListId() {
		return contextListId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#setContextListId(java.lang.Long)
	 */
	@Override
	public void setContextListId(Long contextListId) {
		this.contextListId = contextListId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#getLink()
	 */
	@Override
	public String getLink() {
		return Link;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#setLink(java.lang.String)
	 */
	@Override
	public void setLink(String link) {
		Link = link;
	}
	@Override
	public LinkType getType() {
		return type;
	}
	@Override
	public void setType(LinkType type) {
		this.type = type;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#getEncoding()
	 */
	@Override
	public String getTemplateSelector() {
		return templateSelector;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#setEncoding(java.lang.String)
	 */
	@Override
	public void setTemplateSelector(String templateSelector) {
		this.templateSelector = templateSelector;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#getText()
	 */
	@Override
	public String getText() {
		return text;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#setText(java.lang.String)
	 */
	@Override
	public void setText(String text) {
		this.text = text;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#getSignificance()
	 */
	@Override
	public int getSignificance() {
		return significance;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INote#setSignificance(int)
	 */
	@Override
	public void setSignificance(int significance) {
		this.significance = significance;
	}
	

}
