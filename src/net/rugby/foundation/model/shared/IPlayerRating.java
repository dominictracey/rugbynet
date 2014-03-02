package net.rugby.foundation.model.shared;

import java.util.Date;
import java.util.List;

import net.rugby.foundation.admin.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.PlayerRating.RatingComponent;

public interface IPlayerRating extends IHasId {

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

	public abstract IRatingEngineSchema getSchema();

	public abstract void setSchema(IRatingEngineSchema schema);

	public abstract void setPlayer(IPlayer player);

	public abstract void setPlayerId(Long playerId);

	public abstract IPlayer getPlayer();

	public abstract Long getPlayerId();
	
	public abstract Date getGenerated();
	
	public abstract void setGenerated(Date generated);

//	int compareTo(IPlayerMatchRating o);

	void addMatchStats(IPlayerMatchStats playerMatchStats);

	void setMatchStats(List<IPlayerMatchStats> pmsList);

	List<IPlayerMatchStats> getMatchStats();

	void setMatchStatIds(List<Long> playerMatchStatIds);

	List<Long> getMatchStatIds();

	void addMatchStatId(Long playerMatchStatsId);
	
	Long getQueryId();

	void setQueryId(long queryId);

	String getDetails();

	void setDetails(String details);

	public abstract void setRawScore(float f);

	public abstract float getRawScore();

	List<RatingComponent> getRatingComponents();

	void addRatingComponent(RatingComponent rc);

}