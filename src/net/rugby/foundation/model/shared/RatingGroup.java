package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class RatingGroup implements Serializable, IRatingGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7432948720351616742L;
	@Id
	protected Long id;
	protected int universalRoundOrdinal;
	@Transient
	protected UniversalRound universalRound;
	protected String label;
	
	@Transient
	protected IRatingSeries ratingSeries;
	protected Long ratingSeriesId;
	
	@Transient
	protected List<IRatingMatrix> ratingMatrices;
	protected List<Long> ratingMatrixIds;
	
	public RatingGroup()
	{
		ratingMatrixIds = new ArrayList<Long>();
		ratingMatrices = new ArrayList<IRatingMatrix>();
		
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingGroup#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingGroup#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingGroup#getRatingSeries()
	 */
	@Override
	public IRatingSeries getRatingSeries() {
		return ratingSeries;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingGroup#setRatingSeries(net.rugby.foundation.model.shared.IRatingSeries)
	 */
	@Override
	public void setRatingSeries(IRatingSeries ratingSeries) {
		this.ratingSeries = ratingSeries;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingGroup#getRatingSeriesId()
	 */
	@Override
	public Long getRatingSeriesId() {
		return ratingSeriesId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingGroup#setRatingSeriesId(java.lang.Long)
	 */
	@Override
	public void setRatingSeriesId(Long ratingSeriesId) {
		this.ratingSeriesId = ratingSeriesId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingGroup#getRatingMatrices()
	 */
	@Override
	public List<IRatingMatrix> getRatingMatrices() {
		return ratingMatrices;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingGroup#setRatingMatrices(java.util.List)
	 */
	@Override
	public void setRatingMatrices(List<IRatingMatrix> ratingMatrices) {
		this.ratingMatrices = ratingMatrices;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingGroup#getRatingMatrixIds()
	 */
	@Override
	public List<Long> getRatingMatrixIds() {
		return ratingMatrixIds;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingGroup#setRatingMatrixIds(java.util.List)
	 */
	@Override
	public void setRatingMatrixIds(List<Long> ratingMatrixIds) {
		this.ratingMatrixIds = ratingMatrixIds;
	}
	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public void setLabel(String label) {
		this.label = label;
	}
	@Override
	public int getUniversalRoundOrdinal() {
		return universalRoundOrdinal;
	}
	@Override
	public void setUniversalRoundOrdinal(int universalRoundOrdinal) {
		this.universalRoundOrdinal = universalRoundOrdinal;
	}
	@Override
	public UniversalRound getUniversalRound() {
		return universalRound;
	}
	@Override
	public void setUniversalRound(UniversalRound universalRound) {
		this.universalRound = universalRound;
	}
	
}
