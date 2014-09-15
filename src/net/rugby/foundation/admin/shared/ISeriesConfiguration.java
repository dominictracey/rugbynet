package net.rugby.foundation.admin.shared;

import java.util.Date;
import java.util.List;

import net.rugby.foundation.model.shared.IHasId;
import net.rugby.foundation.model.shared.IRound;

public interface ISeriesConfiguration  extends IHasId {
	enum ConfigurationType { BY_POSITION, BY_TEAM, BY_COUNTRY, ROUND_WRAPUP, ROUND_PREVIEW }
	
	Long getCompId();
	void setCompId(Long compId);
	Long getId();
	void setId(Long id);
	ConfigurationType getType();
	void setType(ConfigurationType type);
	List<Long> getTargets();
	//void setTargets(List<Long> targets);
	void setStatus(String status);
	String getStatus();
	void setLastRound(IRound lastRound);
	IRound getLastRound();
	void setLastRoundId(Long lastRoundId);
	Long getLastRoundId();
	String getCompName();
	void setCompName(String compName);
	Date getLastRun();
	void setLastRun(Date lastRun);
	Date getNextRun();
	void setNextRun(Date nextRun);
	Date getLastError();
	void setLastError(Date lastError);

}
