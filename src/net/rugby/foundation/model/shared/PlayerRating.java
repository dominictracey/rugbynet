package net.rugby.foundation.model.shared;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.Transient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;


@Entity
public class PlayerRating implements IPlayerRating, Serializable, Comparable<IPlayerRating> {

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
		if (query != null) {
			this.queryId = query.getId();
		}
		
		this.details = details;
	}
	
	@Entity
	public static class RatingComponent implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3979300783273508712L;
		

		public RatingComponent() {
			
		}
		
		public RatingComponent(String statsDetails, float backScore, float forwardScore, 
				float rawScore, Long playerMatchStatsId, String matchLabel, Integer scaledRating, Integer unscaledRating, Long scrumId, Long espnCompId) {
			this.backScore = backScore;
			this.forwardScore = forwardScore;
			this.rawScore = rawScore;
			this.setPlayerMatchStatsId(playerMatchStatsId);
			this.setStatsDetails(statsDetails);
			this.setMatchLabel(matchLabel);
			this.scaledRating = scaledRating;
			this.unscaledRating = unscaledRating;
			this.setScrumId(scrumId);
			this.setEspnCompId(espnCompId);
		}
		
		public void addRatingsDetails(String details) {
			details = ratingDetails + details;
			// truncate so we don't have GAE throwing exceptions and turning our String into Text and stuff
			if (details.length() > 500) {
				details = details.substring(0, 499);
			}
			this.ratingDetails = details;
			
		}

		@Id
		protected Long id;

		@Unindexed
		protected float backScore;
		@Unindexed
		protected float forwardScore;
		@Unindexed
		protected float rawScore; 
		@Unindexed		
		private Long playerMatchStatsId;
		@Unindexed
		protected transient String ratingDetails;
		@Unindexed
		protected transient String statsDetails;
		@Unindexed
		protected String matchLabel;
		@Unindexed
		protected float scaledRating;
		@Unindexed
		protected float unscaledRating;
		@Unindexed
		protected float offence;
		@Unindexed
		protected float defence;
		@Unindexed
		protected float setPlay;
		@Unindexed
		protected float loosePlay;
		@Unindexed
		protected float discipline;
		@Unindexed
		protected float matchResult;
		@Unindexed
		private Long scrumId;
		@Unindexed
		private Long espnCompId;
		
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

