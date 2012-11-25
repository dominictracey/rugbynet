package net.rugby.foundation.model.shared;

public interface IMatchRating {

	public abstract Long getMatchRatingID();

	public abstract void setMatchRatingID(Long matchRatingID);

	public abstract Long getPlayerID();

	public abstract void setPlayerID(Long playerID);

	public abstract Long getMatchID();

	public abstract void setMatchID(Long matchID);

	public abstract Long getPlayerRating();

	public abstract void setPlayerRating(Long rating);

	public abstract Integer getIrbMatchID();

	public abstract void setIrbMatchID(Integer irbMatchID);

	public abstract Integer getIrbPlayerID();

	public abstract void setIrbPlayerID(Integer irbPlayerID);

	public abstract Integer getSchema();

	public abstract void setSchema(Integer schema);

}