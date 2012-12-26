package net.rugby.foundation.admin.server.workflow.matchrating;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.tools.pipeline.Job5;
import com.google.appengine.tools.pipeline.Value;

import net.rugby.foundation.admin.server.UrlCacher;
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.Position;

public class FetchPlayerMatchStats extends Job5<IPlayerMatchStats, IPlayer, IMatchGroup, Home_or_Visitor, Integer, String> {

	private IPlayerMatchStatsFactory pmsf;

	int time = 0;

	public FetchPlayerMatchStats(IPlayerMatchStatsFactory pmsf) {
		this.pmsf = pmsf;

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

		IPlayerMatchStats stats = pmsf.getById(null);
		stats.setMatchId(match.getId()); // native ID, not scrum's
		stats.setPlayerId(player.getId());  // native ID, not scrum's
		if (hov == Home_or_Visitor.HOME) 
			stats.setTeamId(match.getHomeTeamId());
		else
			stats.setTeamId(match.getVisitingTeamId());		

		Boolean found = false;
		UrlCacher urlCache = new UrlCacher(url);
		List<String> lines = urlCache.get();
		String line;


		if (lines == null) {
			return null;
		}

		int countTabs = 0;
		Iterator<String> it = lines.iterator();

		// get the player stats
		found = false;

		while (it.hasNext() && !found) {

			line = getNext(it);
			// first we scan to the right tab 3rd tab is home, 4th visitor
			if (line.contains("tabbertab")) {
				++countTabs;
			}

			if ((countTabs == 7 && hov == Home_or_Visitor.HOME) || (countTabs == 8 && hov == Home_or_Visitor.VISITOR)) {
				line = getNext(it);
				line = getNext(it);
				//line = getNext();

				skipPlayer(it); // header row

				// starters will always be in their slot
				if (slot < 16) { 
					for (int i=0; i<slot; ++i) {
						skipPlayer(it); // 17 lines
					}
					// get the goods
					stats = getPlayerStats(it, stats);
				} else {
					// subs may be anywhere (or nowhere) in here since if they didn't run on they don't get a row here.
					boolean foundSub = false;
					
					//skip over starters
					for (int i=0; i<16; ++i) {
						skipPlayer(it); // 17 lines
					}
					
					while (!foundSub && stats != null) {
						stats = getPlayerStats(it, stats);
						// match short name
						if (stats != null && stats.getName().equals(player.getShortName())) {
							foundSub = true;
						}

						// look for their last name						
						if (stats != null && stats.getName().equals(player.getSurName())) { 
							foundSub = true;
						}
					}

					// They were a sub who did not get to run on.
					if (foundSub == false && stats == null) {
						stats = pmsf.getById(null);
						stats.setMatchId(match.getId()); // native ID, not scrum's
						stats.setPlayerId(player.getId());  // native ID, not scrum's
						if (hov == Home_or_Visitor.HOME) 
							stats.setTeamId(match.getHomeTeamId());
						else
							stats.setTeamId(match.getVisitingTeamId());		

						stats.setTimePlayed(0);
						pmsf.put(stats);
						return immediate(stats);
					}


				}

				if (slot < 15) {  // starters
					stats.playerOn(0);
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
			// first we scan to the third tab 
			if (line.contains("tabbertab")) {
				++countTabs;
			}

			if (countTabs == 3) { // Timeline

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
		} else
			return null;
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


			if (line.split("<|>").length > 2) {
				if (!line.split("<|>")[2].contains("+")) {
					time = Integer.parseInt(line.split("<|>")[2]);
				} else {
					time = Integer.parseInt(line.split("<|>")[2].split("[+]")[0]) + Integer.parseInt(line.split("<|>")[2].split("[+]")[1]);
				}
			}


			while (!line.contains(pms.getName()) && !line.contains("</tr>")) {
				line = getNext(it);
				if (line.contains(pms.getName())) {
					if (line.contains("sub")) {
						if (line.split("sub")[1].trim().contains("on")) {
							pms.playerOn(time);
						} else if (line.split("sub")[1].trim().contains("off")) {
							pms.playerOff(time);
						}
					}
					while (!line.contains("</tr>")) {
						line = getNext(it);
					}

				}

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

}
