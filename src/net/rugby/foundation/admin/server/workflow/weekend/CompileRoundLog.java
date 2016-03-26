package net.rugby.foundation.admin.server.workflow.weekend;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.workflow.weekend.results.ProcessMatchResult;
import net.rugby.foundation.admin.server.workflow.weekend.results.ProcessRoundResult;
import net.rugby.foundation.admin.server.workflow.weekend.results.StandingsResult;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.IRound;

import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class CompileRoundLog extends Job3<ProcessRoundResult, Long, List<ProcessMatchResult>, StandingsResult> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private IRoundFactory rf;

	public CompileRoundLog() {
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
	
	@Override
	public Value<ProcessRoundResult> run(Long roundId, List<ProcessMatchResult> matchResults, StandingsResult sr) {

		try {
			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}
		
			
			ProcessRoundResult retval = new ProcessRoundResult();
			boolean matchesSuccessful = true;
			for (ProcessMatchResult mr : matchResults) {
				if (mr != null)
					retval.log.addAll(mr.log);
				if (!mr.success)
					matchesSuccessful = false;
			}

			if (sr != null)
				retval.log.addAll(sr.log);
			
			if (matchesSuccessful) {
				this.rf = injector.getInstance(IRoundFactory.class);
				rf.dropFromCache(roundId);
				IRound r = rf.get(roundId); 
				if (r != null) {
					r.setWorkflowStatus(IRound.WorkflowStatus.FETCHED);				
					rf.put(r);
				}
			}
			
			return immediate(retval);
			
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			return null;
		}

	}

}
