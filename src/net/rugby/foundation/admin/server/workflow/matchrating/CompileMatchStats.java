package net.rugby.foundation.admin.server.workflow.matchrating;

import java.util.List;
import com.google.appengine.tools.pipeline.Job5;
import com.google.appengine.tools.pipeline.Value;

import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ITeamMatchStats;

public class CompileMatchStats extends Job5<GenerateMatchRatingsResults, ITeamMatchStats, ITeamMatchStats, List<IPlayerMatchStats>, List<IPlayerMatchStats>, String> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 6387917945891986170L;

	public CompileMatchStats() {

	}

	@Override
	public Value<GenerateMatchRatingsResults> run(ITeamMatchStats hs, ITeamMatchStats vs, List<IPlayerMatchStats> hps, List<IPlayerMatchStats> vps, String job) {
		return (Value<GenerateMatchRatingsResults>)immediate(new GenerateMatchRatingsResults(hs, vs, hps, vps, job));
	}

}
