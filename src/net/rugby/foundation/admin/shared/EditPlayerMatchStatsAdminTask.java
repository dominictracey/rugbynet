package net.rugby.foundation.admin.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Subclass;

import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;

@Subclass
public class EditPlayerMatchStatsAdminTask extends AdminTask implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 17082041638693505L;

	Long playerId;
	@Transient
	IPlayer player;

	Long matchId;
	@Transient
	IMatchGroup match;

	Long playerMatchStatsId;
	@Transient
	IPlayerMatchStats pms;
	
	public EditPlayerMatchStatsAdminTask()  {

	}


	public EditPlayerMatchStatsAdminTask(Long id, Action action, Long adminId,
			Date created, Date completed, Status status, Priority priority,
			String summary, String details, List<String> log, String promise,
			String pipelineRoot, String pipelineJob, IPlayer player, IMatchGroup match, IPlayerMatchStats pms) {
		super(id, action, adminId, created, completed, status, priority, summary,
				details, log, promise, pipelineRoot, pipelineJob);
		if (player != null) {
			this.playerId = player.getId();
			this.player = player;
		}
		if (match != null) {
			this.matchId = match.getId();
			this.match = match;
		}
		if (pms != null) {
			this.pms = pms;
			this.playerMatchStatsId = pms.getId();
		}
	}


	public Long getPlayerId() {
		return playerId;
	}
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}
	public IPlayer getPlayer() {
		return player;
	}
	public void setPlayer(IPlayer player) {
		this.player = player;
	}
	public Long getMatchId() {
		return matchId;
	}
	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}
	public IMatchGroup getMatch() {
		return match;
	}
	public void setMatch(IMatchGroup match) {
		this.match = match;
	}


	public Long getPlayerMatchStatsId() {
		return playerMatchStatsId;
	}


	public void setPlayerMatchStatsId(Long playerMatchStatsId) {
		this.playerMatchStatsId = playerMatchStatsId;
	}


	public IPlayerMatchStats getPms() {
		return pms;
	}


	public void setPms(IPlayerMatchStats pms) {
		this.pms = pms;
	}

}
