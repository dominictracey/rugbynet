package net.rugby.foundation.model.shared;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;

import net.rugby.foundation.model.shared.Position.position;

@Entity
public class LineupSlot implements ILineupSlot, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2904360038031973438L;
	@Id
	private Long id;
	private Long playerId;
	private Long foreignPlayerId;
	private int slot;
	private Long matchId;
	private Long foreignMatchId;
	private boolean home;
	private String foreignPlayerName;
	
	@Transient
	private IPlayer player;
	@Transient
	private IMatchGroup match;
	@Transient
	private position pos;
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ILineupSlot#getPlayerId()
	 */
	@Override
	public Long getPlayerId() {
		return playerId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ILineupSlot#setPlayerId(java.lang.Long)
	 */
	@Override
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ILineupSlot#getForeignPlayerId()
	 */
	@Override
	public Long getForeignPlayerId() {
		return foreignPlayerId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ILineupSlot#setForeignPlayerId(java.lang.Long)
	 */
	@Override
	public void setForeignPlayerId(Long foreignPlayerId) {
		this.foreignPlayerId = foreignPlayerId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ILineupSlot#getSlot()
	 */
	@Override
	public int getSlot() {
		return slot;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ILineupSlot#setSlot(int)
	 */
	@Override
	public void setSlot(int slot) {
		this.slot = slot;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ILineupSlot#getMatchId()
	 */
	@Override
	public Long getMatchId() {
		return matchId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ILineupSlot#setMatchId(java.lang.Long)
	 */
	@Override
	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ILineupSlot#getForeignMatchId()
	 */
	@Override
	public Long getForeignMatchId() {
		return foreignMatchId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ILineupSlot#setForeignMatchId(java.lang.Long)
	 */
	@Override
	public void setForeignMatchId(Long foreignMatchId) {
		this.foreignMatchId = foreignMatchId;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public Long getId() {
		return id;
	}
	@Override
	public boolean getHome() {
		return home;
	}
	@Override
	public void setHome(boolean home) {
		this.home = home;
	}
	@Override
	public IPlayer getPlayer() {
		return player;
	}
	@Override
	public void setPlayer(IPlayer player) {
		this.player = player;
	}
	@Override
	public IMatchGroup getMatch() {
		return match;
	}
	@Override
	public void setMatch(IMatchGroup match) {
		this.match = match;
	}
	@Override
	public position getPos() {
		return pos;
	}
	@Override
	public void setPos(position pos) {
		this.pos = pos;
	}
	@Override
	public String getForeignPlayerName() {
		return foreignPlayerName;
	}
	@Override
	public void setForeignPlayerName(String foreignPlayerName) {
		this.foreignPlayerName = foreignPlayerName;
	}
}
