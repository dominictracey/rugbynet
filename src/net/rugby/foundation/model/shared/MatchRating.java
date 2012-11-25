package net.rugby.foundation.model.shared;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class MatchRating implements IMatchRating {
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
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchRating#getMatchRatingID()
	 */
	@Override
	public Long getMatchRatingID() {
		return matchRatingID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchRating#setMatchRatingID(java.lang.Long)
	 */
	@Override
	public void setMatchRatingID(Long matchRatingID) {
		this.matchRatingID = matchRatingID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchRating#getPlayerID()
	 */
	@Override
	public Long getPlayerID() {
		return playerID;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchRating#setPlayerID(java.lang.Long)
	 */
	@Override
	public void setPlayerID(Long playerID) {
		this.playerID = playerID;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchRating#getMatchID()
	 */
	@Override
	public Long getMatchID() {
		return matchID;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchRating#setMatchID(java.lang.Long)
	 */
	@Override
	public void setMatchID(Long matchID) {
		this.matchID = matchID;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchRating#getPlayerRating()
	 */
	@Override
	public Long getPlayerRating() {
		return playerRating;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchRating#setPlayerRating(java.lang.Long)
	 */
	@Override
	public void setPlayerRating(Long rating) {
		this.playerRating = rating;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchRating#getIrbMatchID()
	 */
	@Override
	public Integer getIrbMatchID() {
		return irbMatchID;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchRating#setIrbMatchID(java.lang.Integer)
	 */
	@Override
	public void setIrbMatchID(Integer irbMatchID) {
		this.irbMatchID = irbMatchID;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchRating#getIrbPlayerID()
	 */
	@Override
	public Integer getIrbPlayerID() {
		return irbPlayerID;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchRating#setIrbPlayerID(java.lang.Integer)
	 */
	@Override
	public void setIrbPlayerID(Integer irbPlayerID) {
		this.irbPlayerID = irbPlayerID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchRating#getSchema()
	 */
	@Override
	public Integer getSchema() {
		return schema;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchRating#setSchema(java.lang.Integer)
	 */
	@Override
	public void setSchema(Integer schema) {
		this.schema = schema;
	}


	
}
