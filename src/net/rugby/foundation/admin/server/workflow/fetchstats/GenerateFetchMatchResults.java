package net.rugby.foundation.admin.server.workflow.fetchstats;

import java.io.Serializable;
import java.util.List;

import net.rugby.foundation.admin.server.workflow.ResultWithLog;

public class GenerateFetchMatchResults extends ResultWithLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6754812367918042664L;
	public List<Long> homePlayerStatIds;
	public List<Long> visitPlayerStatIds;
	public Long homeStatId;
	public Long visitStatsId;
	public String jobId;
	
	public GenerateFetchMatchResults(	Long homeStatId,
										Long visitStatsId,
										List<Long> homePlayerStatIds,
										List<Long> visitPlayerStatIds,
										String jobId)  {
		this.homePlayerStatIds = homePlayerStatIds;
		this.visitPlayerStatIds = visitPlayerStatIds;
		this.homeStatId	= homeStatId;
		this.visitStatsId = visitStatsId;
		this.jobId = jobId;
	}
}
