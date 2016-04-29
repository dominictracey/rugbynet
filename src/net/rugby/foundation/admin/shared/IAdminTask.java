package net.rugby.foundation.admin.shared;

import java.util.Date;
import java.util.List;

public interface IAdminTask {

	public enum Action { EDITPLAYER, EDITMATCH, EDITSIMPLEMATCHRESULTS, EDITPLAYERMATCHSTATS, EDITTEAMMATCHSTATS, LOGBUG, EDITPLAYERTWITTER }
	public enum Status { OPEN, COMPLETE }
	public enum Priority { BLOCKER, SEVERE, MAJOR, MINOR, TRIVIAL }
	
	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract Action getAction();

	public abstract void setAction(Action action);

	public abstract Long getAdminId();

	public abstract void setAdminId(Long adminId);

	public abstract Date getCreated();

	public abstract void setCreated(Date created);

	public abstract Date getCompleted();

	public abstract void setCompleted(Date completed);

	public abstract Status getStatus();

	public abstract void setStatus(Status status);

	public abstract Priority getPriority();

	public abstract void setPriority(Priority priority);

	public abstract String getSummary();

	public abstract void setSummary(String summary);

	public abstract String getDetails();

	public abstract void setDetails(String details);

	public abstract List<String> getLog();

	public abstract void setLog(List<String> log);

	public abstract String getPromise();

	public abstract void setPromise(String promise);

	public abstract String getPipelineRoot();

	public abstract void setPipelineRoot(String pipelineRoot);

	public abstract String getPipelineJob();

	public abstract void setPipelineJob(String pipelineJob);
	
	public abstract String getPipelineUrl();
	
	public abstract void addLogItem(String logThis);

}