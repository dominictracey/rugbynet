package net.rugby.foundation.model.shared;

import java.util.List;

import net.rugby.foundation.model.shared.Position.position;

public interface IRatingQuery extends IHasId {
	/**
	 * ROUND - getMinMinutes returns the number of minutes the player has to have played on average, per round
	 * TOTAL - getMinMinutes returns the number of minutes the player has to have played over the period of the query (usually 12 months)
	 * @author home
	 *
	 */
	public enum MinMinutes {  
		ROUND,
		TOTAL }
	

	public abstract List<Long> getCompIds();

	public abstract List<Long> getRoundIds();

	public abstract List<Long> getTeamIds();

	public abstract List<Long> getCountryIds();

	public abstract List<position> getPositions();

	void setCompIds(List<Long> compIds);

	void setRoundIds(List<Long> roundIds);

	void setTeamIds(List<Long> teamIds);

	void setCountryIds(List<Long> countryIds);

	void setPositions(List<position> positions);

	public enum Status { NEW, RUNNING, COMPLETE, ERROR }

	Status getStatus();

	void setStatus(Status status);
	
	boolean isTimeSeries();

	Boolean getScaleTime();

	void setScaleTime(Boolean scaleTime);

	boolean getScaleComp();

	void setScaleComp(boolean scaleComp);

	boolean getScaleStanding();

	void setScaleStanding(boolean scaleStanding);

	public abstract void setRatingMatrix(IRatingMatrix ratingMatrix);
	
	public abstract IRatingMatrix getRatingMatrix();
	
	public abstract void setRatingMatrixId(Long ratingMatrixId);
	
	public abstract Long getRatingMatrixId();

	Long getTopTenListId();

	void setTopTenListId(Long topTenListId);

	public abstract String getLabel();
	public abstract void setLabel(String name);

	Long getSchemaId();

	void setSchemaId(Long schemaId);
	public abstract Boolean getInstrument();
	public abstract void setInstrument(Boolean instrument);

	public abstract void setScaleMinutesPlayed(Boolean scaleMinutesPlayed);
	public abstract Boolean getScaleMinutesPlayed();

	void setMinMinutes(int minMinutes);

	int getMinMinutes();

	void setMinMinutesType(MinMinutes minMinutesType);

	MinMinutes getMinMinutesType();
}