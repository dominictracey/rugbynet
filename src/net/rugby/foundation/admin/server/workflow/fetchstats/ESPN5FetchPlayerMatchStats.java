package net.rugby.foundation.admin.server.workflow.fetchstats;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.client.place.AdminTaskPlace;
import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.admin.server.factory.IAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.IPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.model.IPlayerMatchStatsFetcher;
import net.rugby.foundation.admin.server.workflow.fetchstats.FetchMatchStats.Home_or_Visitor;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.ILineupSlotFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.ILineupSlot;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Job6;
import com.google.appengine.tools.pipeline.PromisedValue;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

public class ESPN5FetchPlayerMatchStats extends Job1<Long, Long> {
	private static Injector injector = null;
	private transient IPlayerMatchStatsFetcherFactory pmsff;
	private transient IPlayerMatchStatsFactory pmsf;
	private transient IAdminTaskFactory atf;
	private transient IPlayerMatchStatsFetcher fetcher;
	private transient IMatchGroupFactory mgf;
//	protected transient IPlayer player;
//	protected  transient IMatchGroup match;
//	protected transient Home_or_Visitor hov;
//	protected transient Integer slot;
//	protected transient String url;
	private transient IPlayerFactory pf;
	private IConfigurationFactory ccf;
	private ILineupSlotFactory lusf;
	
	public ESPN5FetchPlayerMatchStats() {

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3101992931956737933L;

	@Override
	public Value<Long> run(Long lusId) {


//		this.hov = hov;
//		this.slot = slot;
//		this.url = url;
		
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
		if (injector == null) {
			injector  = BPMServletContextListener.getInjectorForNonServlets();
		}
		this.pmsff = injector.getInstance(IPlayerMatchStatsFetcherFactory.class);
		this.pmsf = injector.getInstance(IPlayerMatchStatsFactory.class);
		this.atf = injector.getInstance(IAdminTaskFactory.class);
		this.mgf = injector.getInstance(IMatchGroupFactory.class);
		this.pf = injector.getInstance(IPlayerFactory.class);
		this.lusf = injector.getInstance(ILineupSlotFactory.class);
		
		ILineupSlot lus = lusf.get(lusId);
		
		if (lus == null || lus.getPlayerId() == null || lus.getMatchId() == null) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Bad LineupSlot info. Not enough info to fetch playerMatchStats - bailing!!");
			return null;
		}
		
		IPlayer player = pf.get(lus.getPlayerId());
		IMatchGroup match = mgf.get(lus.getMatchId());
		
		if (player == null || match == null || player.getDisplayName() == null) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Bad name for player - bailing!!");
//			match.setWorkflowStatus(WorkflowStatus.BLOCKED);
//			mgf.put(match);
			PromisedValue<Long> x = newPromise(Long.class);
			IAdminTask task = atf.getNewEditPlayerMatchStatsTask("Couldn't get sufficient info for player " + lus.getForeignPlayerName() + " in slot " + lus.getSlot() + " to collect match stats", "No player, match or player displayName", player, match, null, lus.getSlot(), null, true, getPipelineKey().getName(), getJobKey().getName(), x.getHandle());		
			atf.put(task);
			return x;
		}
		
		Home_or_Visitor hov = lus.getHome() ? Home_or_Visitor.HOME : Home_or_Visitor.VISITOR;
		
		fetcher = pmsff.getResultFetcher(player, match, hov, lus.getSlot(), null);
		
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
				IAdminTask task = atf.getNewEditPlayerMatchStatsTask("Non-blocking issue in getting player match stats for " + player.getDisplayName() + " in match " + match.getDisplayName(), errMess, player, match, hov, lus.getSlot(), null, true, null, null, null);		
				atf.put(task);
				stats.getTaskIds().add(task.getId());
				pmsf.put(stats);
//				if (!match.getWorkflowStatus().equals(WorkflowStatus.BLOCKED)) {
//					match.setWorkflowStatus(WorkflowStatus.TASKS_PENDING);
//					mgf.put(match);
//				}
			}
			return immediate(stats.getId());
		} else { // blocking process
			stats = fetcher.getStats();
			if (stats == null) {
				// at least we have the matchId and playerId?
				stats = pmsf.create();
				stats.setPlayerId(player.getId());
				stats.setName(lus.getForeignPlayerName());
				stats.setSlot(lus.getSlot());
				stats.setMatchId(match.getId());
				// @REX this field is not an object so it may be unset and we are introducing a bug by assuming it is set
				if (lus.getHome()) {
					stats.setTeamId(match.getHomeTeamId());
					stats.setTeamAbbr(match.getHomeTeam().getAbbr());
				} else {
					stats.setTeamId(match.getVisitingTeamId());
					stats.setTeamAbbr(match.getVisitingTeam().getAbbr());
				}
				
			}
			pmsf.put(stats);

			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Problem getting player match stats for " + player.getDisplayName() + " in match " + match.getDisplayName() + " : " + fetcher.getErrorMessage());
			PromisedValue<Long> x = newPromise(Long.class);
			IAdminTask task = atf.getNewEditPlayerMatchStatsTask("Problem getting player match stats for " + player.getDisplayName() + " in match " + match.getDisplayName() + " in slot " + lus.getSlot(), fetcher.getErrorMessage(), player, match, hov, lus.getSlot(), stats, true, getPipelineKey().getName(), getJobKey().getName(), x.getHandle());		
			atf.put(task);
			stats.getBlockingTaskIds().add(task.getId());
			pmsf.put(stats);

			// send an admin email
			this.ccf = injector.getInstance(IConfigurationFactory.class);
			AdminTaskPlace atp = new AdminTaskPlace();
			atp.setFilter("ALL");
			atp.setTaskId(task.getId().toString());
			String taskUrl = ccf.get().getBaseToptenUrl() + "Admin.html#AdminTaskPlace:" + atp.getToken();
			
			AdminEmailer emailer = new AdminEmailer();
			emailer.setSubject("TASK: Problem getting player match stats for " + player.getDisplayName() + " in match " + match.getDisplayName() + " in slot " + lus.getSlot());
			StringBuilder message = new StringBuilder();
			message.append("<h3>Workflow halted</h3>");
			message.append("<a href=\"" + taskUrl + "\" target=\"blank\">" + player.getDisplayName() + " in " + match.getDisplayName() + "</a><br/>");
			message.append("<a href=\"" + match.getForeignUrl() + " target=\"blank\">" + match.getDisplayName() + " on ESPN.</a><br/>");
			
			emailer.setMessage(message.toString());
			emailer.send();
			
			return x;
		}
	}

}
