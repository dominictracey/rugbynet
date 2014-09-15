package net.rugby.foundation.admin.shared.seriesconfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;

import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.model.shared.IRound;

@Entity
public class BaseSeriesConfiguration implements ISeriesConfiguration, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1436410203131200837L;
	
	@Id
	protected Long id;
	protected Long compId;
	@Transient
	protected String compName;
	protected ConfigurationType type;
	protected List<Long> targets;
	protected Long lastRoundId;
	@Transient
	protected IRound lastRound;
	protected String status;
	protected Date lastRun;
	protected Date nextRun;
	protected Date lastError;
	
	

	public BaseSeriesConfiguration() {
		targets = new ArrayList<Long>();
	}
	
	@Override
	public Long getCompId() {
		return compId;
	}

	@Override
	public void setCompId(Long compId) {
		this.compId = compId;
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
	public ConfigurationType getType() {
		return type;
	}

	@Override
	public void setType(ConfigurationType type) {
		this.type = type;
	}
	
	@Override
	public List<Long> getTargets() {
		return targets;
	}
	
	@Override
	public Long getLastRoundId() {
		return lastRoundId;
	}

	@Override
	public void setLastRoundId(Long lastRoundId) {
		this.lastRoundId = lastRoundId;
	}

	@Override
	public IRound getLastRound() {
		return lastRound;
	}

	@Override
	public void setLastRound(IRound lastRound) {
		this.lastRound = lastRound;
	}

	@Override
	public String getStatus() {
		return status;
	}
	
	@Override
	public String getCompName() {
		return compName;
	}
	
	@Override
	public void setCompName(String compName) {
		this.compName = compName;
	}

	@Override
	public void setStatus(String status) {
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
	
//	@Override
//	public void setTargets(List<Long> targets) {
//		this.targets = targets;
//	}

}
