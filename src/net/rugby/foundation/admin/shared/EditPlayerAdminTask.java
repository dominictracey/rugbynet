package net.rugby.foundation.admin.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import net.rugby.foundation.model.shared.IPlayer;

import com.googlecode.objectify.annotation.Subclass;

@Subclass
public class EditPlayerAdminTask extends AdminTask implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3702959749976062416L;
	
	Long playerId;
	@Transient
	IPlayer player;
	
	public EditPlayerAdminTask() {
		
	}	
	
	public EditPlayerAdminTask(Long id, Action action, Long adminId,
			Date created, Date completed, Status status, Priority priority,
			String summary, String details, List<String> log, String promise,
			String pipelineRoot, String pipelineJob, Long playerId, IPlayer player) {
		super(id, action, adminId, created, completed, status, priority, summary,
				details, log, promise, pipelineRoot, pipelineJob);
		this.playerId = playerId;
		this.player = player;
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
	
}
