package net.rugby.foundation.admin.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Subclass;

import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;

@Subclass
public class EditTeamMatchStatsAdminTask extends AdminTask implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 17082041638693505L;

	Long teamId;
	@Transient
	ITeamGroup team;

	Long matchId;
	@Transient
	IMatchGroup match;

	Long teamMatchStatsId;
	@Transient
	ITeamMatchStats tms;
	
	public EditTeamMatchStatsAdminTask()  {

	}


	public EditTeamMatchStatsAdminTask(Long id, Action action, Long adminId,
			Date created, Date completed, Status status, Priority priority,
			String summary, String details, List<String> log, String promise,
			String pipelineRoot, String pipelineJob, ITeamGroup team, IMatchGroup match, ITeamMatchStats tms) {
		super(id, action, adminId, created, completed, status, priority, summary,
				details, log, promise, pipelineRoot, pipelineJob);
		if (team != null) {
			this.teamId = team.getId();
			this.team = team;
		}
		if (match != null) {
			this.matchId = match.getId();
			this.match = match;
		}
		if (tms != null) {
			this.tms = tms;
			this.teamMatchStatsId = tms.getId();
		}
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


	public Long getTeamId() {
		return teamId;
	}


	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}


	public ITeamGroup getTeam() {
		return team;
	}


	public void setTeam(ITeamGroup team) {
		this.team = team;
	}


	public Long getTeamMatchStatsId() {
		return teamMatchStatsId;
	}


	public void setTeamMatchStatsId(Long teamMatchStatsId) {
		this.teamMatchStatsId = teamMatchStatsId;
	}


	public ITeamMatchStats getTms() {
		return tms;
	}


	public void setTms(ITeamMatchStats tms) {
		this.tms = tms;
	}


}
