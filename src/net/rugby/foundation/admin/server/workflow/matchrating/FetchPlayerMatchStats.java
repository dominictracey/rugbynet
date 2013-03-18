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
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.Position;

public class FetchPlayerMatchStats extends Job5<IPlayerMatchStats, IPlayer, IMatchGroup, Home_or_Visitor, Integer, String> {

	private transient IPlayerMatchStatsFactory pmsf;
	private transient IAdminTaskFactory atf;

	private transient IPlayer player;
	private transient IMatchGroup match;
	private transient Home_or_Visitor hov;
	private transient Integer slot;
	private transient String url;
	private transient IPlayerMatchStats stats;
	private transient int time = 0;

	private static Injector injector = null;

	public FetchPlayerMatchStats() {

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3101992931956737933L;

	@Override
	public Value<IPlayerMatchStats> run(IPlayer player, IMatchGroup match, Home_or_Visitor hov, Integer slot, String url) {
		assert(player != null);
		assert(match != null);
		assert(!url.isEmpty());

		this.player = player;
		this.match = match;
		this.hov = hov;
		this.slot = slot;
		this.url = url;

		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
		if (injector == null) {
			injector  = BPMServletContextListener.getInjectorForNonServlets();
		}
		this.pmsf = injector.getInstance(IPlayerMatchStatsFactory.class);
		this.atf = injector.getInstance(IAdminTaskFactory.class);



		stats = pmsf.getById(null);
		if (match != null) {
			stats.setMatchId(match.getId()); // native ID, not scrum's
			if (hov == Home_or_Visitor.HOME) 
				stats.setTeamId(match.getHomeTeamId());
			else
				stats.setTeamId(match.getVisitingTeamId());	
		}

		if (player != null) {
			stats.setPlayerId(player.getId());  // native ID, not scrum's
		}

		stats.setSlot(slot);

		if (player == null || match == null || player.getDisplayName() == null) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Bad name for player - bailing!!");
			PromisedValue<IPlayerMatchStats> x = newPromise(IPlayerMatchStats.class);
			IAdminTask task = atf.getNewEditPlayerMatchStatsTask("Couldn't get sufficient info for player in slot " + slot + " to collect match stats", "No player, match or player displayName", player, match, hov, slot, stats, true, getPipelineKey().getName(), getJobKey().getName(), x.getHandle());		
			atf.put(task);
			return x;
		}

		Boolean found = false;
		UrlCacher urlCache = new UrlCacher(url);
		List<String> lines = urlCache.get();
		String line;
		List<String> errorList = new ArrayList<String>();

		if (lines == null) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Could not open match report at " + url);
			PromisedValue<IPlayerMatchStats> x = newPromise(IPlayerMatchStats.class);
			IAdminTask task = atf.getNewEditPlayerMatchStatsTask("Couldn't get sufficient info for player in slot " + slot + " from the match report at " + url, "No lines returned", player, match, hov, slot, stats, true, getPipelineKey().getName(), getJobKey().getName(), x.getHandle());		
			atf.put(task);
			return x;
		}

		int countTabs = 0;
		boolean foundMatchTab = false;
		Iterator<String> it = lines.iterator();

		// get the player stats
		found = false;

