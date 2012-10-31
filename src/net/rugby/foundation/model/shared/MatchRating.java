package net.rugby.foundation.model.shared;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class MatchRating {
	@Id
	private Long matchRatingID;
	private Long playerID;
	private Long matchID;
	private Long playerRating;
	private Integer irbMatchID;
	private Integer irbPlayerID;
	private Integer schema;
	
	public MatchRating() {
		
	}
	
	public MatchRating(Long playerID, Long matchID, Long rating,
			Integer irbMatchID, Integer irbPlayerID, Integer schema) {
		super();
		this.playerID = playerID;
		this.matchID = matchID;
		this.playerRating = rating;
		this.irbMatchID = irbMatchID;
		this.irbPlayerID = irbPlayerID;
		this.schema = schema;
	}
	
	public Long getMatchRatingID() {
		return matchRatingID;
	}

	public void setMatchRatingID(Long matchRatingID) {
		this.matchRatingID = matchRatingID;
	}

	public Long getPlayerID() {
		return playerID;
	}
	public void setPlayerID(Long playerID) {
		this.playerID = playerID;
	}
	public Long getMatchID() {
		return matchID;
	}
	public void setMatchID(Long matchID) {
		this.matchID = matchID;
	}
	public Long getPlayerRating() {
		return playerRating;
	}
	public void setPlayerRating(Long rating) {
		this.playerRating = rating;
	}
	public Integer getIrbMatchID() {
		return irbMatchID;
	}
	public void setIrbMatchID(Integer irbMatchID) {
		this.irbMatchID = irbMatchID;
	}
	public Integer getIrbPlayerID() {
		return irbPlayerID;
	}
	public void setIrbPlayerID(Integer irbPlayerID) {
		this.irbPlayerID = irbPlayerID;
	}

	public Integer getSchema() {
		return schema;
	}

	public void setSchema(Integer schema) {
		this.schema = schema;
	}


	
}
