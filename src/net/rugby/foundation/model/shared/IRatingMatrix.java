package net.rugby.foundation.model.shared;

import java.util.Date;
import java.util.List;

public interface IRatingMatrix extends IHasId {

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract Date getGenerated();

	public abstract void setGenerated(Date generated);

	public abstract Long getRatingGroupId();

	public abstract void setRatingGroupId(Long ratingGroupId);

	public abstract IRatingGroup getRatingGroup();

	public abstract void setRatingGroup(IRatingGroup ratingGroup);

//	public abstract List<Long> getRoundIds();

	//public abstract List<IRound> getRounds();

	public abstract List<Long> getRatingQueryIds();

	public abstract List<IRatingQuery> getRatingQueries();

	public abstract Criteria getCriteria();
	
	public abstract void setCriteria(Criteria crit);

//	void setRoundIds(List<Long> roundIds);

	void setRatingQueries(List<IRatingQuery> ratingQueries);

}