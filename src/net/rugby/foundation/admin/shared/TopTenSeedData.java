package net.rugby.foundation.admin.shared;

import java.io.Serializable;
import java.util.List;

import net.rugby.foundation.model.shared.IPlayerRating;

public class TopTenSeedData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6761184923411466077L;
	protected List<IPlayerRating> pmiList;
	protected String title;
	protected String description;
	protected Long compId;
	protected Long ratingQueryId;
	protected int playersPerTeam = 10;
	protected List<Long> roundIds;
	private Long queryId;
	
	public TopTenSeedData() {
		
	}
	
	public TopTenSeedData(Long queryId, String title,
			String description, Long compId, List<Long> roundIds, Long ratingQueryId, int playersPerTeam) {
		super();
		this.setQueryId(queryId);
		this.title = title;
		this.description = description;
		this.compId = compId;
		this.ratingQueryId = ratingQueryId;
		this.playersPerTeam = playersPerTeam;
		this.roundIds = roundIds;
	}

	public List<IPlayerRating> getPmiList() {
		return pmiList;
	}
	public void setPmiList(List<IPlayerRating> pmiList) {
		this.pmiList = pmiList;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getCompId() {
		return compId;
	}
	public void setCompId(Long compId) {
		this.compId = compId;
	}

	public int getPlayersPerTeam() {
		return playersPerTeam;
	}

	public void setPlayersPerTeam(int playersPerTeam) {
		this.playersPerTeam = playersPerTeam;
	}

	public Long getRatingQueryId() {
		return ratingQueryId;
	}

	public void setRatingQueryId(Long ratingQueryId) {
		this.ratingQueryId = ratingQueryId;
	}

	public List<Long> getRoundIds() {
		return roundIds;
	}

	public void setRoundIds(List<Long> roundIds) {
		this.roundIds = roundIds;
	}

	public Long getQueryId() {
		return queryId;
	}

	public void setQueryId(Long queryId) {
		this.queryId = queryId;
	}
}
