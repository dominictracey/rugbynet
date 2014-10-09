package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.google.appengine.datanucleus.annotations.Unindexed;
import com.googlecode.objectify.annotation.Entity;
import net.rugby.foundation.model.shared.Position.position;

@Entity
public class RatingQuery implements IRatingQuery, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3026327859974634819L;
	@Id
	private Long id;
	protected List<Long> compIds;
	protected List<Long> roundIds;
	protected List<Long> teamIds;
	protected List<Long> countryIds;
	protected List<position> positions;
	
	protected Status status;
	protected String label;
	
	@Unindexed
	protected boolean scaleTime = true;
	@Unindexed
	protected boolean scaleComp = true;
	@Unindexed
	protected boolean scaleStanding = true;
	@Transient
	protected IRatingMatrix ratingMatrix;
	protected Long ratingMatrixId;
	
	protected Long topTenListId;

	public RatingQuery() {
		compIds = new ArrayList<Long>();
		roundIds = new ArrayList<Long>();
		teamIds = new ArrayList<Long>();
		countryIds = new ArrayList<Long>();
		positions = new ArrayList<position>();
		
		status = Status.NEW;
	}
	
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingQuery#getCompIds()
	 */
	@Override
	public List<Long> getCompIds() {
		return compIds;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingQuery#getRoundIds()
	 */
	@Override
	public List<Long> getRoundIds() {
		return roundIds;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingQuery#getTeamIds()
	 */
	@Override
	public List<Long> getTeamIds() {
		return teamIds;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingQuery#getCountryIds()
	 */
	@Override
	public List<Long> getCountryIds() {
		return countryIds;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingQuery#getPositions()
	 */
	@Override
	public List<position> getPositions() {
		return positions;
	}
	@Override
	public void setCompIds(List<Long> compIds) {
		this.compIds = compIds;
	}

	@Override
	public void setRoundIds(List<Long> roundIds) {
		this.roundIds = roundIds;
	}

	@Override
	public void setTeamIds(List<Long> teamIds) {
		this.teamIds = teamIds;
	}

	@Override
	public void setCountryIds(List<Long> countryIds) {
		this.countryIds = countryIds;
	}

	@Override
	public void setPositions(List<position> positions) {
		this.positions = positions;
	}
	
	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		String sCompIds = "CompIds: null";
		if (compIds != null) {
			sCompIds = "CompIds: ";
			for (Long cid : compIds) {
				sCompIds += cid.toString() + ", ";
			}
		}
		
		String sRoundIds = "RoundIds: null";
		if (roundIds != null) {
			sRoundIds = "RoundIds: ";
			for (Long rid : roundIds) {
				sRoundIds += rid.toString() + ", ";
			}
		}
		
		String sCountryIds = "CountryIds: null";
		if (countryIds != null) {
			sCountryIds = "CountryIds: ";
			for (Long cid : countryIds) {
				sCountryIds += cid.toString() + ", ";
			}
		}
		
		String sTeamIds = "TeamIds : null";
		if (teamIds != null) {
			sTeamIds = "TeamIds : ";
			for (Long cid : teamIds) {
				sTeamIds += cid.toString() + ", ";
			}
		}
		
		String sPositions = "Positions : null";
		if (positions != null) {
			sPositions = "Positions : ";
			for (position p : positions) {
				sPositions += p + ", ";
			}
		}
		
		return "[RatingQuery " + sCompIds + " :: " + sRoundIds + " :: " + sCountryIds + " :: " +  sTeamIds + " :: " + sPositions + " ]";
	}

	@Override
	public boolean isTimeSeries() {
		if (roundIds != null && roundIds.size() > 1)
			return true;
		else
			return false;
	}
	@Override
	public Boolean getScaleTime() {
		return scaleTime;
	}
	@Override
	public void setScaleTime(Boolean scaleTime) {
		this.scaleTime = scaleTime;
	}
	@Override
	public boolean getScaleComp() {
		return scaleComp;
	}
	@Override
	public void setScaleComp(boolean scaleComp) {
		this.scaleComp = scaleComp;
	}
	@Override
	public boolean getScaleStanding() {
		return scaleStanding;
	}
	@Override
	public void setScaleStanding(boolean scaleStanding) {
		this.scaleStanding = scaleStanding;
	}

	@Override
	public void setRatingMatrix(IRatingMatrix ratingMatrix) {
		this.ratingMatrix = ratingMatrix;
		
	}

	@Override
	public IRatingMatrix getRatingMatrix() {
		return ratingMatrix;
	}

	@Override
	public void setRatingMatrixId(Long ratingMatrixId) {
		this.ratingMatrixId = ratingMatrixId;
		
	}

	@Override
	public Long getRatingMatrixId() {
		return ratingMatrixId;
	}
	@Override
	public Long getTopTenListId() {
		return topTenListId;
	}
	@Override
	public void setTopTenListId(Long topTenListId) {
		this.topTenListId = topTenListId;
	}
	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public void setLabel(String label) {
		this.label = label;
	}

}
