package net.rugby.foundation.admin.server.workflow.weekend.results;

import java.io.Serializable;
import java.util.List;

import net.rugby.foundation.admin.server.workflow.ResultWithLog;

public class ProcessMatchResult extends ResultWithLog implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -732033730743570348L;
	public Long matchId;
	
	public ProcessMatchResult(Long matchId, List<String> log)  {
		this.matchId = matchId;
		this.log = log;
	}

	public ProcessMatchResult() {

	}
}
