package net.rugby.foundation.admin.server.workflow.weekend.results;

import java.io.Serializable;
import java.util.List;

import net.rugby.foundation.admin.server.workflow.ResultWithLog;
import net.rugby.foundation.admin.server.workflow.fetchstats.GenerateFetchLineupsResults;

public class MS3LineupsAnnounced extends ResultWithLog implements Serializable  {


	/**
	 * 
	 */
	private static final long serialVersionUID = -732033730743570348L;
	public Long matchId;
	public Long compId;
	public GenerateFetchLineupsResults fetchSubTreeResults;
	
	public MS3LineupsAnnounced(Long matchId, Long compId, List<String> log)  {
		this.matchId = matchId;
		this.compId = compId;
		this.log = log;
	}

	public MS3LineupsAnnounced() {

	}
}
