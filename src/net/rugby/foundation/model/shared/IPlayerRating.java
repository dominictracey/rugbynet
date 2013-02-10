package net.rugby.foundation.model.shared;

import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;

public interface IPlayerRating {

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract Integer getRating();

	public abstract void setRating(Integer rating);

	public abstract Long getGroupId();

	public abstract void setGroupId(Long groupId);

	public abstract IGroup getGroup();

	public abstract void setGroup(IGroup group);

	public abstract Long getSchemaId();

	public abstract void setSchemaId(Long schemaId);

	public abstract IMatchRatingEngineSchema getSchema();

	public abstract void setSchema(IMatchRatingEngineSchema schema);

	public abstract void setPlayer(IPlayer player);

	public abstract void setPlayerId(Long playerId);

	public abstract IPlayer getPlayer();

	public abstract Long getPlayerId();
}