		while (it.hasNext() && !found) {

			line = getNext(it);
			// first we scan to the match stats tab. Home is next after and visitor is after thtat
			if (line.contains("tabbertab")) {

				line = getNext(it);
				if (line.contains("Match stats"))
					foundMatchTab = true;
				else if (foundMatchTab) {
					countTabs++;
				}
			}

			if ((countTabs == 1 && hov == Home_or_Visitor.HOME) || (countTabs == 2 && hov == Home_or_Visitor.VISITOR)) {
				line = getNext(it);

				skipPlayer(it); // header row

				// starters will always be in their slot
				//if (slot < 15) { 
				//					for (int i=0; i<slot; ++i) {
				//						stats = getPlayerStats(it, stats);
				//						if (stats.getName().equals(player.getShortName()) || stats.getName().equals(player.getSurName())) {
				//							found = true; //somehow they ended up in an earlier slot?!?
				//						}
				//					}
				//
				//					if (!found) {
				//						// they should be here
				//						stats = getPlayerStats(it, stats);
				//					}

				// there is a weird case (http://www.espnscrum.com/scrum/rugby/current/match/166469.html?view=scorecard) 
				// where the starter just doesn't show up in the stats. See [12	C Hudson Tonga'uiha].
				// this not only hoses up that player, but everyone after him.
				stats = getPlayerStats(it, stats);
				if (!player.getSurName().equals(stats.getName()) && !player.getShortName().equals(stats.getName())) {
					// keep looking
					boolean stillLooking = true;
					while(stats != null && stillLooking) {
						stats = getPlayerStats(it,stats);
						if (stats != null && (player.getSurName().equals(stats.getName()) || player.getShortName().equals(stats.getName()))) {
							stillLooking = false;
							break;
						}
					}

					// if we didn't find it, assume they didn't get a run on and create an AdminTask if they were a starter
					if (stillLooking) {
						stats = noRunOn();
						if (slot > 14) {
							return immediate(stats);
						} else {
							Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Could not find match data row for " + player.getDisplayName());
							PromisedValue<IPlayerMatchStats> x = newPromise(IPlayerMatchStats.class);
							IAdminTask task = atf.getNewEditPlayerMatchStatsTask("Couldn't get sufficient info for player " + player.getDisplayName() + " in slot " + slot + " from the match report at " + url, "No line in match stats - created empty stats record for your review", player, match, hov, slot, stats, true, getPipelineKey().getName(), getJobKey().getName(), x.getHandle());		
							atf.put(task);
							return x;
						}
					}

				}
				//}
				//				} else {
				//					// subs may be anywhere (or nowhere) in here since if they didn't run on they don't get a row here.
				//					boolean foundSub = false;
				//
				//					//skip over starters - but check them
				//					for (int i=0; i<16; ++i) {
				//						stats = getPlayerStats(it, stats);
				//						if (stats.getName().equals(player.getShortName()) || stats.getName().equals(player.getSurName())) {
				//							foundSub = true; //somehow they ended up starting?!?
				//						}
				//					}
				//
				//					while (!foundSub && stats != null) {
				//						stats = getPlayerStats(it, stats);
				//						// match short name or last name
				//						if (stats != null && (stats.getName().equals(player.getShortName()) || stats.getName().equals(player.getSurName()))) {
				//							foundSub = true;
				//						}
				//					}
				//
				//					// They were a sub who did not get to run on.
				//					if (foundSub == false && stats == null) {
				//						stats = noRunOn();
				//						return immediate(stats);
				//					}
				//
				//				}

				if (slot < 15) {  // starters
					try {
						stats.playerOn(0);
					} catch (Exception e) {
						errorList.add(e.getLocalizedMessage());
					}
				}

				pmsf.put(stats);
				found = true;
			}

		}

		// now get the playing time

		countTabs = 0;
		it = lines.iterator();  //restart
		found = false;

		while (it.hasNext() && !found) {

			line = getNext(it);
			// first we scan to the Timeline tab 
			boolean foundTimelineTab = false;
			if (line.contains("tabbertab")) {
				line = getNext(it);
				if (line.contains("Timeline")) {
					foundTimelineTab = true;
				}
			}

			if (foundTimelineTab) { // Timeline

				// get past header row
				while(it.hasNext() && !line.contains("</tr>")) {
					line = it.next();
				}

				// now process row-by-row
				boolean done = false;
				while (!done) {
					done = ProcessTimelineRow(it, stats);
				}
				found = true;
			}
		}

