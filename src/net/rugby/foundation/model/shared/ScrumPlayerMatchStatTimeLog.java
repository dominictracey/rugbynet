package net.rugby.foundation.model.shared;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

import net.rugby.foundation.model.shared.Position.position;

@Entity
public class ScrumPlayerMatchStatTimeLog implements Serializable, IPlayerMatchStatTimeLog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9189569427277720575L;
	
	@Id 
	private Long id;
	private int timeOn = 0;
	private int timeOff = 0;
	private position pos;
	private Long playerId;
	private Long matchId;
	
	public ScrumPlayerMatchStatTimeLog() {
		
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStatTimeLog#start(int, net.rugby.foundation.model.shared.Position.position, java.lang.Long, java.lang.Long)
	 */
	@Override
	public void start(int timeOn, position pos, Long playerId, Long matchId) {
		this.timeOn = timeOn;
		this.pos = pos;
		this.playerId = playerId;
		this.matchId = matchId;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStatTimeLog#stop(int)
	 */
	@Override
	public void stop(int timeOff) {
		this.timeOff = timeOff;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStatTimeLog#getPlayingTime()
	 */
	@Override
	public int getPlayingTime() {
		return timeOff-timeOn;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStatTimeLog#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStatTimeLog#getTimeOn()
	 */
	@Override
	public int getTimeOn() {
		return timeOn;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStatTimeLog#getTimeOff()
	 */
	@Override
	public int getTimeOff() {
		return timeOff;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStatTimeLog#getPos()
	 */
	@Override
	public position getPos() {
		return pos;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStatTimeLog#getPlayerId()
	 */
	@Override
	public Long getPlayerId() {
		return playerId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchStatTimeLog#getMatchId()
	 */
	@Override
	public Long getMatchId() {
		return matchId;
	}
}
