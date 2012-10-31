package net.rugby.foundation.model.shared;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

@SuppressWarnings("serial")
@Entity
public class PlayerRowData implements Serializable {
  @Id
  private Long id;
  private String displayName;
  private String teamAbbr;
  private Long teamID;
  private String pool;
  private Long origRating;
  private Long overallRating;
  private Long positionRating;
  private Long classRating;
  private Long lastRating;
  private Long poolRating;
  private Player.movement movement;
  
  private Position.position position;
  
  public PlayerRowData()  {
    new PlayerRowData(null, "", "", null, "", 0L, 0L, 0L, 0L, 0L, 0L, Player.movement.UNCHANGED, Position.position.NONE);
  }
  
  public PlayerRowData(Long id, String displayName, String teamAbbr, Long teamID,
		String pool, Long origRating, Long overallRating, Long positionRating,
		Long classRating, Long lastRating, Long poolRating, Player.movement movement, Position.position position) {
	super();
	this.id = id;
	this.displayName = displayName;
	this.teamAbbr = teamAbbr;
	this.teamID = teamID;
	this.pool = pool;
	this.origRating = origRating;
	this.overallRating = overallRating;
	this.positionRating = positionRating;
	this.classRating = classRating;
	this.lastRating = lastRating;
	this.poolRating = poolRating;
	this.movement = movement;
	this.position = position;
}

public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getDisplayName() { return displayName; }
  public void setDisplayName(String displayName) { this.displayName = displayName; } 


	public Position.position getPosition() {
		return position;
	}
	
	public void setPosition(Position.position position) {
		this.position = position;
	}

	public String getTeamAbbr() {
		return teamAbbr;
	}

	public void setTeamAbbr(String teamAbbr) {
		this.teamAbbr = teamAbbr;
	}

	public Long getTeamID() {
		return teamID;
	}

	public void setTeam(Long  teamID) {
		this.teamID = teamID;
	}

	public String getPool() {
		return pool;
	}

	public void setPool(String pool) {
		this.pool = pool;
	}

	public Long getOrigRating() {
		return origRating;
	}

	public void setOrigRating(Long origRating) {
		this.origRating = origRating;
	}

	public Long getOverallRating() {
		return overallRating;
	}

	public void setOverallRating(Long overallRating) {
		this.overallRating = overallRating;
	}

	public Long getPositionRating() {
		return positionRating;
	}

	public void setPositionRating(Long positionRating) {
		this.positionRating = positionRating;
	}

	public Long getClassRating() {
		return classRating;
	}

	public void setClassRating(Long classRating) {
		this.classRating = classRating;
	}

	public Long getLastRating() {
		return lastRating;
	}

	public void setLastRating(Long lastRating) {
		this.lastRating = lastRating;
	}

	public Player.movement getMovement() {
		return movement;
	}

	public void setMovement(Player.movement movement) {
		this.movement = movement;
	}

	public Long getPoolRating() {
		return poolRating;
	}

	public void setPoolRating(Long poolRating) {
		this.poolRating = poolRating;
	}

  
}
