package net.rugby.foundation.admin.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import net.rugby.foundation.model.shared.IPlayer;

import com.googlecode.objectify.annotation.Subclass;

@Subclass
public class EditPlayersTwitterAdminTask extends EditPlayerAdminTask implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3702959749976062416L;
	
	Long topTenListId;
	Long topTenItemId;
	
	public EditPlayersTwitterAdminTask() {
		
	}	
	
	public Long getTopTenListId() {
		return topTenListId;
	}

	public void setTopTenListId(Long topTenListId) {
		this.topTenListId = topTenListId;
	}

	public Long getTopTenItemId() {
		return topTenItemId;
	}

	public void setTopTenItemId(Long topTenItemId) {
		this.topTenItemId = topTenItemId;
	}

	public EditPlayersTwitterAdminTask(Long id, Action action, Long adminId,
			Date created, Date completed, Status status, Priority priority,
			String summary, String details, List<String> log, String promise,
			String pipelineRoot, String pipelineJob, Long playerId, IPlayer player, Long topTenListId, Long topTenItemId) {
		super(id, action, adminId, created, completed, status, priority, summary,
				details, log, promise, pipelineRoot, pipelineJob, playerId, player);
		this.topTenItemId = topTenItemId;
		this.topTenListId = topTenListId;
	}

	
}
