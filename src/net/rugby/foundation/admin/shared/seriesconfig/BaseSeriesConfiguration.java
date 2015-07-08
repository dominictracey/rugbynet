package net.rugby.foundation.admin.shared.seriesconfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;

import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IRatingQuery.MinMinutes;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.UniversalRound;

@Entity
public class BaseSeriesConfiguration implements ISeriesConfiguration, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1436410203131200837L;
	
	@Id
	protected Long id;
	protected String displayName;
	
	protected List<String> compNames;
	protected List<Long> targets;
	
	protected int targetRoundOrdinal;
	@Transient
	protected UniversalRound targetRound;
	
	protected int lastRoundOrdinal;
	@Transient
	protected UniversalRound lastRound;
	
	protected Status status;
	protected Date lastRun;
	protected Date nextRun;
	protected Date lastError;
	
	@Transient
	protected IRatingSeries series;
	protected Long seriesId;
	
	protected String pipelineId;
	
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
	
	private Boolean live;
	
	private int minMinutes;
	private MinMinutes minMinuteType;

	public BaseSeriesConfiguration() {
		targets = new ArrayList<Long>();
		activeCriteria = new ArrayList<Criteria>();
		countryIds = new ArrayList<Long>();
		countries = new ArrayList<ICountry>();
		compIds = new ArrayList<Long>();
//		comps = new ArrayList<ICompetition>();
		countryIds = new ArrayList<Long>();
		live = false;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public List<Long> getTargets() {
		return targets;
	}

	@Override
	public Status getStatus() {
		return status;
	}
	
	@Override
	public void setStatus(Status status) {
		this.status = status;
	}
	
	@Override
	public Date getLastRun() {
		return lastRun;
	}

	@Override
	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}

	@Override
	public Date getNextRun() {
		return nextRun;
	}

	@Override
	public void setNextRun(Date nextRun) {
		this.nextRun = nextRun;
	}

	@Override
	public Date getLastError() {
		return lastError;
	}

	@Override
	public void setLastError(Date lastError) {
		this.lastError = lastError;
	}
	@Override
	public IRatingSeries getSeries() {
		return series;
	}
	@Override
	public void setSeries(IRatingSeries series) {
		this.series = series;
	}
	@Override
	public Long getSeriesId() {
		return seriesId;
	}
	@Override
	public void setSeriesId(Long seriesId) {
		this.seriesId = seriesId;
	}
	@Override
	public String getPipelineId() {
		return pipelineId;
	}
	@Override
	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}

	@Override
	public List<Long> getCountryIds() {
		return countryIds;
	}

	@Override
	public void setCountryIds(List<Long> countryIds) {
		this.countryIds = countryIds;
	}

	@Override
	public List<ICountry> getCountries() {
		return countries;
	}

	@Override
	public void setCountries(List<ICountry> countries) {
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

	@Override
	public void setActiveCriteria(List<Criteria> activeCriteria) {
		this.activeCriteria = activeCriteria;
	}

	@Override
	public List<Long> getCompIds() {
		return compIds;
	}

	@Override
	public void setCompIds(List<Long> compIds) {
		this.compIds = compIds;
	}

//	@Override
//	public List<ICompetition> getComps() {
//		return comps;
//	}
//
//	@Override
//	public void setComps(List<ICompetition> comps) {
//		this.comps = comps;
//	}

	@Override
	public List<String> getCompNames() {
		return compNames;
	}

	@Override
	public void setCompNames(List<String> compNames) {
		this.compNames = compNames;
	}

	@Override
	public int getLastRoundOrdinal() {
		return lastRoundOrdinal;
	}

	@Override
	public void setLastRoundOrdinal(int lastRoundOrdinal) {
		this.lastRoundOrdinal = lastRoundOrdinal;
	}

	@Override
	public UniversalRound getLastRound() {
		return lastRound;
	}

	@Override
	public void setLastRound(UniversalRound lastRound) {
		this.lastRound = lastRound;
	}

	@Override
	public UniversalRound getTargetRound() {
		return targetRound;
	}

	@Override
	public void setTargetRound(UniversalRound targetRound) {
		this.targetRound = targetRound;
	}
	@Override
	public int getTargetRoundOrdinal() {
		return targetRoundOrdinal;
	}
	@Override
	public void setTargetRoundOrdinal(int targetRoundOrdinal) {
		this.targetRoundOrdinal = targetRoundOrdinal;
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
	public Boolean getLive() {
		return live;
	}
	@Override
	public void setLive(Boolean live) {
		this.live = live;
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
	public ICompetition getHostComp() {
		return hostComp;
	}

	@Override
	public void setHostComp(ICompetition hostComp) {
		this.hostComp = hostComp;
	}

	@Override
	public int getMinMinutes() {
		return minMinutes;
	}

	@Override
	public void setMinMinutes(int minMinutes) {
		this.minMinutes = minMinutes;
	}

	@Override
	public MinMinutes getMinMinuteType() {
		return minMinuteType;
	}

	@Override
	public void setMinMinuteType(MinMinutes minMinuteType) {
		this.minMinuteType = minMinuteType;
	}


	

}
