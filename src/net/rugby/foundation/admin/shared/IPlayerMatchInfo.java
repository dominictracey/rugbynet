package net.rugby.foundation.admin.shared;

import net.rugby.foundation.model.shared.IMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;

public interface IPlayerMatchInfo {

	public abstract IPlayerMatchStats getPlayerMatchStats();

	public abstract void setPlayerMatchStats(IPlayerMatchStats playerMatchStats);

//	public abstract IMatchRating getMatchRating();
//
//	public abstract void setMatchRating(IMatchRating matchRating);

}