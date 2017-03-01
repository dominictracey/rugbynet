package net.rugby.foundation.admin.server.workflow.weekend;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.workflow.weekend.results.MS0ProcessMatchResult;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS8Rated;
import net.rugby.foundation.admin.server.workflow.weekend.results.R0ProcessRoundResult;
import net.rugby.foundation.admin.server.workflow.weekend.results.RS3StandingsResult;
import net.rugby.foundation.admin.server.workflow.weekend.results.RS5UpdateNextAndPreviousRoundsResult;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.IRound;

import com.google.appengine.tools.pipeline.Job6;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class RJ9CompileRoundLog extends Job6<R0ProcessRoundResult, Long, List<MS0ProcessMatchResult>, List<MS8Rated>, RS3StandingsResult, RS5UpdateNextAndPreviousRoundsResult, List<String>> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private IRoundFactory rf;

	public RJ9CompileRoundLog() {
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
	
	@Override
	public Value<R0ProcessRoundResult> run(Long roundId, List<MS0ProcessMatchResult> matchResults, List<MS8Rated> roundSeriesResults, RS3StandingsResult sr, RS5UpdateNextAndPreviousRoundsResult nprr, List<String> graphUpdateOutput) {

		try {
			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}
		
			
			R0ProcessRoundResult retval = new R0ProcessRoundResult();
			retval.success = true;  
			
			boolean matchesSuccessful = true;
			for (MS0ProcessMatchResult mr : matchResults) {
				if (mr != null)
					retval.log.addAll(mr.log);
				if (!mr.success)
					matchesSuccessful = false;
			}

			boolean ratingSeriesSuccessful = true;
			for (MS8Rated mr : roundSeriesResults) {
				if (mr != null) {
					retval.log.addAll(mr.log);
					if (!mr.success) {
						ratingSeriesSuccessful = false;
					}
				} else {
					ratingSeriesSuccessful = false; //unfortunately we don't know why we have a null in the list
				}
			}
			
			if (sr != null)
				retval.log.addAll(sr.log);
			
			this.rf = injector.getInstance(IRoundFactory.class);
			rf.dropFromCache(roundId);
			IRound r = rf.get(roundId); 
			if (r != null) {
				if (sr != null && sr.success) {
					r.setWorkflowStatus(IRound.WorkflowStatus.COMPLETE);
				} else if (ratingSeriesSuccessful) {
					r.setWorkflowStatus(IRound.WorkflowStatus.RATED);												
				} else if (matchesSuccessful) {				
					r.setWorkflowStatus(IRound.WorkflowStatus.FETCHED);				
				} else {
					retval.success = false; // TODO should we call it unsuccessful if we aren't COMPLETE?
				}
				rf.put(r);
			}
				

			return immediate(retval);
			
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			return null;
		}

	}

}
