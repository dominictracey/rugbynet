package net.rugby.foundation.admin.server.workflow.weekend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.admin.server.workflow.ResultWithLog;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS0ProcessMatchResult;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS4Underway;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS5Over;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS6Final;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS7StatsFetched;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS8Rated;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS9Promoted;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.model.shared.IMatchGroup;

import com.google.appengine.tools.pipeline.Job6;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class MJ9CompileMatchLog extends Job6<MS0ProcessMatchResult, MS4Underway, MS5Over, MS6Final, MS7StatsFetched, MS8Rated, MS9Promoted> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;
	private static Injector injector = null;
	transient private IMatchGroupFactory mf;
	
	public MJ9CompileMatchLog() {
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.INFO);
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
	public Value<MS0ProcessMatchResult> run(MS4Underway underway, MS5Over over, MS6Final finalized, MS7StatsFetched fetched, MS8Rated rated, MS9Promoted promoted) {

		try {
			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.mf = injector.getInstance(IMatchGroupFactory.class);
		
			MS0ProcessMatchResult retval = new MS0ProcessMatchResult();
			List<ResultWithLog> logs = new ArrayList<ResultWithLog>();
			retval.success = true;
	
			IMatchGroup match = mf.get(underway.matchId);
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Compiling match log output for " + match.getDisplayName());
			
			if (underway != null) {
				logs.add(underway);
				if (!underway.success)
					retval.success = false;
			}
			if (over != null) {
				logs.add(over);
				if (!over.success)
					retval.success = false;
			}
			if (finalized != null) {
				logs.add(finalized);
				if (!finalized.success)
					retval.success = false;
			}
			if (fetched != null) {
				logs.add(fetched);
				if (!fetched.success)
					retval.success = false;
			}
			if (rated != null) {
				logs.add(rated);
				if (!rated.success)
					retval.success = false;
			}
			if (promoted != null) {
				logs.add(promoted);
				if (!promoted.success)
					retval.success = false;
			}

			
			
			retval.log.add(match.getDisplayName() + "\n");			
			retval.log.addAll(ResultWithLog.aggregate(logs));
			
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Log output for " + match.getDisplayName() + ": " + retval.log);
			
			match.setWorkflowLog(retval.log);
			mf.put(match);
			
			AdminEmailer emailer = new AdminEmailer();
			emailer.setSubject(match.getDisplayName() + " workflow results");
			StringBuilder message = new StringBuilder();
			for (String s: retval.log) {
				message.append(s);
				message.append("<br/>\n");
			}
			emailer.setMessage(message.toString());
			emailer.send();
			
			return immediate(retval);
			
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			return null;
		}

	}

}
