package net.rugby.foundation.admin.server.workflow.fetchstats;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.tools.pipeline.FutureList;
import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.ImmediateValue;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.Value;

import net.rugby.foundation.admin.server.workflow.fetchstats.FetchMatchStats.Home_or_Visitor;
import net.rugby.foundation.admin.server.workflow.weekend.MJ2Lineups;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS3LineupsAnnounced;
//import net.rugby.foundation.admin.server.workflow.fetchstats.FetchMatchStats.NameAndId;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS7StatsFetched;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ILineupSlot;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IRound;

public class ESPN0FetchMatchStats extends FetchMatchStats {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7423886544546008932L;
	private ICompetitionFactory cf;
	private IRoundFactory rf;

	@Override
	public Value<MS7StatsFetched> run(Long matchId) {

		if (injector == null) {
			injector = BPMServletContextListener.getInjectorForNonServlets();
		}

		this.pf = injector.getInstance(IPlayerFactory.class);
		this.mgf = injector.getInstance(IMatchGroupFactory.class);
		this.cf = injector.getInstance(ICompetitionFactory.class);
		this.rf = injector.getInstance(IRoundFactory.class);
		
		IMatchGroup match = mgf.get(matchId);
		IRound r = rf.get(match.getRoundId());
		ICompetition comp = cf.get(r.getCompId());
		
		if (match == null) {
			GenerateFetchMatchResults retval = new GenerateFetchMatchResults(null, null, null, null, "");
			retval.log.add("Invalid match Id provided: " + matchId);
			MS7StatsFetched wrapper = new MS7StatsFetched();
			wrapper.fetchSubTreeResults = retval;
			return immediate(wrapper);
		}
		
		if (match.getForeignUrl() == null) {
			// need to get score and find match details url before we do this
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Need to get scores and populate match URL before trying to get match stats for match " + match.getDisplayName());
			GenerateFetchMatchResults retval = new GenerateFetchMatchResults(null, null, null, null, "");
			retval.log.add("Need to get scores and populate match URL before trying to get match stats for match " + match.getDisplayName());
			MS7StatsFetched wrapper = new MS7StatsFetched();
			wrapper.fetchSubTreeResults = retval;
			return immediate(wrapper);
		}

		//String url = match.getForeignUrl(); //+"?view=scorecard";

		Logger.getLogger("FetchedPlayer").log(Level.INFO,"Starting generate match ratings for match " + match.getDisplayName());

		FutureValue<Long> homeTeamStats = futureCall(new ESPN6FetchTeamMatchStats(), immediate(match), immediate(Home_or_Visitor.HOME));
		FutureValue<Long> visitorTeamStats = futureCall(new ESPN6FetchTeamMatchStats(), immediate(match), immediate(Home_or_Visitor.VISITOR));
		
		// job settings controlling retries
		JobSetting nowBackOffFactor = new JobSetting.BackoffFactor(2);
		JobSetting nowBackOffSeconds = new JobSetting.BackoffSeconds(10); // retry at 10, 200 and 4000 seconds?
		JobSetting nowMaxAttempts = new JobSetting.MaxAttempts(7); // 
			
		FutureValue<MS3LineupsAnnounced> lineups = futureCall(new ESPN2FetchLineups(), immediate(matchId), nowBackOffFactor, nowBackOffSeconds, nowMaxAttempts);
		//Value<List<ILineupSlot>> visitLineups = getIds(Home_or_Visitor.VISITOR, url);

		List<Value<Long>> homePlayers = new ArrayList<Value<Long>>();
		List<Value<Long>> visitorPlayers = new ArrayList<Value<Long>>();
						
   

		FutureValue<List<Long>> playerMatchStats = futureCall(new ESPN3GenerateFetchPlayerMatchStats(), lineups, nowBackOffFactor, nowBackOffSeconds, nowMaxAttempts);


		//FutureList<IPlayerMatchStats> vpms = new FutureList<IPlayerMatchStats>(visitorPlayerMatchStats);

		// now we can invoke the engine
		//FutureValue<List<IPlayerMatchRating>> ratings = futureCall(new CreateMatchRatings(), immediate(match), hpms, vpms, homeTeamStats, visitorTeamStats);
		//Value<String> retVal = new ImmediateValue<String>(this.getPipelineKey().toString());
		
		return futureCall(new CompileMatchStats(), homeTeamStats, visitorTeamStats, playerMatchStats, null, new ImmediateValue<String>(this.getPipelineKey().toString()));

	}

}
