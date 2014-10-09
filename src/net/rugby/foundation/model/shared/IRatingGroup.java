package net.rugby.foundation.model.shared;

import java.util.List;


public interface IRatingGroup extends IHasId {

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract int getUniversalRoundOrdinal();

	public abstract void setUniversalRoundOrdinal(int universalRoundOrdinal);

	public abstract IRatingSeries getRatingSeries();

	public abstract void setRatingSeries(IRatingSeries ratingSeries);

	public abstract Long getRatingSeriesId();

	public abstract void setRatingSeriesId(Long ratingSeriesId);

	public abstract List<IRatingMatrix> getRatingMatrices();

	public abstract void setRatingMatrices(List<IRatingMatrix> ratingMatrices);

	public abstract List<Long> getRatingMatrixIds();

	public abstract void setRatingMatrixIds(List<Long> ratingMatrixIds);

	String getLabel();

	void setLabel(String label);

	UniversalRound getUniversalRound();

	void setUniversalRound(UniversalRound universalRound);

}