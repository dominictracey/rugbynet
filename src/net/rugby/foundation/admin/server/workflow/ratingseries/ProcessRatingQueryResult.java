package net.rugby.foundation.admin.server.workflow.ratingseries;

import java.io.Serializable;

import net.rugby.foundation.admin.server.workflow.ResultWithLog;

public class ProcessRatingQueryResult extends ResultWithLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6754812367918042664L;
	public Long queryId;
	public Boolean success;
	
	public ProcessRatingQueryResult(Long queryId, boolean success)  {
		this.queryId = queryId;
		this.success = success;
	}

	public ProcessRatingQueryResult() {
		
	}
}
