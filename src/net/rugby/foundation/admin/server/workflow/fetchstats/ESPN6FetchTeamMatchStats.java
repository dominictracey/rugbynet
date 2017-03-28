package net.rugby.foundation.admin.server.workflow.fetchstats;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import net.rugby.foundation.admin.client.place.AdminTaskPlace;
import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.admin.server.factory.IAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.IPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.ITeamMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.model.IPlayerMatchStatsFetcher;
import net.rugby.foundation.admin.server.model.ITeamMatchStatsFetcher;
import net.rugby.foundation.admin.server.workflow.RetryRequestException;
import net.rugby.foundation.admin.server.workflow.fetchstats.FetchMatchStats.Home_or_Visitor;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ILineupSlot;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Job6;
import com.google.appengine.tools.pipeline.PromisedValue;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

public class ESPN6FetchTeamMatchStats extends Job3<Long, IMatchGroup, Home_or_Visitor, Long>  {
	private static Injector injector = null;

	private transient ITeamMatchStatsFactory tmsf;
	private transient IAdminTaskFactory atf;
	private transient ITeamMatchStatsFetcher fetcher;
	private transient IConfigurationFactory ccf;
	private transient ITeamMatchStatsFetcherFactory tmsff;
	private transient IRoundFactory rf;
	private transient ICompetitionFactory cf;


	public ESPN6FetchTeamMatchStats() {
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.INFO);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3101992931956737933L;

	/***
	 * 
	 * @param match
	 * @param hov
	 * @param blockingTMSid - as of 9/22/16 we were having concurrency problems of calling node-horseman multiple times. This parameter is ignored, but allows us to call in a serial manner.
	 * @return
	 */
	@Override
	public Value<Long> run(IMatchGroup match, Home_or_Visitor hov, Long blockingTMSid) throws RetryRequestException {

		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
		if (injector == null) {
			injector  = BPMServletContextListener.getInjectorForNonServlets();
		}

		this.atf = injector.getInstance(IAdminTaskFactory.class);
		this.tmsff = injector.getInstance(ITeamMatchStatsFetcherFactory.class);
		this.rf = injector.getInstance(IRoundFactory.class);
		this.cf = injector.getInstance(ICompetitionFactory.class);
		this.tmsf = injector.getInstance(ITeamMatchStatsFactory.class);

		if (match == null || match.getId() == null) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Bad Match. Not enough info to fetch TeamMatchStats - bailing!!");
			return null;
		} else {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, this.getJobDisplayName() + ": Fetching team match stats for " + match.getDisplayName() + " (" + hov.name() + ")");
		}

		IRound r = rf.get(match.getRoundId());
		ICompetition comp = cf.get(r.getCompId());
		ITeamGroup team  = hov == Home_or_Visitor.HOME ? match.getHomeTeam() : match.getVisitingTeam();

		fetcher = tmsff.get(comp.getCompType());

		// there are three states we can be in after we try to get this team's match stats
		//	1. Everything awesome - carry on
		//		- process() == true && errorMessage == null;
		//	2. Issues, but we can still let the workflow continue, just have someone go have a look 
		//		- process() == true && errorMessage != null
		//	3. We got some or no stats, and there is/are issues that someone needs to look at before workflow can continue
		//		- process() == false && errorMessage != null

		boolean success = fetcher.process(match, comp, hov == Home_or_Visitor.HOME);
		if (success) {
			ITeamMatchStats tms = fetcher.getStats();
			tmsf.put(tms);
			assert fetcher.getErrorMessage() == null;
			if (fetcher.getWarningMessages() != null && !fetcher.getWarningMessages().isEmpty()) {
				for (String mess : fetcher.getWarningMessages()) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Non-blocking issue in getting team match stats for " + match.getDisplayName() + " : " + mess);
					IAdminTask task = atf.getNewEditTeamMatchStatsTask("Issue fetching team match stats for " + team.getDisplayName() + " in match " + match.getDisplayName(), mess, team, match, hov, tms, true, null, null, null);		
					atf.put(task);
					tms.getTaskIds().add(task.getId());	
				}
			}
			tmsf.put(tms);
			return immediate(tms.getId());
		} else { // blocking process

			throw new RetryRequestException("No stats found for " + team.getDisplayName() + " at " + DateTime.now().toString());
			
//			// at least we have the matchId and teamId?
//			ITeamMatchStats stats = fetcher.getStats();
//			if (stats == null) {
//				throw new RetryRequestException("No stats found for " + team.getDisplayName() + " at " + DateTime.now().toString());
//			}
//			
//			if (fetcher.getErrorCode().equals("SCRAPER_MATCH_STATS_NOT_AVAILABLE")) {
//				throw new RetryRequestException("No stats found for " + team.getDisplayName() + " at " + DateTime.now().toString());
//			}
//
//			tmsf.put(stats);
//
//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Problem getting team match stats for " + team.getDisplayName() + " in match " + match.getDisplayName() + " : " + fetcher.getErrorMessage());
//			PromisedValue<Long> x = newPromise(Long.class);
//			IAdminTask task = atf.getNewEditTeamMatchStatsTask("Problem getting team match stats for " + team.getDisplayName() + " in match " + match.getDisplayName(), fetcher.getErrorCode() + ": " + fetcher.getErrorMessage(), team, match, hov, stats, true, getPipelineKey().getName(), getJobKey().getName(), x.getHandle());		
//			atf.put(task);
//			stats.getBlockingTaskIds().add(task.getId());
//			tmsf.put(stats);
//
//			// send an admin email
//			this.ccf = injector.getInstance(IConfigurationFactory.class);
//			AdminTaskPlace atp = new AdminTaskPlace();
//			atp.setFilter("ALL");
//			atp.setTaskId(task.getId().toString());
//			String taskUrl = ccf.get().getBaseToptenUrl() + "Admin.html#AdminTaskPlace:" + atp.getToken();
//
//			AdminEmailer emailer = new AdminEmailer();
//			emailer.setSubject("TASK: Problem getting team match stats for " + team.getDisplayName() + " in match " + match.getDisplayName());
//			StringBuilder message = new StringBuilder();
//			message.append("<h3>Workflow halted</h3>");
//			message.append(fetcher.getErrorCode() + ": " + fetcher.getErrorMessage()); 
//			message.append("<a href=\"" + taskUrl + "\" target=\"blank\">" + team.getDisplayName() + " in " + match.getDisplayName() + "</a><br/>");
//			message.append("<a href=\"" + match.getForeignUrl() + " target=\"blank\">" + match.getDisplayName() + " on ESPN.</a><br/>");
//
//			emailer.setMessage(message.toString());
//			emailer.send();
//
//			return x;
		}
	}


}