		if (found = true) {
			pmsf.put(stats);
			return immediate(stats);
		} else {

			return null;
		}

	}

	private IPlayerMatchStats noRunOn() {
		stats = pmsf.getById(null);
		stats.setMatchId(match.getId()); // native ID, not scrum's
		stats.setPlayerId(player.getId());  // native ID, not scrum's
		stats.setSlot(slot);
		if (hov == Home_or_Visitor.HOME) 
			stats.setTeamId(match.getHomeTeamId());
		else
			stats.setTeamId(match.getVisitingTeamId());		
		stats.setName(player.getSurName());
		stats.setTimePlayed(0);
		pmsf.put(stats);
		return stats;
	}

	private IPlayerMatchStats getPlayerStats(Iterator<String> it, IPlayerMatchStats stats) {
		//now read the player
		String line = getNext(it);  //<tr>

		if (!line.contains("<tr")) 
			return null;

		line = getNext(it); //pos
		// check to see if we've gone through all the rows
		// if so, its because they were named to the match day 22 but didn't get on
		if (line.contains("spacer.gif")) {
			return null;
		}

		stats.setPosition(Position.getFromScrum(line.split("[<|>]")[2]));

		line = getNext(it); //name
		if (line.split("<|>").length < 3) {
			Logger.getLogger("FetchPlayerMatchStats").log(Level.SEVERE, "Bad name read for player "  + stats.getName() + ", line read is " + line);
			return null;
		} 

		String shortName = line.split("<|>")[2];
		stats.setName(shortName);




		line = getNext(it); //tries/assists

		stats.setTries(Integer.parseInt(line.split("<|/|>")[2]));
		stats.setTryAssists(Integer.parseInt(line.split("<|/|>")[3]));

		line = getNext(it); //points
		stats.setPoints(Integer.parseInt(line.split("<|/|>")[2]));

		line = getNext(it); //K/P/R
		stats.setKicks(Integer.parseInt(line.split("<|/|>")[2]));
		stats.setPasses(Integer.parseInt(line.split("<|/|>")[3]));
		stats.setRuns(Integer.parseInt(line.split("<|/|>")[4]));

		line = getNext(it); //meters run
		stats.setMetersRun(Integer.parseInt(line.split("<|/|>")[2]));       		

		line = getNext(it); //clean breaks
		stats.setCleanBreaks(Integer.parseInt(line.split("<|/|>")[2])); 

		line = getNext(it); 
		stats.setDefendersBeaten(Integer.parseInt(line.split("<|/|>")[2]));

		line = getNext(it);
		stats.setOffloads(Integer.parseInt(line.split("<|/|>")[2]));

		line = getNext(it);
		stats.setTurnovers(Integer.parseInt(line.split("<|/|>")[2]));

		line = getNext(it);  //<!--BP-->?

		line = getNext(it); //tackles/missed
		stats.setTacklesMade(Integer.parseInt(line.split("<|/|>")[2]));
		stats.setTacklesMissed(Integer.parseInt(line.split("<|/|>")[3]));

		line = getNext(it); //lineouts won/stolen
		stats.setLineoutsWonOnThrow(Integer.parseInt(line.split("<|/|>")[2]));
		stats.setLineoutsStolenOnOppThrow(Integer.parseInt(line.split("<|/|>")[3]));

		line = getNext(it);
		stats.setPenaltiesConceded(Integer.parseInt(line.split("<|/|>")[2]));

		line = getNext(it); 
		stats.setYellowCards(Integer.parseInt(line.split("<|/|>")[2]));
		stats.setRedCards(Integer.parseInt(line.split("<|/|>")[3]));

		line = getNext(it);

		return stats;
	}

	private boolean ProcessTimelineRow(Iterator<String> it, IPlayerMatchStats pms) {
		String line = getNext(it);

		if (line.contains("<tr class=\"liveTblRow"))  {
			// first col is time
			line = getNext(it);

			// ignore the header row, which has liveTblTextGrn in the first column
			if (line.contains("liveTblTextGrn")) {
				while (it.hasNext() && !line.contains("</tr")) {
					line = it.next();
				}

				if (it.hasNext())
					return false;
				else {
					// found end of file??
					return true;
				}
			}

			if (line.split("<|>").length > 2) {
				try {
					if (!line.split("<|>")[2].contains("+")) {
						time = Integer.parseInt(line.split("<|>")[2]);
					} else {
						time = Integer.parseInt(line.split("<|>")[2].split("[+]")[0]) + Integer.parseInt(line.split("<|>")[2].split("[+]")[1]);
					}
				} catch (NumberFormatException e) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Parsing timeline found bad time - thought there was a number on this line: " + line);
				}
			}


			// not enough to just test on "contains".  http://www.espnscrum.com/premiership-2012-13/rugby/match/166469.html
			// Leister is fielding Micky Young and Tom Youngs - gah!
			try {
				while (!line.contains(pms.getName()) && !line.contains("</tr>")) {
					line = getNext(it);
					if (line.contains(pms.getName())) {
						if (pms.getName().equals(line.split("-")[0].trim())) {
							if (line.contains("sub")) {
								if (line.split("sub")[1].trim().contains("on")) {
									pms.playerOn(time);
								} else if (line.split("sub")[1].trim().contains("off")) {
									pms.playerOff(time);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				if (pms != null) {
					createAdminTask(e.getLocalizedMessage());
				} 
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Parsing timeline issue: " + e.getMessage());

			}

			while (!line.contains("</tr>")) {
				line = getNext(it);
			}

			return false;
		} else {
			// we are done
			pms.matchOver(time);
			return true;
		}
	}

	private void skipPlayer(Iterator<String> it) {
		String line = getNext(it);
		assert(line.contains("<tr class=\"liveTblRow"));
		for (int i=0; i<16; ++i) {
			line = getNext(it);
		}
		assert(line.contains("</tr>"));
	}

	private String getNext(Iterator<String> it) {
		String line = it.next();  //<tr>
		while (line.isEmpty() && it.hasNext()) line = it.next();

		if (!it.hasNext())
			throw new RuntimeException("Found end of file unexpectedly");

		return line;
	}


	private IAdminTask createAdminTask(String details) {
		PromisedValue<IPlayerMatchStats> x = newPromise(IPlayerMatchStats.class);
		boolean sendEmail = true;
		IAdminTask task = atf.getNewEditPlayerMatchStatsTask("Fetching player match stats issue for player " + player.getDisplayName() + " in match " + match.getDisplayName(), details, player, match, hov, slot, stats, sendEmail, getJobKey().toString(), getPipelineKey().toString(), x.getHandle());

		return task;


	}

}
