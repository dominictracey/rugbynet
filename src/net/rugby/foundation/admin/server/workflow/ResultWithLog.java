package net.rugby.foundation.admin.server.workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class ResultWithLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2808505364005231667L;
	public List<String> log = new ArrayList<String>();
	public Boolean success = false;
	public static List<String> aggregate(List<ResultWithLog> logs) {
		List<String> retval = new ArrayList<String>();
		for (ResultWithLog r : logs) {
			if (r != null) {
				retval.addAll(r.log);
			}
		}
		
		return retval;
	}
}
