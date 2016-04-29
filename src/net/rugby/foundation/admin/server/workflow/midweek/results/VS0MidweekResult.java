package net.rugby.foundation.admin.server.workflow.midweek.results;

import java.io.Serializable;
import java.util.List;

import net.rugby.foundation.admin.server.workflow.ResultWithLog;
import net.rugby.foundation.model.shared.UniversalRound;

public class VS0MidweekResult extends ResultWithLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -158490895134567181L;
	public UniversalRound ur;

	
	public VS0MidweekResult(UniversalRound ur, List<String> log)  {
		this.ur = ur;
		this.log = log;
	}

	public VS0MidweekResult() {

	}
}
