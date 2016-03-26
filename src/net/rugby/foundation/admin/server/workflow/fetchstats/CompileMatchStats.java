package net.rugby.foundation.admin.server.workflow.fetchstats;

import java.util.ArrayList;
import java.util.List;
import com.google.appengine.tools.pipeline.Job5;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

import net.rugby.foundation.admin.server.workflow.weekend.results.MS7StatsFetched;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;
import net.rugby.foundation.model.shared.ITeamMatchStats;

public class CompileMatchStats extends Job5<MS7StatsFetched, Long, Long, List<Long>, List<Long>, String> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 6387917945891986170L;
	private transient IMatchGroupFactory mf;
	//private transient IRoundFactory rf;
	private transient ITeamMatchStatsFactory tmsf;

	public CompileMatchStats() {

	}

	@Override
	public Value<MS7StatsFetched> run(Long hsId, Long vsId, List<Long> hpsIds, List<Long> vpsIds, String job) {
		Injector injector = BPMServletContextListener.getInjectorForNonServlets();

		this.mf = injector.getInstance(IMatchGroupFactory.class);
		//this.rf = injector.getInstance(IRoundFactory.class);
		this.tmsf = injector.getInstance(ITeamMatchStatsFactory.class);
		
		List<String> log = new ArrayList<String>();

		
		ITeamMatchStats hs = tmsf.get(hsId);
		IMatchGroup match = null;
		if (hs == null) {
			GenerateFetchMatchResults retval = new GenerateFetchMatchResults(hsId, vsId, hpsIds, vpsIds, job);
			
			MS7StatsFetched wrapper = new MS7StatsFetched();
			wrapper.fetchSubTreeResults = retval;
			wrapper.success = false;
			wrapper.log.add("Bad TeamMatchStats id passed in");
			
			return immediate(wrapper);
		} else {
			match = mf.get(hs.getMatchId());
		}
		

		// all AdminTasks have been completed when we get here so update the WorkflowState
		match.setWorkflowStatus(WorkflowStatus.FETCHED);
		mf.put(match);
		log.add("Stats fetched for " + match.getDisplayName());

//		
//		IRound round = rf.get(match.getRoundId());
//		boolean done = true;
//		for (IMatchGroup m : round.getMatches()) {
//			if (m.getWorkflowStatus() != WorkflowStatus.FETCHED) {
//				done = false;
//				break;
//			}
//		}
//		
//		if (done) {
//			round.setWorkflowStatus(IRound.WorkflowStatus.FETCHED);
//			rf.put(round);
//			
//			// send an admin email?
//			
//			log.add("Done fetching stats for matches in " + round.getName());
//		}
		
		GenerateFetchMatchResults retval = new GenerateFetchMatchResults(hsId, vsId, hpsIds, vpsIds, job);
		retval.log.addAll(log);
		MS7StatsFetched wrapper = new MS7StatsFetched();
		wrapper.fetchSubTreeResults = retval;
		wrapper.matchId = match.getId();
		wrapper.success = true;
		
		return (Value<MS7StatsFetched>)immediate(wrapper);
	}

}
