/**
 * 
 */
package net.rugby.foundation.game1.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;

/**
 * @author home
 *
 */
@Entity
public class Entry implements IEntry, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private Long id;
	private Long compId;
	
	private List<Long> roundEntryList = null; //RoundEntryId
	private Date lastUpdated;
	private Date created;
	private String name;
	private Long ownerId;
	private Integer score = 0;
	
	@Transient
	private Map<Long, IRoundEntry> roundEntries; //Key: RoundId
	
	public Entry() {
		roundEntryList = new ArrayList<Long>();
		roundEntries = new HashMap<Long, IRoundEntry>();
	}

	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IEntry#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IEntry#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IEntry#getCompId()
	 */
	@Override
	public Long getCompId() {
		return compId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IEntry#setCompId(java.lang.Long)
	 */
	@Override
	public void setCompId(Long compId) {
		this.compId = compId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IEntry#getRoundEntryMap()
	 */
	@Override
	public List<Long> getRoundEntryIdList() {
		return roundEntryList;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IEntry#setRoundEntryMap(java.util.Map)
	 */
	@Override
	public void setRoundEntryIdList(List<Long> roundEntryMap) {
		this.roundEntryList = roundEntryMap;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IEntry#getLastUpdated()
	 */
	@Override
	public Date getLastUpdated() {
		return lastUpdated;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IEntry#setLastUpdated(java.util.Date)
	 */
	@Override
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IEntry#getCreated()
	 */
	@Override
	public Date getCreated() {
		return created;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IEntry#setCreated(java.util.Date)
	 */
	@Override
	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IEntry#getRoundEntries()
	 */
	@Override
	public Map<Long, IRoundEntry> getRoundEntries() {
		return roundEntries;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IEntry#setRoundEntries(java.util.Map)
	 */
	@Override
	public void setRoundEntries(Map<Long, IRoundEntry> roundEntries) {
		this.roundEntries = roundEntries;
		
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IEntry#setOwnerId(java.lang.Long)
	 */
	@Override
	public void setOwnerId(Long id) {
		this.ownerId = id;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.IEntry#getOwnerId()
	 */
	@Override
	public Long getOwnerId() {
		return this.ownerId;
	}

	@Override
	public Integer getScore() {
		return score;
	}

	@Override
	public void setScore(Integer score) {
		this.score = score;
	}
}
