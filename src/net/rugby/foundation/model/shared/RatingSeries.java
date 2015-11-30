package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class RatingSeries implements Serializable, IRatingSeries {

	/**
	 * 
	 */
	private static final long serialVersionUID = -52134464616567385L;

	@Id
	protected Long id;
	protected String displayName;
	protected Date start;
	protected Date end;
	protected Date created;
	protected Date updated;
	protected boolean live;
	protected List<Long> countryIds;
	@Transient
	protected List<ICountry> countries;
	
	protected RatingMode mode;
	protected List<Criteria> activeCriteria;

	private List<Long> compIds;
//	@Transient
//	private List<ICompetition> comps;
	
	private Long hostCompId;
	@Transient
	private ICompetition hostComp;
	
	private Boolean isGlobal;

	@Transient
	private List<IRatingGroup> ratingGroups;
	private List<Long> ratingGroupIds;
	
	private Long sponsorId;
	@Transient
	private Sponsor sponsor;
	
	public RatingSeries()
	{
		setCountryIds(new ArrayList<Long>());
		setCompIds(new ArrayList<Long>());
		setCountries(new ArrayList<ICountry>());
//		setComps(new ArrayList<ICompetition>());	
		setActiveCriteria(new ArrayList<Criteria>());
		setRatingGroupIds(new ArrayList<Long>());
		setRatingGroups(new ArrayList<IRatingGroup>());
		isGlobal = false;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingSeries#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingSeries#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingSeries#getStart()
	 */
	@Override
	public Date getStart() {
		return start;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingSeries#setStart(org.joda.time.Date)
	 */
	@Override
	public void setStart(Date start) {
		this.start = start;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingSeries#getEnd()
	 */
	@Override
	public Date getEnd() {
		return end;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingSeries#setEnd(org.joda.time.Date)
	 */
	@Override
	public void setEnd(Date end) {
		this.end = end;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingSeries#getCreated()
	 */
	@Override
	public Date getCreated() {
		return created;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingSeries#setCreated(org.joda.time.Date)
	 */
	@Override
	public void setCreated(Date created) {
		this.created = created;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingSeries#getUpdated()
	 */
	@Override
	public Date getUpdated() {
		return updated;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingSeries#setUpdated(org.joda.time.Date)
	 */
	@Override
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingSeries#isLive()
	 */
	@Override
	public boolean isLive() {
		return live;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingSeries#setLive(boolean)
	 */
	@Override
	public void setLive(boolean live) {
		this.live = live;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingSeries#getCountryIds()
	 */
	@Override
	public List<Long> getCountryIds() {
		return countryIds;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingSeries#setCountryIds(java.util.List)
	 */
	protected void setCountryIds(List<Long> countryIds) {
		this.countryIds = countryIds;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingSeries#getCountries()
	 */
	@Override
	public List<ICountry> getCountries() {
		return countries;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IRatingSeries#setCountries(java.util.List)
	 */
	protected void setCountries(List<ICountry> countries) {
		this.countries = countries;
	}
	
	@Override
	public RatingMode getMode() {
		return mode;
	}
	
	@Override
	public void setMode(RatingMode mode) {
		this.mode = mode;
	}
	
	@Override
	public List<Criteria> getActiveCriteria() {
		return activeCriteria;
	}
	
	protected void setActiveCriteria(List<Criteria> activeCriteria) {
		this.activeCriteria = activeCriteria;
	}
	@Override
	public List<Long> getCompIds() {
		return compIds;
	}
	
	protected void setCompIds(ArrayList<Long> arrayList) {
		compIds = arrayList;
	}
//	@Override
//	public List<ICompetition> getComps() {
//		return comps;
//	}
//
//	protected void setComps(ArrayList<ICompetition> arrayList) {
//		comps = arrayList;
//	}
//
	@Override
	public Boolean isGlobal() {
		return isGlobal;
	}

	@Override
	public void isGlobal(Boolean isGlobal) {
		this.isGlobal = isGlobal;
	}

	@Override
	public List<IRatingGroup> getRatingGroups() {
		return ratingGroups;
	}

	@Override
	public void setRatingGroups(List<IRatingGroup> ratingGroups) {
		this.ratingGroups = ratingGroups;
	}

	@Override
	public List<Long> getRatingGroupIds() {
		return ratingGroupIds;
	}

	@Override
	public void setRatingGroupIds(List<Long> ratingGroupIds) {
		this.ratingGroupIds = ratingGroupIds;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	@Override
	public ICompetition getHostComp() {
		return hostComp;
	}

	@Override
	public void setHostComp(ICompetition hostComp) {
		this.hostComp = hostComp;
	}

	@Override
	public Long getHostCompId() {
		return hostCompId;
	}

	@Override
	public void setHostCompId(Long hostCompId) {
		this.hostCompId = hostCompId;
	}
	
	@Override
	public Long getSponsorId() {
		return sponsorId;
	}
	@Override
	public void setSponsorId(Long sponsorId) {
		this.sponsorId = sponsorId;
	}
	@Override
	public Sponsor getSponsor() {
		return sponsor;
	}
	@Override
	public void setSponsor(Sponsor sponsor) {
		this.sponsor = sponsor;
	}
}
