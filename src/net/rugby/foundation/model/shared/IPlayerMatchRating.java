package net.rugby.foundation.model.shared;

public interface IPlayerMatchRating extends IPlayerRating {

	public abstract IPlayerMatchStats getPlayerMatchStats();

	public abstract void setPlayerMatchStats(IPlayerMatchStats playerMatchStats);

	Long getPlayerMatchStatsId();

	void setPlayerMatchStatsId(Long playerMatchStatsId);

	String getDetails();

}