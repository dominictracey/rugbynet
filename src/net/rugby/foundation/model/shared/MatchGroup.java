package net.rugby.foundation.model.shared;

import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Unindexed;

@Subclass
public class MatchGroup extends Group implements IMatchGroup {

	private static final long serialVersionUID = 1L;
	public MatchGroup() {
		setGroupType(GroupType.MATCH);
		homeTeamID = null;
		visitingTeamID = null;
		setStatus(Status.SCHEDULED);
	}


	private Date date;
	private Boolean locked = false;
	private Status status;
	private WorkflowStatus workflowStatus;
	
	@Transient
	private ISimpleScoreMatchResult simpleResult = null;
	private Long simpleResultId = null;
	
	@Transient
	private ITeamGroup homeTeam;
	private Long homeTeamID;

	@Transient
	private ITeamGroup visitingTeam;
	private Long visitingTeamID;

	private Long roundId; //parent object id
	private Long foreignId;
	private Long foreignLeagueId;
	private String foreignUrl;
	
	@Transient
	private IVenue venue;
	private Long venueId;
	
	protected String fetchMatchStatsPipelineId;
	
	protected String guid;
	
	@Unindexed
	private List<String> workflowLog;

	protected Long ESPNStreamId;
	protected Long compId;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		
		if (o != null) {
			if (o instanceof MatchGroup) {
				MatchGroup m = (MatchGroup)o;
				if (m.getDisplayName() != null && m.getHomeTeam() != null && m.getVisitingTeam() != null && m.getDate() != null) {
					return (m.getDisplayName().equals(getDisplayName()) &&
							m.getHomeTeam().equals(getHomeTeam()) &&
							m.getVisitingTeam().equals(getVisitingTeam()) &&
							m.getDate().equals(getDate()));
				}
			}
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#setDisplayName(net.rugby.foundation.model.shared.Group, net.rugby.foundation.model.shared.Group)
	 */
	public void setDisplayName(ITeamGroup home, ITeamGroup visitors)
	{
		setDisplayName(home.getDisplayName() + " vs. " + visitors.getDisplayName());
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#getHomeTeam()
	 */
	@Override
	public ITeamGroup getHomeTeam() {
		return homeTeam;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#setHomeTeam(com.googlecode.objectify.Key)
	 */
	@Override
	public void setHomeTeam(ITeamGroup homeTeam) {
		this.homeTeam = homeTeam;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#getVisitingTeam()
	 */
	@Override
	public ITeamGroup getVisitingTeam() {
		return visitingTeam;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#setVisitingTeam(com.googlecode.objectify.Key)
	 */
	@Override
	public void setVisitingTeam(ITeamGroup visitingTeam) {
		this.visitingTeam = visitingTeam;
	}

	  /* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#getGroupInfo()
	 */
	@Override
	  public String getGroupInfo() {
		return groupInfo;
	  }

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#getDate()
	 */
	@Override
	public Date getDate() {
		return date;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#setDate(java.util.Date)
	 */
	@Override
	public void setDate(Date date) {
		this.date = date;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#getLocked()
	 */
	@Override
	public Boolean getLocked() {
		return locked;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#setLocked(java.lang.Boolean)
	 */
	@Override
	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#getHomeTeamId()
	 */
	@Override
	public Long getHomeTeamId() {
		return homeTeamID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#setHomeTeamId(java.lang.Long)
	 */
	@Override
	public void setHomeTeamId(Long id) {
		homeTeamID = id;
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#getVisitingTeamId()
	 */
	@Override
	public Long getVisitingTeamId() {
		return visitingTeamID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#setVisitingTeamId(java.lang.Long)
	 */
	@Override
	public void setVisitingTeamId(Long id) {
		visitingTeamID = id;
		
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public void setStatus(Status status) {
		this.status = status;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#setDisplayName()
	 */
	@Override
	public void setDisplayName() {
		String homeTeamName = "TBD";
		String visitingTeamName = "TBD";
		if (homeTeam != null)
			homeTeamName = homeTeam.getDisplayName();
		if (visitingTeam != null) {
			visitingTeamName = visitingTeam.getDisplayName();
		}
		
		((IGroup)this).setDisplayName(homeTeamName + " vs. " + visitingTeamName); 
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#getSimpleScoreMatchResult()
	 */
    @JsonManagedReference
	@Override
	public ISimpleScoreMatchResult getSimpleScoreMatchResult() {
		return simpleResult;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#setSimpleScoreMatchResult(net.rugby.foundation.model.shared.ISimpleScoreMatchResult)
	 */
	@Override
	public void setSimpleScoreMatchResult(ISimpleScoreMatchResult result) {
		this.simpleResult = result;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#getSimpleScoreMatchResultId()
	 */
	@Override
	public Long getSimpleScoreMatchResultId() {
		return simpleResultId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#setSimpleScoreMatchResult(java.lang.Long)
	 */
	@Override
	public void setSimpleScoreMatchResultId(Long resultId) {
		this.simpleResultId = resultId;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#setRoundId(java.lang.Long)
	 */
	@Override
	public void setRoundId(Long id) {
		this.roundId = id;
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IMatchGroup#getRoundId()
	 */
	@Override
	public Long getRoundId() {
		return roundId;
	}

	@Override
	public void setForeignId(Long foreignId) {
		this.foreignId = foreignId;
		this.foreignUrl = "http://en.espn.co.uk/scrum/rugby/match/" + foreignId.toString() + ".html?view=scorecard";
	}

	@Override
	public Long getForeignId() {
		return foreignId;
	}

	@Override
	public String getForeignUrl() {
		return foreignUrl;
	}

	@Override
	public void setForeignUrl(String foreignUrl) {
		this.foreignUrl = foreignUrl;
	}

	public IVenue getVenue() {
		return venue;
	}

	public void setVenue(IVenue venue) {
		this.venue = venue;
	}

	public Long getVenueId() {
		return venueId;
	}

	public void setVenueId(Long venueId) {
		this.venueId = venueId;
	}

	@Override
	public void setFetchMatchStatsPipelineId(String pipelineId) {
		this.fetchMatchStatsPipelineId = pipelineId;
		
	}

	@Override
	public String getFetchMatchStatsPipelineId() {
		return fetchMatchStatsPipelineId;
	}

	@Override
	public WorkflowStatus getWorkflowStatus() {
		return workflowStatus;
	}

	@Override
	public void setWorkflowStatus(WorkflowStatus workflowStatus) {
		this.workflowStatus = workflowStatus;	
	}

	@Override
	public String getGuid() {
		return guid;
	}

	@Override
	public void setGuid(String guid) {
		this.guid = guid;
	}

	@Override
	public void setWorkflowLog(List<String> log) {
		this.workflowLog = log;	
	}
	
	@Override
	public List<String> getWorkflowLog() {
		return workflowLog;
	}
	@Override
	public Long getForeignLeagueId() {
		return foreignLeagueId;
	}
	@Override
	public void setForeignLeagueId(Long foreignLeagueId) {
		this.foreignLeagueId = foreignLeagueId;
	}
	@Override
	public Long getESPNStreamId() {
		return ESPNStreamId;
	}
	@Override
	public void setESPNStreamId(Long eSPNStreamId) {
		ESPNStreamId = eSPNStreamId;
	}
	@Override
	public Long getCompId() {
		return compId;
	}
	@Override
	public void setCompId(Long compId) {
		this.compId = compId;
	}

}
