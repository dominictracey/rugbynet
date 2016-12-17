package net.rugby.foundation.admin.server.workflow.fetchstats;

import java.io.Serializable;
import java.util.List;

import net.rugby.foundation.admin.server.workflow.ResultWithLog;

public class GenerateFetchLineupsResults extends ResultWithLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6754812367918042664L;
	public List<Long> homeLineup;
	public List<Long> visitLineup;
//	public List<Long> homePlayers;
//	public List<Long> visitPlayers;
	public String jobId;
	
	public GenerateFetchLineupsResults(	List<Long> homeLineup,
										List<Long> visitLineup,
										String jobId)  {
		this.homeLineup = homeLineup;
		this.visitLineup = visitLineup;
//		this.homePlayers = hpsIds;
//		this.visitPlayers = vpsIds;
		this.jobId = jobId;
	}
}
