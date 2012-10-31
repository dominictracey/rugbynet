package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class TeamMembership implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	private Long id;
	private Date start;
	private Date end;
	private Long playerID;
	private Long teamID;
	private Boolean active;
	
	public TeamMembership()
	{
		
	}

	public TeamMembership(Long id, Date start, Date end, Long playerID,
			Long teamID, Boolean active) {
		super();
		this.id = id;
		this.start = start;
		this.end = end;
		this.playerID = playerID;
		this.teamID = teamID;
		this.active = active;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Long getPlayerID() {
		return playerID;
	}

	public void setPlayerID(Long playerID) {
		this.playerID = playerID;
	}

	public Long getTeamID() {
		return teamID;
	}

	public void setTeamID(Long teamID) {
		this.teamID = teamID;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
}
