package net.rugby.foundation.model.shared;

import java.io.Serializable;
import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;


@SuppressWarnings("serial")
@Entity
public class RawScore implements Serializable, IRawScore  {
  
	
	@Id
	private Long id;
	private Long playerMatchStatsId;
	private Long schemaId;
	private Float rawScore;
	private Long playerId;
	@Unindexed
	private String details;
	
	public RawScore() {}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRawScore#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRawScore#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRawScore#getPlayerMatchStatsId()
	 */
	@Override
	public Long getPlayerMatchStatsId() {
		return playerMatchStatsId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRawScore#setPlayerMatchStatsId(java.lang.Long)
	 */
	@Override
	public void setPlayerMatchStatsId(Long playerMatchStatsId) {
		this.playerMatchStatsId = playerMatchStatsId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRawScore#getSchemaId()
	 */
	@Override
	public Long getSchemaId() {
		return schemaId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRawScore#setSchemaId(java.lang.Long)
	 */
	@Override
	public void setSchemaId(Long schemaId) {
		this.schemaId = schemaId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRawScore#getRawScore()
	 */
	@Override
	public Float getRawScore() {
		return rawScore;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRawScore#setRawScore(java.lang.Float)
	 */
	@Override
	public void setRawScore(Float rawScore) {
		this.rawScore = rawScore;
	}
	@Override
	public Long getPlayerId() {
		return playerId;
	}
	@Override
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}
	@Override
	public String getDetails() {
		return details;
	}
	@Override
	public void setDetails(String details) {
		this.details = details;
	}
	

}
