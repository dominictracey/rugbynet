package net.rugby.foundation.admin.server.workflow.fetchstats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.admin.server.factory.espnscrum.UrlCacher;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS7StatsFetched;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.ImmediateValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class FetchMatchStats extends Job1<MS7StatsFetched, Long> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	public enum Home_or_Visitor { HOME, VISITOR }

	private static Injector injector = null;
	transient private IPlayerFactory pf;
	transient private IMatchGroupFactory mgf;

	
	public FetchMatchStats() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}


	protected class NameAndId {
		String name;
		Long id;

		NameAndId(Long id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	/**
	 * return generator jobId
	 * params String compName
	 * 			Long scrumId
	 * 			Long adminID
	 */		
	@Override
	public Value<MS7StatsFetched> run(Long matchId) {

		if (injector == null) {
			injector = BPMServletContextListener.getInjectorForNonServlets();
		}

		this.pf = injector.getInstance(IPlayerFactory.class);
		this.mgf = injector.getInstance(IMatchGroupFactory.class);
		
		IMatchGroup match = mgf.get(matchId);
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

		String url = match.getForeignUrl(); //+"?view=scorecard";

		Logger.getLogger("FetchedPlayer").log(Level.INFO,"Starting generate match ratings for url " + url);

		FutureValue<Long> homeTeamStats = futureCall(new FetchTeamMatchStats(), immediate(matchId), immediate(Home_or_Visitor.HOME), immediate(url));
		FutureValue<Long> visitorTeamStats = futureCall(new FetchTeamMatchStats(), immediate(matchId), immediate(Home_or_Visitor.VISITOR), immediate(url));
		
		List<NameAndId> homeIds = getIds(Home_or_Visitor.HOME, url);
		List<NameAndId> visitIds = getIds(Home_or_Visitor.VISITOR, url);

		List<Value<Long>> homePlayers = new ArrayList<Value<Long>>();
		List<Value<Long>> visitorPlayers = new ArrayList<Value<Long>>();

		// job settings controlling retries
		JobSetting nowBackOffFactor = new JobSetting.BackoffFactor(2);
		JobSetting nowBackOffSeconds = new JobSetting.BackoffSeconds(10); // retry at 10, 200 and 4000 seconds?
		JobSetting nowMaxAttempts = new JobSetting.MaxAttempts(7); // 
					
		int count = 0;
		for (NameAndId info : homeIds) {
			// first see if we have it in the database
			IPlayer dbPlayer = pf.getByScrumId(info.id);

			// will return a "blank player" if it can't find it in the DB so check if the returned player has a scrum ID set.
			if (dbPlayer != null && dbPlayer.getScrumId() != null) {
				homePlayers.add(immediate(dbPlayer.getId()));
			} else {
				Value<Long> homePlayerId = futureCall(new FetchPlayerByScrumId(), immediate(info.name),  immediate(url), immediate(info.id), immediate(1L), immediate(match.getId()), nowBackOffFactor, nowBackOffSeconds, nowMaxAttempts);
				homePlayers.add(homePlayerId);
			}
		}

		count = 0;
		for (NameAndId info : visitIds) {
			// first see if we have it in the database
			IPlayer dbPlayer = pf.getByScrumId(info.id);

			// will return a "blank player" if it can't find it in the DB so check if the returned player has a scrum ID set.
			if (dbPlayer != null && dbPlayer.getScrumId() != null) {
				visitorPlayers.add(immediate(dbPlayer.getId()));
			} else {
				Value<Long> visitPlayerId = futureCall(new FetchPlayerByScrumId(), immediate(info.name),  immediate(url), immediate(info.id), immediate(1L), immediate(match.getId()), nowBackOffFactor, nowBackOffSeconds, nowMaxAttempts);
				visitorPlayers.add(visitPlayerId);
			}
		}	   

		List<Value<Long>> homePlayerMatchStats = new ArrayList<Value<Long>>();
		List<Value<Long>> visitorPlayerMatchStats = new ArrayList<Value<Long>>();

		count = 0;
		for (Value<Long> fp : homePlayers) {
			FutureValue<Long> stats = futureCall(new FetchPlayerMatchStats(), fp, immediate(match.getId()), immediate(Home_or_Visitor.HOME), immediate(count++), immediate(url), nowBackOffFactor, nowBackOffSeconds, nowMaxAttempts);
			
			homePlayerMatchStats.add(stats);

		}


		count = 0;
		for (Value<Long> fp : visitorPlayers) {
			FutureValue<Long> stats = futureCall(new FetchPlayerMatchStats(), fp, immediate(match.getId()), immediate(Home_or_Visitor.VISITOR), immediate(count++), immediate(url), nowBackOffFactor, nowBackOffSeconds, nowMaxAttempts);
			visitorPlayerMatchStats.add(stats);

		}
		//FutureList<IPlayerMatchStats> vpms = new FutureList<IPlayerMatchStats>(visitorPlayerMatchStats);

		// now we can invoke the engine
		//FutureValue<List<IPlayerMatchRating>> ratings = futureCall(new CreateMatchRatings(), immediate(match), hpms, vpms, homeTeamStats, visitorTeamStats);
		//Value<String> retVal = new ImmediateValue<String>(this.getPipelineKey().toString());
		
		return futureCall(new CompileMatchStats(), homeTeamStats, visitorTeamStats, futureList(homePlayerMatchStats), futureList(visitorPlayerMatchStats), new ImmediateValue<String>(this.getPipelineKey().toString()));

	}


	private List<NameAndId> getIds(Home_or_Visitor home, String url) {
		boolean found = false;
		boolean isVisitor = false;

		if (home == Home_or_Visitor.VISITOR) {
			isVisitor = true;
		}

		List<NameAndId> ids = new ArrayList<NameAndId>();
		IUrlCacher urlCache = new UrlCacher(url);
		List<String> lines = urlCache.get();
		String line;

		if (lines == null) {
			return null;
		}

		Iterator<String> it = lines.iterator();
		while (it.hasNext() && !found) {

			line = it.next();
			// first we scan to the right date
			if (line.contains("<h2>Teams")) {

				//skip down to players
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE,"Skipping down to teams...");

				// there can be a bunch of optional 10-line sections here for tries, cons, pens and drops
				// easiest to just look for the top of the player section
				while (it.hasNext() && !line.contains("<tr>")) {
					line = it.next();
				}

				if (!it.hasNext()) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Couldn't find top of the player section in teams tab when skipping through the summary headers (tries, cons, pens, drops)");
					return null;
				}

				// divTeams
				for (int i=0; i<8; ++i) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST,line);
					line = it.next(); 
				}

				//line = it.next(); // tbody

				// get 15 home starters
				for (int i=0; i<15; ++i) {
					NameAndId id = getId(it);
					if (!isVisitor)
						ids.add(id);
				}

				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE,"Skipping down to home subs...");
				// skip to subs
				for (int i=0; i<7; ++i) {  
					line = it.next();
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST,line);
				}

				// get 6 or 7 home subs
				NameAndId id = getId(it);
				while (id != null) {
					if (!isVisitor)
						ids.add(id);
					id = getId(it);
				}

				if (!isVisitor) {
					found = true;
				} else {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE,"Skipping down to end of home div...");
					//skip to end of home div
					//for (int i=0; i<2; ++i) {
					while (it.hasNext() && !line.contains("</td")) {
						line = it.next(); 
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST,line);
					}

					// sitting on home's closing </td>
					assert(line.contains("</td>"));
					//skip down to visiting team players
					//for (int i=0; i<8; ++i) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE,"Skipping down to visiting team...");
					while (it.hasNext() && !line.contains("</tr>")) {
						line = it.next(); 
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST,line);
					}

					// get 15 visiting team players
					for (int i=0; i<15; ++i) {
						id = getId(it);
						if (isVisitor && id != null)
							ids.add(id);
					}

					// skip to subs
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE,"Skipping down to visiting subs...");
					for (int i=0; i<7; ++i) {  
						line = it.next();
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST,line);
					}

					// get 7 visitor subs
					id = getId(it);
					while (id != null) {
						if (isVisitor)
							ids.add(id);
						id = getId(it);
					}

					found = true;

				}
			}
		}

		if (found) {
			return ids;
		} else {
			return null;
		}
	}

	/*
	 * sets playerOn to be true/false depending on how they finished the match
	 */
	NameAndId getId(Iterator<String> it) {

		String line = it.next();
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST,line);

		if (!line.contains("<tr")) {
			return null;  // hit the end of the substitutes
		}

		// there are 15 lines to a player section
		for (int i=0; i<6; ++i) {
			line = it.next();
		}

		//<a href="/scrum/rugby/player/14650.html" class="liveLineupTextblk" target="_top">Conrad Smith</a>
		String id = "";
		if (line.split("[/|.]").length > 4) {
			id = line.split("[/|.]")[4].trim();
		}

		String name = "unknown";
		if (line.split("[<|>]").length > 5) {
			name = line.split("[<|>]")[4];
		}

		//check for card
		line = it.next();  //</td>
		//line = it.next();

		if (line.contains("<td")) {
			//skip card
			for (int i=0; i<4; ++i) {
				line = it.next();
			}
		}

		// there may be another card!
		if (line.contains("<td")) {
			//skip card
			for (int i=0; i<4; ++i) {
				line = it.next();
			}
		}
		
		// just read innermost </tr>
		for (int i=0; i<3; ++i) {
			line = it.next();
		}

		// iterator on outer </tr>
		if (id == null || id.isEmpty()) {
			if (name == null || name.isEmpty()) {
				return null;
			} else {
				return new NameAndId(null,name);
			}
		} else {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINER,"Found player " + name + " (" + id + ")");
			return new NameAndId(Long.parseLong(id),name);
		}

	}
}
