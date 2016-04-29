package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class RatingMatrix implements IRatingMatrix, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2894020263275496578L;
	
	@Id
	protected Long id;
	protected Date generated;
	
	protected Long ratingGroupId;
	@Transient
	protected IRatingGroup ratingGroup;
	
//	protected List<Long> roundIds;
//	@Transient
//	protected List<IRound> rounds;
	
	protected List<Long> ratingQueryIds;
	@Transient
	protected List<IRatingQuery> ratingQueries;
	
	protected Criteria listCalcCriteria;
	
	public RatingMatrix()
	{
//		roundIds = new ArrayList<Long>();
//		rounds = new ArrayList<IRound>();
		ratingQueryIds = new ArrayList<Long>();
		ratingQueries = new ArrayList<IRatingQuery>();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingMatrix#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingMatrix#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingMatrix#getGenerated()
	 */
	@Override
	public Date getGenerated() {
		return generated;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingMatrix#setGenerated(org.joda.time.Date)
	 */
	@Override
	public void setGenerated(Date generated) {
		this.generated = generated;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingMatrix#getRatingGroupId()
	 */
	@Override
	public Long getRatingGroupId() {
		return ratingGroupId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingMatrix#setRatingGroupId(java.lang.Long)
	 */
	@Override
	public void setRatingGroupId(Long ratingGroupId) {
		this.ratingGroupId = ratingGroupId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingMatrix#getRatingGroup()
	 */
	@Override
	public IRatingGroup getRatingGroup() {
		return ratingGroup;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingMatrix#setRatingGroup(net.rugby.foundation.model.shared.IRatingGroup)
	 */
	@Override
	public void setRatingGroup(IRatingGroup ratingGroup) {
		this.ratingGroup = ratingGroup;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingMatrix#getRoundIds()
	 */
//	@Override
//	public List<Long> getRoundIds() {
//		return roundIds;
//	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingMatrix#getRounds()
	 */
//	@Override
//	public List<IRound> getRounds() {
//		return rounds;
//	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingMatrix#getRatingQueryIds()
	 */
	@Override
	public List<Long> getRatingQueryIds() {
		return ratingQueryIds;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingMatrix#getRatingQueries()
	 */
	@Override
	public List<IRatingQuery> getRatingQueries() {
		return ratingQueries;
	}

	@Override
	public Criteria getCriteria() {
		return listCalcCriteria;
	}

	@Override
	public void setCriteria(Criteria crit) {
		listCalcCriteria = crit;
	}

//	@Override
//	public void setRoundIds(List<Long> roundIds) {
//		this.roundIds = roundIds;
//	}

	@Override
	public void setRatingQueries(List<IRatingQuery> ratingQueries) {
		this.ratingQueries = ratingQueries;
	}
	
}
