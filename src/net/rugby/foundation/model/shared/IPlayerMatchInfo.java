package net.rugby.foundation.model.shared;


public interface IPlayerMatchInfo {

	public abstract IPlayerMatchStats getPlayerMatchStats();

	public abstract void setPlayerMatchStats(IPlayerMatchStats playerMatchStats);

	public abstract IPlayerMatchRating getMatchRating();

	public abstract void setMatchRating(IPlayerMatchRating matchRating);

}