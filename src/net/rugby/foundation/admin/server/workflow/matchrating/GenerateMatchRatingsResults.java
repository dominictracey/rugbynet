package net.rugby.foundation.admin.server.workflow.matchrating;

import java.io.Serializable;
import java.util.List;

import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ITeamMatchStats;

public class GenerateMatchRatingsResults implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6754812367918042664L;
	public List<IPlayerMatchStats> homePlayerStats;
	public List<IPlayerMatchStats> visitPlayerStats;
	public ITeamMatchStats homeStats;
	public ITeamMatchStats visitStats;
	public String jobId;
	
	public GenerateMatchRatingsResults(	ITeamMatchStats homeStats,
										ITeamMatchStats visitStats,
										List<IPlayerMatchStats> homePlayerStats,
										List<IPlayerMatchStats> visitPlayerStats,
										String jobId)  {
		this.homePlayerStats = homePlayerStats;
		this.visitPlayerStats = visitPlayerStats;
		this.homeStats	= homeStats;
		this.visitStats = visitStats;
		this.jobId = jobId;
	}
}
