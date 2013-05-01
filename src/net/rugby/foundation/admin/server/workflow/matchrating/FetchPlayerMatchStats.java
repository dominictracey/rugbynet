package net.rugby.foundation.admin.server.workflow.matchrating;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.tools.pipeline.Job5;
import com.google.appengine.tools.pipeline.PromisedValue;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

import net.rugby.foundation.admin.server.UrlCacher;
import net.rugby.foundation.admin.server.factory.IAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.IPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.model.IPlayerMatchStatsFetcher;
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.Position;

public class FetchPlayerMatchStats extends Job5<IPlayerMatchStats, IPlayer, IMatchGroup, Home_or_Visitor, Integer, String> {
	private static Injector injector = null;
	private IPlayerMatchStatsFetcherFactory pmsff;
	private IPlayerMatchStatsFactory pmsf;
	private IAdminTaskFactory atf;
	private IPlayerMatchStatsFetcher fetcher;
	
	public FetchPlayerMatchStats() {

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3101992931956737933L;

	@Override
	public Value<IPlayerMatchStats> run(IPlayer player, IMatchGroup match, Home_or_Visitor hov, Integer slot, String url) {

		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
		if (injector == null) {
			injector  = BPMServletContextListener.getInjectorForNonServlets();
		}
		this.pmsff = injector.getInstance(IPlayerMatchStatsFetcherFactory.class);
		this.pmsf = injector.getInstance(IPlayerMatchStatsFactory.class);
		this.atf = injector.getInstance(IAdminTaskFactory.class);

		if (player == null || match == null || player.getDisplayName() == null) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Bad name for player - bailing!!");
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
			}
			return immediate(stats);
		} else { // blocking process
		
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Problem getting player match stats for " + player.getDisplayName() + " in match " + match.getDisplayName() + " : " + fetcher.getErrorMessage());
			PromisedValue<IPlayerMatchStats> x = newPromise(IPlayerMatchStats.class);
			IAdminTask task = atf.getNewEditPlayerMatchStatsTask("Problem getting player match stats for " + player.getDisplayName() + " in match " + match.getDisplayName() + " in slot " + slot, fetcher.getErrorMessage(), player, match, hov, slot, fetcher.getStats(), true, getPipelineKey().getName(), getJobKey().getName(), x.getHandle());		
			atf.put(task);
			return x;
		}

	}

}
