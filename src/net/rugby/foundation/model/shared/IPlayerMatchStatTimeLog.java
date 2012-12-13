package net.rugby.foundation.model.shared;

import net.rugby.foundation.model.shared.Position.position;

public interface IPlayerMatchStatTimeLog {

	public abstract void start(int timeOn, position pos, Long playerId,
			Long matchId);

	public abstract void stop(int timeOff);

	public abstract int getPlayingTime();

	public abstract Long getId();

	public abstract int getTimeOn();

	public abstract int getTimeOff();

	public abstract position getPos();

	public abstract Long getPlayerId();

	public abstract Long getMatchId();

}