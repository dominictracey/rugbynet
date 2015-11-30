package net.rugby.foundation.admin.server.workflow.ratingseries;

import java.io.Serializable;
import net.rugby.foundation.model.shared.IRatingGroup;

public class ProcessRatingSeriesResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6754812367918042664L;
	public IRatingGroup group;
	public String jobId;
	
	public ProcessRatingSeriesResult(IRatingGroup group, String jobId)  {
		this.group = group;
		this.jobId = jobId;
	}
}
