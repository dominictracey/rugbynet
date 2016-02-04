package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Transient;

import net.rugby.foundation.model.shared.IMatchGroup.Status;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;


import com.googlecode.objectify.annotation.Entity;

@Entity
public class MatchResult implements Serializable, IMatchResult {

	private static final long serialVersionUID = 1L;
	private MatchGroup.Status status;
	
	@Id
	private Long id;
	private Long matchID;
	@JsonIgnore
	@Transient
	private IMatchGroup match;
	
	private Date recordedDate;
	private String source;
	private IMatchResult.ResultType type;
	
	public MatchResult()
	{
		
	}

	public MatchResult(Long matchID, Date recordedDate, String source) {
		super();
		this.matchID = matchID;
		this.recordedDate = recordedDate;
		this.source = source;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchResult#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchResult#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchResult#getMatchID()
	 */
	@Override
	public Long getMatchID() {
		return matchID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchResult#setMatchID(java.lang.Long)
	 */
	@Override
	public void setMatchID(Long matchID) {
		this.matchID = matchID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchResult#getRecordedDate()
	 */
	@Override
	public Date getRecordedDate() {
		return recordedDate;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchResult#setRecordedDate(java.util.Date)
	 */
	@Override
	public void setRecordedDate(Date recordedDate) {
		this.recordedDate = recordedDate;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchResult#getSource()
	 */
	@Override
	public String getSource() {
		return source;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchResult#setSource(java.lang.String)
	 */
	@Override
	public void setSource(String source) {
		this.source = source;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchResult#getType()
	 */
	@Override
	public IMatchResult.ResultType getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchResult#setType(net.rugby.foundation.model.shared.fetch.ResultFetcherFactory.ResultType)
	 */
	@Override
	public void setType(IMatchResult.ResultType type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchResult#getStatus()
	 */
	@Override
	public Status getStatus() {
		return status;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchResult#setStatus(net.rugby.foundation.model.shared.MatchGroup.Status)
	 */
	@Override
	public void setStatus(Status status) {
		this.status = status;
	}
//	
//	@JsonIgnore
//	@Override
//	public IMatchGroup getMatch() {
//		return match;
//	}
//	
//	@JsonIgnore
//	@Override
//	public void setMatch(IMatchGroup match) {
//		this.match = match;
//	}
}
