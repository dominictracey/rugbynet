package net.rugby.foundation.model.shared;

import javax.persistence.Id;
import java.io.Serializable;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;

import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;

@Entity
public class PlayerRating implements IPlayerRating, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7755010593959762373L;

	public PlayerRating() {
		
	}

	public PlayerRating(Integer rating, IPlayer player, IGroup group,
			IMatchRatingEngineSchema schema) {
		super();
		this.rating = rating;
		this.player = player;
		if (player != null) {
			this.playerId = player.getId();
		} else {
			this.playerId = null;
		}
		this.group = group;
		this.groupId = group.getId();
		this.schema = schema;
		this.schemaId = schema.getId();
		
	}

	@Id
	private Long id;
	private Integer rating;
	private Long groupId;
	@Transient
	private transient IGroup group;
	private Long schemaId;
	@Transient
	private transient IMatchRatingEngineSchema schema;
	private Long playerId;
	@Transient
	private transient IPlayer player;

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
	public IMatchRatingEngineSchema getSchema() {
		return schema;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IPlayerRating#setSchema(net.rugby.foundation.admin.server.model.IMatchRatingEngineSchema)
	 */
	@Override
	public void setSchema(IMatchRatingEngineSchema schema) {
		this.schema = schema;
	}

}