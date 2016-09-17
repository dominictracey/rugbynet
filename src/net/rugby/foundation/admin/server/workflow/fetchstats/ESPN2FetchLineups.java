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
import net.rugby.foundation.model.shared.IGroup;
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

//@Singleton
public class ESPN2FetchLineups extends Job1<MS3LineupsAnnounced, Long> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	public enum Home_or_Visitor { HOME, VISITOR }

	protected static Injector injector = null;
	transient protected IPlayerFactory pf;
	transient protected IMatchGroupFactory mgf;
	private ILineupFetcherFactory luff;
	private ILineupSlotFactory lsf;
	private ICompetitionFactory cf;
	private IRoundFactory rf;

	
	public ESPN2FetchLineups() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}


	/**
	 * return generator jobId
	 * params String compName
	 * 			Long scrumId
	 * 			Long adminID
	 */		
	@Override
	public Value<MS3LineupsAnnounced> run(Long matchId) {

		if (injector == null) {
			injector = BPMServletContextListener.getInjectorForNonServlets();
		}

		this.pf = injector.getInstance(IPlayerFactory.class);
		this.mgf = injector.getInstance(IMatchGroupFactory.class);
		this.luff = injector.getInstance(ILineupFetcherFactory.class);
		this.lsf = injector.getInstance(ILineupSlotFactory.class);
		this.rf = injector.getInstance(IRoundFactory.class);
		this.cf = injector.getInstance(ICompetitionFactory.class);
		
		IMatchGroup match = mgf.get(matchId);
		if (match == null) {
			GenerateFetchLineupsResults retval = new GenerateFetchLineupsResults(null, null, "");
			retval.log.add("Invalid match Id provided: " + matchId);
			MS3LineupsAnnounced wrapper = new MS3LineupsAnnounced();
			wrapper.fetchSubTreeResults = retval;
			return immediate(wrapper);
		}
		
		if (match.getForeignUrl() == null) {
			// need to get foreign url before we do this. They should all be set now before the comp kicks off?
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Need to get scores and populate match URL before trying to get match stats for match " + match.getDisplayName());
			GenerateFetchLineupsResults retval = new GenerateFetchLineupsResults(null, null, "");
			retval.log.add("Need to get ESPN id for this match before trying to get lineups for match " + match.getDisplayName());
			MS3LineupsAnnounced wrapper = new MS3LineupsAnnounced();
			wrapper.fetchSubTreeResults = retval;
			return immediate(wrapper);
		}

		Logger.getLogger("FetchLineups").log(Level.INFO,"Starting fetching lineups for match " + match.getDisplayName());

		IRound r = rf.get(match.getRoundId());
		ICompetition comp = cf.get(r.getCompId());
		
		ILineupFetcher fetcher = luff.getLineupFetcher(comp.getCompType());
		fetcher.setComp(comp);
		fetcher.setMatch(match);
		
		List<ILineupSlot> homeLineup = fetcher.get(true);
		List<ILineupSlot> visitingLineup = fetcher.get(false);
		
		// did we get an error?
		if (fetcher.getErrorCode() != null && !fetcher.getErrorCode().isEmpty()) {
			// Something bad happened in the fetching
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"An error was returned trying to get line-ups for " + match.getDisplayName() + ": " + fetcher.getErrorCode() + " " + fetcher.getErrorMessage());
			GenerateFetchLineupsResults retval = new GenerateFetchLineupsResults(null, null, "");
			retval.log.add("An error was returned trying to get line-ups for " + match.getDisplayName() + ": " + fetcher.getErrorCode() + " " + fetcher.getErrorMessage());
			MS3LineupsAnnounced wrapper = new MS3LineupsAnnounced();
			wrapper.fetchSubTreeResults = retval;
			return immediate(wrapper);
		}
		
		// @REX can we get warnings here?

		// job settings controlling retries
		JobSetting nowBackOffFactor = new JobSetting.BackoffFactor(2);
		JobSetting nowBackOffSeconds = new JobSetting.BackoffSeconds(10); // retry at 10, 200 and 4000 seconds?
		JobSetting nowMaxAttempts = new JobSetting.MaxAttempts(7); // 
			
		List<Long> homeLineupSlotIds = new ArrayList<Long>();
		List<Long> visitorLineupSlotIds = new ArrayList<Long>();
		
		List<Value<Long>> homePlayers = new ArrayList<Value<Long>>();
		List<Value<Long>> visitorPlayers = new ArrayList<Value<Long>>();
		
		// make sure we have all the players
		for (ILineupSlot lus : homeLineup) {
			lsf.put(lus);
			homeLineupSlotIds.add(lus.getId());
			// first see if we have it in the database
			IPlayer dbPlayer = pf.getByScrumId(lus.getForeignPlayerId());

			// will return a "blank player" if it can't find it in the DB so check if the returned player has a scrum ID set.
			if (dbPlayer != null && dbPlayer.getScrumId() != null) {
				homePlayers.add(immediate(dbPlayer.getId()));
			} else {
				FutureValue<Long> homePlayerId = futureCall(new FetchPlayerByScrumId(), immediate(lus.getForeignPlayerName()),  immediate("no URL available"), immediate(lus.getForeignPlayerId()), immediate(1L), immediate(match.getId()), nowBackOffFactor, nowBackOffSeconds, nowMaxAttempts);
				homePlayers.add(homePlayerId);
			}
		}

		for (ILineupSlot lus : visitingLineup) {
			lsf.put(lus); 
			visitorLineupSlotIds.add(lus.getId());
			// first see if we have it in the database
			IPlayer dbPlayer = pf.getByScrumId(lus.getForeignPlayerId());


			// will return a "blank player" if it can't find it in the DB so check if the returned player has a scrum ID set.
			if (dbPlayer != null && dbPlayer.getScrumId() != null) {
				visitorPlayers.add(immediate(dbPlayer.getId()));
			} else {
				Value<Long> visitPlayerId = futureCall(new FetchPlayerByScrumId(), immediate(lus.getForeignPlayerName()),  immediate("no URL available"), immediate(lus.getForeignPlayerId()), immediate(1L), immediate(match.getId()), nowBackOffFactor, nowBackOffSeconds, nowMaxAttempts);
				visitorPlayers.add(visitPlayerId);
			}
		}	   

				
		return futureCall(new CompileLineups(), immediate(matchId), immediate(homeLineupSlotIds), immediate(visitorLineupSlotIds), futureList(homePlayers), futureList(visitorPlayers), new ImmediateValue<String>(this.getPipelineKey().toString()));

	}



}
