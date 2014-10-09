package net.rugby.foundation.admin.shared;

import java.util.Date;
import java.util.List;

import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IHasId;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.UniversalRound;

public interface ISeriesConfiguration  extends IHasId {

	public enum Status { OK, PENDING, RUNNING, ERROR }
	Long getId();
	void setId(Long id);
	List<Long> getTargets();
	void setStatus(Status status);
	Status getStatus();
	void setLastRoundOrdinal(int lastRound);
	int getLastRoundOrdinal();
	List<String> getCompNames();
	void setCompNames(List<String> compNames);
	Date getLastRun();
	void setLastRun(Date lastRun);
	Date getNextRun();
	void setNextRun(Date nextRun);
	Date getLastError();
	void setLastError(Date lastError);
	Long getSeriesId();
	IRatingSeries getSeries();
	void setSeriesId(Long ratingSeriesId);
	void setSeries(IRatingSeries ratingSeries);
	String getPipelineId();
	void setPipelineId(String pipelineId);
	void setComps(List<ICompetition> comps);
	List<ICompetition> getComps();
	void setCompIds(List<Long> compIds);
	List<Long> getCompIds();
	void setActiveCriteria(List<Criteria> activeCriteria);
	List<Criteria> getActiveCriteria();
	void setMode(RatingMode mode);
	List<Long> getCountryIds();
	void setCountryIds(List<Long> countryIds);
	List<ICountry> getCountries();
	void setCountries(List<ICountry> countries);
	RatingMode getMode();
	/**
	 *
	 * @param lastRound Last UniversalRound successfully processed
	 */
	void setLastRound(UniversalRound lastRound);
	/**
	 * 
	 * @return Last UniversalRound successfully processed
	 */
	UniversalRound getLastRound();
	/**
	 *
	 * @param targetRound The UniversalRound we want to process
	 */
	void setTargetRound(UniversalRound targetRound);
	/**
	 * 
	 * @return The UniversalRound we want to process
	 */
	UniversalRound getTargetRound();
	int getTargetRoundOrdinal();
	void setTargetRoundOrdinal(int targetRoundOrdinal);
	String getDisplayName();
	void setDisplayName(String displayName);
	Boolean getLive();
	void setLive(Boolean live);
}
