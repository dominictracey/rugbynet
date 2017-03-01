package net.rugby.foundation.admin.shared;

import java.util.Date;
import java.util.List;

import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IHasId;
import net.rugby.foundation.model.shared.IRatingQuery.MinMinutes;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.UniversalRound;

public interface ISeriesConfiguration  extends IHasId {

	/**
	 * OK - The LastRound completed successfully and the TargetRound hasn't been created yet
	 * PENDING - The TargetRound has been created but isn't complete yet
	 * RUNNING - The Series Orchestration is actively working on the TargetRound
	 * ERROR - The TargetRound has had an unrecoverable error happen
	 * @author home
	 *
	 */
	public enum Status { OK, 
						PENDING,
						RUNNING,
						ERROR }
	

	Long getId();
	void setId(Long id);
	List<Long> getTargets();
	/**
	 * OK - The LastRound completed successfully and the TargetRound hasn't been created yet
	 * PENDING - The TargetRound has been created but isn't complete yet
	 * RUNNING - The Series Orchestration is actively working on the TargetRound
	 * ERROR - The TargetRound has had an unrecoverable error happen
	 * @author home
	 *
	 */
	void setStatus(Status status);
	/**
	 * OK - The LastRound completed successfully and the TargetRound hasn't been created yet
	 * PENDING - The TargetRound has been created but isn't complete yet
	 * RUNNING - The Series Orchestration is actively working on the TargetRound
	 * ERROR - The TargetRound has had an unrecoverable error happen
	 * @author home
	 *
	 */
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
	void setSeriesId(Long ratingSeriesId);
	String getPipelineId();
	void setPipelineId(String pipelineId);
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
	Long getHostCompId();
	ICompetition getHostComp();
	void setHostCompId(Long hostCompId);
	void setHostComp(ICompetition hostComp);
	void setMinMinutes(int val);
	int getMinMinutes();
	void setMinMinuteType(MinMinutes isAs);
	MinMinutes getMinMinuteType();
}
