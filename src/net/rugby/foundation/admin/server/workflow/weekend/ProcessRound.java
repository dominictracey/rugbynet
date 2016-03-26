package net.rugby.foundation.admin.server.workflow.weekend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.server.workflow.ratingseries.CheckRatingGroup;
import net.rugby.foundation.admin.server.workflow.weekend.results.ProcessMatchResult;
import net.rugby.foundation.admin.server.workflow.weekend.results.ProcessRoundResult;
import net.rugby.foundation.admin.server.workflow.weekend.results.StandingsResult;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.RatingMode;

import com.google.appengine.tools.pipeline.FutureList;
import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class ProcessRound extends Job1<ProcessRoundResult, Long> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private IRoundFactory rf;
	transient private IRound round;
	transient private ISeriesConfigurationFactory scf;

	public ProcessRound() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}

	
	@Override
	public Value<ProcessRoundResult> run(Long roundId) {

		try {
			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.rf = injector.getInstance(IRoundFactory.class);
			this.scf = injector.getInstance(ISeriesConfigurationFactory.class);
			
			// valid round?
			round = rf.get(roundId);
			if (round == null) {
				ProcessRoundResult result = new ProcessRoundResult();
				result.roundId = roundId;
				result.log.add("No round could be found matching roundId " + roundId);
				return immediate(result);
			}
			
			List<Value<ProcessMatchResult>> _matchResults = new ArrayList<Value<ProcessMatchResult>>();
			FutureList<ProcessMatchResult> matchResults = new FutureList<ProcessMatchResult>(_matchResults);
			
			ISeriesConfiguration sc = scf.getByCompAndMode(round.getCompId(), RatingMode.BY_MATCH);
			Value<Long> groupId = futureCall(new CheckRatingGroup(), immediate(sc.getId()), immediate(round.getUrOrdinal()), immediate(round.getName()));
			
			JobSetting backOffFactor = new JobSetting.BackoffFactor(1);
			JobSetting backOffSeconds = new JobSetting.BackoffSeconds(10*60); // retry every 10 minutes
			JobSetting maxAttempts = new JobSetting.MaxAttempts(600); // about 4 days
			
			// kick off a ProcessMatch job for each match
			for (IMatchGroup m : round.getMatches()) {
				_matchResults.add(futureCall(new ProcessMatch(), immediate(m.getId()), groupId, immediate(m.getDisplayName()),  backOffFactor, backOffSeconds, maxAttempts));
			}
			
			IRound r = null;
			// get the standings for the next round when they are done
			// might have to skip some real world weeks to get to the next comp round 
			for (int i = 1; i < 15; ++i) {
				r = rf.getForUR(round.getCompId(), round.getUrOrdinal()+i);
				if (r != null) {
					break;
				}
			}
			
			Value<StandingsResult> standingsResult = null;
			
			if (r != null) {
				standingsResult = futureCall(new FetchStandings(), immediate(r.getId()), matchResults);
			} else {
				StandingsResult sr = new StandingsResult();
				sr.log.add("Couldn't find next round.");
				sr.roundId = roundId;
				sr.success = false;
				standingsResult = immediate(sr);
			}
			
			// compile the log to email to the admins in the WeekendFinalizeServlet
			FutureValue<ProcessRoundResult> retval = futureCall(new CompileRoundLog(), immediate(roundId), matchResults, standingsResult);

			return retval;
			
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			return null;
		}

	}

}
