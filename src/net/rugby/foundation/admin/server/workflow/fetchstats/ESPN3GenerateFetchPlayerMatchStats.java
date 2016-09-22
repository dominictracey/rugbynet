package net.rugby.foundation.admin.server.workflow.fetchstats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.espnscrum.ILineupFetcherFactory;
import net.rugby.foundation.admin.server.model.ILineupFetcher;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS3LineupsAnnounced;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.ILineupSlotFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ILineupSlot;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IRound;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.ImmediateValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

/***
 * Takes a list of LineupSlots and fetches the match stats for the players. Creates AdminTasks as needed.
 * @author dominictracey
 *
 */
//@Singleton
public class ESPN3GenerateFetchPlayerMatchStats extends Job1<List<Long>, MS3LineupsAnnounced> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	public enum Home_or_Visitor { HOME, VISITOR }

	protected static Injector injector = null;
	transient protected IPlayerFactory pf;
	transient protected IMatchGroupFactory mgf;
	private ILineupFetcherFactory luff;
	private ILineupSlotFactory lsf;
	private ICompetitionFactory cf;
	private IRoundFactory rf;

	
	public ESPN3GenerateFetchPlayerMatchStats() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}


	/**
	 * return generator jobId
	 * params String compName
	 * 			Long scrumId
	 * 			Long adminID
	 */		
	@Override
	public Value<List<Long>> run(MS3LineupsAnnounced lineup) {

		if (injector == null) {
			injector = BPMServletContextListener.getInjectorForNonServlets();
		}

		this.pf = injector.getInstance(IPlayerFactory.class);
		this.mgf = injector.getInstance(IMatchGroupFactory.class);
		this.lsf = injector.getInstance(ILineupSlotFactory.class);
		this.rf = injector.getInstance(IRoundFactory.class);
		this.cf = injector.getInstance(ICompetitionFactory.class);
		
		
		// lineup must be valid and not empty
		if (lineup == null || lineup.fetchSubTreeResults == null || 
				lineup.fetchSubTreeResults.homeLineup == null || lineup.fetchSubTreeResults.homeLineup.isEmpty() ||
				lineup.fetchSubTreeResults.visitLineup == null || lineup.fetchSubTreeResults.visitLineup.isEmpty()) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Invalid lineups provided.");
			return immediate(null);
		}
		
		List<Long> homeLineupIds = lineup.fetchSubTreeResults.homeLineup;
		List<Long> visitLineupIds = lineup.fetchSubTreeResults.visitLineup;		
		
		List<ILineupSlot> homeLineup = hydrateLineups(homeLineupIds);
		List<ILineupSlot> visitLineup = hydrateLineups(visitLineupIds);
		
		// all the LUS's should be for the same match
		Long matchId = homeLineup.get(0).getMatchId(); 
		if (matchId != null) {
			for (ILineupSlot lus: homeLineup) {
				if (!lus.getMatchId().equals(matchId)) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Job will only process one match at a time.");
					return immediate(null);
				}
			}
			for (ILineupSlot lus: visitLineup) {
				if (!lus.getMatchId().equals(matchId)) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Job will only process one match at a time.");
					return immediate(null);
				}
			}
		} else {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"No matchId for lineup items.");
			return immediate(null);
		}
		
		// and the match is good.
		IMatchGroup match = mgf.get(matchId);
		if (match == null) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"MatchId in lineup items invalid.");
			return immediate(null);
		}

		Logger.getLogger("FetchPlayerMatchStats").log(Level.INFO,"Starting fetching PMSs for match " + match.getDisplayName());

		// job settings controlling retries
		JobSetting nowBackOffFactor = new JobSetting.BackoffFactor(2);
		JobSetting nowBackOffSeconds = new JobSetting.BackoffSeconds(10); // retry at 10, 200 and 4000 seconds?
		JobSetting nowMaxAttempts = new JobSetting.MaxAttempts(7); // 
		
		List<Value<Long>> retval = new ArrayList<Value<Long>>();
		for (ILineupSlot lus: homeLineup) {
			FutureValue<Long> pmsId = futureCall(new ESPN5FetchPlayerMatchStats(), immediate(lus.getId()), nowBackOffFactor, nowBackOffSeconds, nowMaxAttempts);
			retval.add(pmsId);
		}
		
		for (ILineupSlot lus: visitLineup) {
			FutureValue<Long> pmsId = futureCall(new ESPN5FetchPlayerMatchStats(), immediate(lus.getId()), nowBackOffFactor, nowBackOffSeconds, nowMaxAttempts);
			retval.add(pmsId);
		}
				
		return futureList(retval);

	}


	private List<ILineupSlot> hydrateLineups(List<Long> ids) {
		List<ILineupSlot> retval = new ArrayList<ILineupSlot>();
		
		for (Long id : ids) {
			retval.add(lsf.get(id));
		}
		
		return retval;
	}



}
