package net.rugby.foundation.model.shared;


import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IRatingSeries extends IHasId {

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract Date getStart();

	public abstract void setStart(Date start);

	public abstract Date getEnd();

	public abstract void setEnd(Date end);

	public abstract Date getCreated();

	public abstract void setCreated(Date created);

	public abstract Date getUpdated();

	public abstract void setUpdated(Date updated);

	public abstract boolean isLive();

	public abstract void setLive(boolean live);

	public abstract List<Long> getCountryIds();

	public abstract List<ICountry> getCountries();

	public abstract RatingMode getMode();

	public abstract void setMode(RatingMode mode);

	public abstract List<Criteria> getActiveCriteria();

	public abstract List<Long> getCompIds();

	public abstract Boolean isGlobal();

	public abstract void isGlobal(Boolean isGlobal);

	public abstract List<Long> getRatingGroupIds();

	public abstract void setRatingGroupIds(List<Long> ratingGroupIds);

	public abstract String getDisplayName();

	public abstract void setDisplayName(String displayName);

	void setHostCompId(Long hostCompId);

	Long getHostCompId();

	void setHostComp(ICompetition hostComp);

	ICompetition getHostComp();
	
	void setSponsor(Sponsor sponsor);
	Sponsor getSponsor();
	void setSponsorId(Long sponsorId);
	Long getSponsorId();

	public abstract Map<Long,String> getRatingGroupNameMap();

}