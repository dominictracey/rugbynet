package net.rugby.foundation.topten.model.shared;

import net.rugby.foundation.model.shared.IHasId;

public interface INote extends IHasId {

	public enum LinkType { Match, Round, Position, Comp, Team, Country }
	
	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract Long getPlayer1Id();

	public abstract void setPlayer1Id(Long player1Id);

	public abstract Long getPlayer2Id();

	public abstract void setPlayer2Id(Long player2Id);

	public abstract Long getPlayer3Id();

	public abstract void setPlayer3Id(Long player3Id);

	public abstract Long getTeamId();

	public abstract void setTeamId(Long teamId);

	/**
	 * 
	 * @return universal round ordinal
	 */
	public abstract int getRound();

	/**
	 * 
	 * @param round - universal round ordinal
	 */
	public abstract void setRound(int round);

	public abstract Long getMatchId();

	public abstract void setMatchId(Long matchId);

	public abstract Long getCompId();

	public abstract void setCompId(Long compId);

	public abstract int getMovement();

	public abstract void setMovement(int movement);

	public abstract int getDuration();

	public abstract void setDuration(int duration);

	public abstract Long getPlaceId();

	public abstract void setPlaceId(Long placeId);

	public abstract Long getContextListId();

	public abstract void setContextListId(Long contextListId);

	/**
	 * Note that this can refer to a TopTenItem (a specific player on a TTL) so it may be different than ttlf.get(getContextListId()).getGuid()
	 * @return the guid that can be prepended with a http://rugby.net/s/
	 */
	public abstract String getLink();

	public abstract void setLink(String link);

	public abstract LinkType getType();

	public abstract void setType(LinkType type);

	public abstract String getText();

	public abstract void setText(String text);

	public abstract int getSignificance();

	public abstract void setSignificance(int significance);

	void setDetails(String details);

	String getDetails();

	String getTemplateSelector();

	void setTemplateSelector(String templateSelector);

}