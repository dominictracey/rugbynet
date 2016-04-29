package net.rugby.foundation.admin.server.workflow.weekend.results;

import java.io.Serializable;
import java.util.List;

import net.rugby.foundation.admin.server.workflow.ResultWithLog;

public class RS3StandingsResult extends ResultWithLog implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -732033730743570348L;
	public Long roundId;
	
	public RS3StandingsResult(Long roundId, List<String> log)  {
		this.roundId = roundId;
		this.log = log;
	}

	public RS3StandingsResult() {
		
	}
}
