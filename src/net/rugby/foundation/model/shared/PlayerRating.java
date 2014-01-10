package net.rugby.foundation.model.shared;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;

import net.rugby.foundation.admin.shared.IRatingEngineSchema;

@Entity
public class PlayerRating implements IPlayerRating, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7755010593959762373L;

	public PlayerRating() {

	}

	public PlayerRating(Integer rating, IPlayer player, IGroup group,
			IRatingEngineSchema schema, IRatingQuery query, String details) {
		super();
		this.rating = rating;
		this.player = player;
		if (player != null) {
			this.playerId = player.getId();
		} else {
			this.playerId = null;
		}
		if (group != null) {
			this.group = group;
			this.groupId = group.getId();
		}
		this.schema = schema;
		this.schemaId = schema.getId();
		this.queryId = query.getId();
		this.details = details;
	}

	@Id
	protected Long id;
	protected Integer rating;
	protected Long groupId;
	protected Date generated;
	@Transient
	protected transient IGroup group;
	protected Long schemaId;
	@Transient
	protected transient IRatingEngineSchema schema;
	protected Long playerId;
	@Transient
	protected transient IPlayer player;

	protected List<Long> playerMatchStatIds;
	@Transient
	private List<IPlayerMatchStats> playerMatchStats;
		
	protected Long queryId;
	protected String details;
	protected Float rawScore;
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerRating#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerRating#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerRating#getRating()
	 */
	@Override
	public Integer getRating() {
		return rating;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerRating#setRating(java.lang.Integer)
	 */
	@Override
	public void setRating(Integer rating) {
		this.rating = rating;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerRating#getGroupId()
	 */
	@Override
	public Long getGroupId() {
		return groupId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerRating#setGroupId(java.lang.Long)
	 */
	@Override
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerRating#getGroup()
	 */
	@Override
	public IGroup getGroup() {
		return group;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerRating#setGroup(net.rugby.foundation.model.shared.Group)
	 */
	@Override
	public void setGroup(IGroup group) {
		this.group = group;
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
	public IPlayer getPlayer() {
		return player;
	}
	@Override
	public void setPlayer(IPlayer player) {
		this.player = player;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerRating#getSchemaId()
	 */
	@Override
	public Long getSchemaId() {
		return schemaId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerRating#setSchemaId(java.lang.Long)
	 */
	@Override
	public void setSchemaId(Long schemaId) {
		this.schemaId = schemaId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerRating#getSchema()
	 */
	@Override
	public IRatingEngineSchema getSchema() {
		return schema;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerRating#setSchema(net.rugby.foundation.admin.server.model.IMatchRatingEngineSchema)
	 */
	@Override
	public void setSchema(IRatingEngineSchema schema) {
		this.schema = schema;
	}

	@Override
	public Date getGenerated() {
		return generated;
	}

	@Override
	public void setGenerated(Date generated) {
		this.generated = generated;
	}

	@Override
	public String toString() {
		return "[PlayerRating:: player: " + playerId + " rating: " + rating + "]";
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerMatchRating#setPlayerMatchStats(java.util.List)
	 */
	@Override
	public void addMatchStats(IPlayerMatchStats pms) {
		if (this.playerMatchStats == null) {
			this.playerMatchStats = new ArrayList<IPlayerMatchStats>();
		}
		this.playerMatchStats.add(pms);
	}

	@Override
	public int compareTo(IPlayerMatchRating o) {
		if (rating == null) { 
			return 1;
		}

		if (o.getRating() == null) {
			return -1;
		}

		if (rating.equals(o.getRating())) {
			return 0;
		} else if (rating < o.getRating()) {
			return 1;
		} else {
			return -1;
		}
	}
	@Override
	public List<Long> getMatchStatIds() {
		return playerMatchStatIds;
	}
	@Override
	public void setMatchStatIds(List<Long> playerMatchStatIds) {
		this.playerMatchStatIds = playerMatchStatIds;
	}
	@Override
	public List<IPlayerMatchStats> getMatchStats() {
		return playerMatchStats;
	}
	@Override
	public void setMatchStats(List<IPlayerMatchStats> pmsList) {
		if (pmsList != null) {
			this.playerMatchStatIds = new ArrayList<Long>();
			for (IPlayerMatchStats pms : pmsList) {
				this.playerMatchStatIds.add(pms.getId());
			}
		} else {
			this.playerMatchStatIds = null;
		}

		this.playerMatchStats = pmsList;
	}
	@Override
	public void addMatchStatId(Long playerMatchStatsId) {
		if (playerMatchStatIds == null) {
			playerMatchStatIds = new ArrayList<Long>();
		}

		playerMatchStatIds.add(playerMatchStatsId);

	}

	@Override
	public Long getQueryId() {
		return queryId;
	}

	@Override
	public void setQueryId(long queryId) {
		this.queryId = queryId;
	}
	
	@Override
	public String getDetails() {
		return details;
	}

	@Override
	public void setDetails(String details) {
		this.details = details;
	}

	@Override
	public void setRawScore(float rawScore) {
		this.rawScore = rawScore;
		
	}

	@Override
	public float getRawScore() {
		return rawScore;
	}
}