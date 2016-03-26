package net.rugby.foundation.admin.server.workflow.weekend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.workflow.ResultWithLog;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS4Underway;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS5Over;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS6Final;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS7StatsFetched;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS8Rated;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS9Promoted;
import net.rugby.foundation.admin.server.workflow.weekend.results.ProcessMatchResult;
import com.google.appengine.tools.pipeline.Job6;
import com.google.appengine.tools.pipeline.Value;

//@Singleton
public class MJ9CompileMatchLog extends Job6<ProcessMatchResult, MS4Underway, MS5Over, MS6Final, MS7StatsFetched, MS8Rated, MS9Promoted> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	public MJ9CompileMatchLog() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}


	protected class NameAndId {
		String name;
		Long id;

		NameAndId(Long id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	/**
	 * return generator jobId
	 * params String compName
	 * 			Long scrumId
	 * 			Long adminID
	 */		
	@Override
	public Value<ProcessMatchResult> run(MS4Underway underway, MS5Over over, MS6Final finalized, MS7StatsFetched fetched, MS8Rated rated, MS9Promoted promoted) {

		try {
			
		
			ProcessMatchResult retval = new ProcessMatchResult();
			List<ResultWithLog> logs = new ArrayList<ResultWithLog>();
			if (underway != null)
				logs.add(underway);
			if (over != null) 
				logs.add(over);
			if (finalized != null)
				logs.add(finalized);
			if (fetched != null)
				logs.add(fetched);
			if (rated != null)
				logs.add(rated);
			if (promoted != null)
				logs.add(promoted);
			
			retval.log = ResultWithLog.aggregate(logs);

			return immediate(retval);
			
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			return null;
		}

	}

}
