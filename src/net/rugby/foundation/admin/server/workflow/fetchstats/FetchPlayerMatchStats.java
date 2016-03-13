package net.rugby.foundation.admin.server.workflow.fetchstats;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.tools.pipeline.Job5;
import com.google.appengine.tools.pipeline.PromisedValue;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

import net.rugby.foundation.admin.server.factory.IAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.IPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.model.IPlayerMatchStatsFetcher;
import net.rugby.foundation.admin.server.workflow.fetchstats.FetchMatchStats.Home_or_Visitor;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;

public class FetchPlayerMatchStats extends Job5<IPlayerMatchStats, IPlayer, IMatchGroup, Home_or_Visitor, Integer, String> {
	private static Injector injector = null;
	private transient IPlayerMatchStatsFetcherFactory pmsff;
	private transient IPlayerMatchStatsFactory pmsf;
	private transient IAdminTaskFactory atf;
	private transient IPlayerMatchStatsFetcher fetcher;
	private transient IMatchGroupFactory mgf;
	protected transient IPlayer player;
	protected  transient IMatchGroup match;
	protected transient Home_or_Visitor hov;
	protected transient Integer slot;
	protected transient String url;
	
	
	public FetchPlayerMatchStats() {

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3101992931956737933L;

	@Override
	public Value<IPlayerMatchStats> run(IPlayer player, IMatchGroup match, Home_or_Visitor hov, Integer slot, String url) {

		this.player = player;
		this.match = match;
		this.hov = hov;
		this.slot = slot;
		this.url = url;
		
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
		if (injector == null) {
			injector  = BPMServletContextListener.getInjectorForNonServlets();
		}
		this.pmsff = injector.getInstance(IPlayerMatchStatsFetcherFactory.class);
		this.pmsf = injector.getInstance(IPlayerMatchStatsFactory.class);
		this.atf = injector.getInstance(IAdminTaskFactory.class);
		this.mgf = injector.getInstance(IMatchGroupFactory.class);

		if (player == null || match == null || player.getDisplayName() == null) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Bad name for player - bailing!!");
			match.setWorkflowStatus(WorkflowStatus.BLOCKED);
			mgf.put(match);
			PromisedValue<IPlayerMatchStats> x = newPromise(IPlayerMatchStats.class);
			IAdminTask task = atf.getNewEditPlayerMatchStatsTask("Couldn't get sufficient info for player in slot " + slot + " to collect match stats", "No player, match or player displayName", player, match, hov, slot, null, true, getPipelineKey().getName(), getJobKey().getName(), x.getHandle());		
			atf.put(task);
			return x;
		}
		
		fetcher = pmsff.getResultFetcher(player, match, hov, slot, url);
		
		IPlayerMatchStats stats = null;
		
		// there are three states we can be in after we try to get this player's match stats
		//	1. Everything awesome - carry on
		//		- process() == true && errorMessage == null;
		//	2. Issues, but we can still let the workflow continue, just have someone go have a look 
		//		- process() == true && errorMessage != null
		//	3. We got some or no stats, and there is/are issues that someone needs to look at before workflow can continue
		//		- process() == false && errorMessage != null

		if (fetcher.process()) {
			stats = fetcher.getStats();
			pmsf.put(stats);
			String errMess = fetcher.getErrorMessage();
			if (errMess != null && !errMess.isEmpty()) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Non-blocking issue in getting player match stats for " + player.getDisplayName() + " in match " + match.getDisplayName() + " : " + errMess);
				IAdminTask task = atf.getNewEditPlayerMatchStatsTask("Non-blocking issue in getting player match stats for " + player.getDisplayName() + " in match " + match.getDisplayName(), errMess, player, match, hov, slot, null, true, null, null, null);		
				atf.put(task);
				stats.getTaskIds().add(task.getId());
				pmsf.put(stats);
				if (match.getWorkflowStatus().equals(WorkflowStatus.BLOCKED)) {
					match.setWorkflowStatus(WorkflowStatus.TASKS_PENDING);
					mgf.put(match);
				}
			}
			return immediate(stats);
		} else { // blocking process
			stats = fetcher.getStats();
			if (stats == null) {
				// at least we have the matchId and playerId?
				stats = pmsf.create();
				stats.setPlayerId(player.getId());
				stats.setMatchId(match.getId());
				stats.setName("");
			}
			pmsf.put(stats);

			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Problem getting player match stats for " + player.getDisplayName() + " in match " + match.getDisplayName() + " : " + fetcher.getErrorMessage());
			PromisedValue<IPlayerMatchStats> x = newPromise(IPlayerMatchStats.class);
			IAdminTask task = atf.getNewEditPlayerMatchStatsTask("Problem getting player match stats for " + player.getDisplayName() + " in match " + match.getDisplayName() + " in slot " + slot, fetcher.getErrorMessage(), player, match, hov, slot, stats, true, getPipelineKey().getName(), getJobKey().getName(), x.getHandle());		
			atf.put(task);
			stats.getBlockingTaskIds().add(task.getId());
			pmsf.put(stats);
			match.setWorkflowStatus(WorkflowStatus.BLOCKED);
			mgf.put(match);
			return x;
		}

	}
	
//	public Value<IPlayerMatchStats> handleFailure(Throwable e) {
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Exception thrown getting player match stats for: " + e.getLocalizedMessage());
//		PromisedValue<IPlayerMatchStats> x = newPromise(IPlayerMatchStats.class);
//		IAdminTask task = atf.getNewEditPlayerMatchStatsTask("Problem getting player match stats: for " + player.getDisplayName() + " in match " + match.getDisplayName() + " in slot " + slot, e.getLocalizedMessage(), player, match, hov, slot, fetcher.getStats(), true, getPipelineKey().getName(), getJobKey().getName(), x.getHandle());		
//		atf.put(task);
//		return x;
//	}

}
