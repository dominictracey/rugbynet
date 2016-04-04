package net.rugby.foundation.admin.server.workflow.weekend.results;

import java.io.Serializable;
import java.util.List;

import net.rugby.foundation.admin.server.workflow.ResultWithLog;

public class MS5Over extends ResultWithLog implements Serializable  {


	/**
	 * 
	 */
	private static final long serialVersionUID = -732033730743570348L;
	public Long matchId;
	
	public MS5Over(Long matchId, List<String> log)  {
		this.matchId = matchId;
		this.log = log;
	}

	public MS5Over() {

	}
}
