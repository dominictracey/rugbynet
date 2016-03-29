package net.rugby.foundation.admin.server.workflow.weekend.results;

import java.io.Serializable;
import java.util.List;

import net.rugby.foundation.admin.server.workflow.ResultWithLog;

public class R0ProcessRoundResult extends ResultWithLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -158490895134567181L;
	public Long roundId;
	
	public R0ProcessRoundResult(Long roundId, List<String> log)  {
		this.roundId = roundId;
		this.log = log;
	}

	public R0ProcessRoundResult() {

	}
}
