package net.rugby.foundation.admin.server.workflow.weekend.results;

import java.io.Serializable;
import java.util.List;

import net.rugby.foundation.admin.server.workflow.ResultWithLog;
import net.rugby.foundation.admin.server.workflow.fetchstats.GenerateFetchMatchResults;

public class MS7StatsFetched extends ResultWithLog implements Serializable  {


	/**
	 * 
	 */
	private static final long serialVersionUID = -732033730743570348L;
	public Long matchId;
	public Boolean blocked;
	public GenerateFetchMatchResults fetchSubTreeResults;
	
	public MS7StatsFetched(Long matchId, List<String> log)  {
		this.matchId = matchId;
		this.log = log;
	}

	public MS7StatsFetched() {

	}
}
