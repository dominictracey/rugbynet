package net.rugby.foundation.admin.server.workflow.fetchstats;

import java.util.List;
import com.google.appengine.tools.pipeline.Job5;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamMatchStats;

public class CompileMatchStats extends Job5<GenerateFetchMatchResults, ITeamMatchStats, ITeamMatchStats, List<IPlayerMatchStats>, List<IPlayerMatchStats>, String> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 6387917945891986170L;
	private IMatchGroupFactory mf;
	private IRoundFactory rf;

	public CompileMatchStats() {

	}

	@Override
	public Value<GenerateFetchMatchResults> run(ITeamMatchStats hs, ITeamMatchStats vs, List<IPlayerMatchStats> hps, List<IPlayerMatchStats> vps, String job) {
		Injector injector = BPMServletContextListener.getInjectorForNonServlets();

		this.mf = injector.getInstance(IMatchGroupFactory.class);
		this.rf = injector.getInstance(IRoundFactory.class);
		
		IMatchGroup match = mf.get(hs.getMatchId());
		
		// TASKS_PENDING doesn't block us from getting here so we can't clear it
		if (match.getWorkflowStatus() != WorkflowStatus.TASKS_PENDING) {
			match.setWorkflowStatus(WorkflowStatus.FETCHED);
			mf.put(match);
		}
		
		IRound round = rf.get(match.getRoundId());
		boolean done = true;
		for (IMatchGroup m : round.getMatches()) {
			if (m.getWorkflowStatus() != WorkflowStatus.FETCHED) {
				done = false;
				break;
			}
		}
		
		if (done) {
			round.setWorkflowStatus(IRound.WorkflowStatus.FETCHED);
			rf.put(round);
			
			// send an admin email?
		}
		
		return (Value<GenerateFetchMatchResults>)immediate(new GenerateFetchMatchResults(hs, vs, hps, vps, job));
	}

}
