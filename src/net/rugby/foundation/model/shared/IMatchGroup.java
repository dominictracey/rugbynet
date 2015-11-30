/**
 * 
 */
package net.rugby.foundation.model.shared;

import java.util.Date;

/**
 * @author home
 *
 */
public interface IMatchGroup extends IGroup  {

	public enum Status {SCHEDULED, FINAL_HOME_WIN, FINAL_HOME_WIN_OT, FINAL_VISITOR_WIN, FINAL_VISITOR_WIN_OT, FINAL_DRAW, FINAL_DRAW_OT, UNDERWAY_FIRST_HALF, HALFTIME, UNDERWAY_SECOND_HALF, COMPLETE_AWAITING_RESULTS, POSTPONED, RESCHEDULED, CANCELED, UNREPORTED}
	public enum WorkflowStatus {PENDING, TASKS_PENDING, NO_STATS, FETCHED} 
	
	public abstract void setDisplayName();
	
	public abstract ITeamGroup getHomeTeam();
	public abstract void setHomeTeam(ITeamGroup homeTeam);
	public abstract ITeamGroup getVisitingTeam();
	public abstract void setVisitingTeam(ITeamGroup visitingTeam);
	
	public abstract Long getHomeTeamId();
	public abstract void setHomeTeamId(Long id);
	public abstract Long getVisitingTeamId();
	public abstract void setVisitingTeamId(Long id);

	public abstract String getGroupInfo();

	public abstract Date getDate();
	public abstract void setDate(Date date);

	public abstract Boolean getLocked();
	public abstract void setLocked(Boolean locked);

	public abstract ISimpleScoreMatchResult getSimpleScoreMatchResult();
	public abstract void setSimpleScoreMatchResult(ISimpleScoreMatchResult result);
	public abstract Long getSimpleScoreMatchResultId();
	public abstract void setSimpleScoreMatchResultId(Long resultId);
	
	public abstract Status getStatus();
	public abstract void setStatus(Status status);
	public abstract WorkflowStatus getWorkflowStatus();
	public abstract void setWorkflowStatus(WorkflowStatus status);
	
	public abstract String getDisplayName();
	public abstract void setDisplayName(String displayName);
	/**
	 * @param id
	 */
	public abstract void setRoundId(Long id);
	public abstract Long getRoundId();
	
	public abstract void setForeignId(Long foreignId);
	public abstract Long getForeignId();
	public abstract String getForeignUrl();
	public abstract void setForeignUrl(String foreignUrl);
	public abstract void setFetchMatchStatsPipelineId(String pipelineId);
	public abstract String getFetchMatchStatsPipelineId();

	/**
	 * 
	 * @return the url identifier to get to the match top ten list
	 */
	public abstract String getGuid();

	public abstract void setGuid(String guid);
	
}