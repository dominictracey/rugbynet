package net.rugby.foundation.model.shared;

public interface IRawScore extends IHasId {

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract Long getPlayerMatchStatsId();

	public abstract void setPlayerMatchStatsId(Long playerMatchStatsId);

	public abstract Long getSchemaId();

	public abstract void setSchemaId(Long schemaId);

	public abstract Long getPlayerId();

	public abstract void setPlayerId(Long playerId);
	
	public abstract Float getRawScore();

	public abstract void setRawScore(Float rawScore);

	public abstract String getDetails();

	public abstract void setDetails(String string);

}