//		/***
//		 * 
//		 * @return the normalized (0-1000) score for this match's stats for this player without any time bias.
//		 */
//		public int getComputedScore() {
//			return computedScore;
//		}
//		public void setComputedScore(int computedScore) {
//			this.computedScore = computedScore;
//		}
//		/***
//		 * 
//		 * @return the normalized (0-1000) score for this match's stats for this player with time bias.
//		 */
//		public int getTimeWeighted() {
//			return timeWeighted;
//		}
//		public void setTimeWeighted(int timeWeighted) {
//			this.timeWeighted = timeWeighted;
//		}
		
		/***
		 * 
		 * @return The back component of the player's rating. Is a sum of the stat component and schema weight products.
		 */
		public float getBackScore() {
			return backScore;
		}
		public void setBackScore(float backScore) {
			this.backScore = backScore;
		}
		/***
		 * 
		 * @return The forward component of the player's rating. Is a sum of the stat component and schema weight products.
		 */
		public float getForwardScore() {
			return forwardScore;
		}
		public void setForwardScore(float forwardScore) {
			this.forwardScore = forwardScore;
		}
		/***
		 * 
		 * @return For forwards, this is the sum of the forwardScore and backScore. Backs get just their backScore.
		 */
		public float getRawScore() {
			return rawScore;
		}
		public void setRawScore(float rawScore) {
			this.rawScore = rawScore;
		}
		public Long getPlayerMatchStatsId() {
			return playerMatchStatsId;
		}
		public void setPlayerMatchStatsId(Long playerMatchStatsId) {
			this.playerMatchStatsId = playerMatchStatsId;
		}

		public void setStatsDetails(String details) {
			// truncate so we don't have GAE throwing exceptions and turning our String into Text and stuff
			if (details != null && details.length() > 500) {
				details = details.substring(0, 499);
			}
			this.statsDetails = details;
		}

		public String getMatchLabel() {
			return matchLabel;
		}
		
		public void setMatchLabel(String label) {
			matchLabel = label;
		}

		public float getScaledRating() {
			return scaledRating;
		}

		public void setScaledRating(float scaledRating) {
			this.scaledRating = scaledRating;
		}

		public float getUnscaledRating() {
			return unscaledRating;
		}

		public void setUnscaledRating(float unscaledRating) {
			this.unscaledRating = unscaledRating;
		}

		public String getRatingDetails() {
			return ratingDetails;
		}

		public String getStatsDetails() {
			return statsDetails;
		}

		public float getOffence() {
			return offence;
		}

		public void setOffence(float offence) {
			this.offence = offence;
		}

		public float getDefence() {
			return defence;
		}

		public void setDefence(float defence) {
			this.defence = defence;
		}

		public float getSetPlay() {
			return setPlay;
		}

		public void setSetPlay(float setPlay) {
			this.setPlay = setPlay;
		}

		public float getLoosePlay() {
			return loosePlay;
		}

		public void setLoosePlay(float loosePlay) {
			this.loosePlay = loosePlay;
		}

		public float getDiscipline() {
			return discipline;
		}

		public void setDiscipline(float discipline) {
			this.discipline = discipline;
		}

		public float getMatchResult() {
			return matchResult;
		}

		public void setMatchResult(float matchResult) {
			this.matchResult = matchResult;
		}

		public void setRatingDetails(String ratingDetails) {
			this.ratingDetails = ratingDetails;
		}

		public Long getScrumId() {
			return scrumId;
		}

		public void setScrumId(Long scrumId) {
			this.scrumId = scrumId;
		}

		public Long getEspnCompId() {
			return espnCompId;
		}

		public void setEspnCompId(Long espnCompId) {
			this.espnCompId = espnCompId;
		}
		
		
	}

	@Id
	protected Long id;
	protected Integer rating;
	protected Long groupId;

	// need to be indexed for prf.getBefore() query for clean up
	protected Date generated;
	
	@Transient
	protected IGroup group;
	protected Long schemaId;
	@Transient
	protected IRatingEngineSchema schema;
	protected Long playerId;
	@Transient
	protected IPlayer player;
	@Unindexed
	protected List<Long> playerMatchStatIds;
	@Transient
	private List<IPlayerMatchStats> playerMatchStats;
		
	protected Long queryId;
	@Unindexed
	protected String details;
	@Unindexed
	protected Float rawScore;
	
	@Embedded
	protected List<RatingComponent> ratingComponents;
	
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
		if (schema != null) {
			this.schemaId = schema.getId();
		}
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
		String name = "-o-";
		if (player != null)  {
			name = player.getDisplayName();
		}
		return "[PlayerRating:: player: " + name + " (" + playerId + ") rating: " + rating + "]";
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
		if (this.playerMatchStatIds == null) {
			this.playerMatchStatIds = new ArrayList<Long>();
		}
		if (pms != null && pms.getId() != null && !playerMatchStatIds.contains(pms.getId())) {
			this.playerMatchStatIds.add(pms.getId());
		}
	}

//	@Override
//	public int compareTo(IPlayerMatchRating o) {
//		if (rating == null) { 
//			return 1;
//		}
//
//		if (o.getRating() == null) {
//			return -1;
//		}
//
//		if (rating.equals(o.getRating())) {
//			return 0;
//		} else if (rating < o.getRating()) {
//			return 1;
//		} else {
//			return -1;
//		}
//	}
	
	@Override
	public int compareTo(IPlayerRating o) {
		if (rating == null) { 
			return 1;
		}

		if (o.getRating() == null) {
			return -1;
		}

		if (rating.equals(o.getRating())) {
			// same rating so let the guy who has played less matches be higher
			if (ratingComponents != null && o.getRatingComponents() != null)  {
			
				return ratingComponents.size() >= o.getRatingComponents().size() ? 1 : -1;
			} else {
				return 1;	
			}
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
		// truncate so we don't have GAE throwing exceptions and turning our String into Text and stuff
		if (details != null && details.length() > 500) {
			details = details.substring(0, 499);
		}
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

	@Override
	public List<RatingComponent> getRatingComponents() {
		if (ratingComponents == null) {
			ratingComponents = new ArrayList<RatingComponent>();
		}
		return ratingComponents;
	}

	@Override
	public void addRatingComponent(RatingComponent rc) {
		getRatingComponents().add(rc);
		
	}


}