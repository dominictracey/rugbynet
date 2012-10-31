package net.rugby.foundation.model.shared;

import java.io.Serializable;

public class MatchPopupData implements Serializable {

private static final long serialVersionUID = 1L;

	private String matchDescription; // AUS vs. NZ
	private String matchLink; //link to Match group
	private Long overallRating;
	private Long positionRating;
	private Long classRating;
	
	public MatchPopupData() {
		; // needed for serialization
	}
	
	public MatchPopupData(String matchDescription, String matchLink,
			Long overallRating, Long positionRating, Long classRating) {
		super();
		this.matchDescription = matchDescription;
		this.matchLink = matchLink;
		this.overallRating = overallRating;
		this.positionRating = positionRating;
		this.classRating = classRating;
	}
	
	public String getMatchDescription() {
		return matchDescription;
	}
	public void setMatchDescription(String matchDescription) {
		this.matchDescription = matchDescription;
	}
	public String getMatchLink() {
		return matchLink;
	}
	public void setMatchLink(String matchLink) {
		this.matchLink = matchLink;
	}
	public Long getOverallRating() {
		return overallRating;
	}
	public void setOverallRating(Long overallRating) {
		this.overallRating = overallRating;
	}
	public Long getPositionRating() {
		return positionRating;
	}
	public void setPositionRating(Long positionRating) {
		this.positionRating = positionRating;
	}
	public Long getClassRating() {
		return classRating;
	}
	public void setClassRating(Long classRating) {
		this.classRating = classRating;
	}
	
	
}

