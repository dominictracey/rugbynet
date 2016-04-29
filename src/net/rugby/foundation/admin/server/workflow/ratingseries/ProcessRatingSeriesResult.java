package net.rugby.foundation.admin.server.workflow.ratingseries;

import java.io.Serializable;

import net.rugby.foundation.admin.server.workflow.ResultWithLog;

public class ProcessRatingSeriesResult extends ResultWithLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6754812367918042664L;

	public String jobId;
	
	public ProcessRatingSeriesResult(Boolean success, String jobId)  {
		this.jobId = jobId;
		this.success = success;
	}

	public ProcessRatingSeriesResult() {

	}
}
