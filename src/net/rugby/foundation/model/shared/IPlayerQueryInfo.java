package net.rugby.foundation.model.shared;

import java.util.List;


public interface IPlayerQueryInfo {

	public abstract List<IPlayerMatchStats> getPlayerMatchStats();

	public abstract void setPlayerMatchStats(List<IPlayerMatchStats> playerMatchStats);

	public abstract IPlayerMatchRating getMatchRating();

	public abstract void setMatchRating(IPlayerMatchRating matchRating);

}