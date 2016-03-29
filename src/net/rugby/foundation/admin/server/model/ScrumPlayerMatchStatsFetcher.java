package net.rugby.foundation.admin.server.model;

//import java.io.FileNotFoundException;
//import java.io.PrintWriter;
//import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.admin.server.workflow.fetchstats.FetchMatchStats.Home_or_Visitor;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.Position;
import net.rugby.foundation.model.shared.Position.position;

public class ScrumPlayerMatchStatsFetcher implements IPlayerMatchStatsFetcher {
	private transient IPlayerMatchStatsFactory pmsf;

	private transient IPlayer player;
	private transient IMatchGroup match;
	private transient Home_or_Visitor hov;
	private transient Integer slot;
	private transient String url;
	private transient IPlayerMatchStats stats;
	private transient int time = 0;
	private transient Map<String, IPlayerMatchStats> playerMap = new HashMap<String, IPlayerMatchStats>();
	private String errorMessage;
	private List<String> header;

	private IUrlCacher urlCache;

	public ScrumPlayerMatchStatsFetcher(IPlayerMatchStatsFactory pmsf, IUrlCacher urlCache) {
		this.pmsf = pmsf;
		this.urlCache = urlCache;
	}

	private IPlayerMatchStats populateStats()  {
		assert(player != null);
		assert(match != null);
		assert(!url.isEmpty());

		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
		setErrorMessage(null);
		Boolean foundStats = false;
		urlCache.setUrl(url);
		List<String> lines = urlCache.get();
		String line;

		// fix the timeline before we get started processing the list	
		List<String> timeline = lines;
		if(isUpsideDown(timeline)) {
			timeline = extractTimelineTable(timeline);
			header = processHeader(timeline);
			timeline = flipList(timeline);
			timeline = flipRows(timeline);
			timeline = addHeader(timeline);
			//			writeListToFile(timeline, "WithHeader");
			lines = replaceTimeline(timeline, lines);
			//			writeListToFile(lines, "CompleteFile");
		}	

		if (lines == null) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Did not read any lines at " + url);
			setErrorMessage("Couldn't get sufficient info for player in slot " + slot + " from the match report at " + url + " : No lines returned from that url.");
			return null;
		}
		int countTabs = 0;
		boolean foundMatchTab = false;
		Iterator<String> it = lines.iterator();
		// get the player stats
		foundStats = false;
		// whether he is a reserve with his position listed as "R" - in which case we have to figure out who he came on for.
		boolean findPosition = false;
		while (it.hasNext() && !foundStats) {
			line = getNext(it);
			// first we scan to the match stats tab. Home is next after and visitor is after that
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
				// there is a weird case (http://www.espnscrum.com/scrum/rugby/current/match/166469.html?view=scorecard) 
				// where the starter just doesn't show up in the stats. See [12	C Hudson Tonga'uiha].
				// this not only hoses up that player, but everyone after him.
				//				IPlayerMatchStats cursor = pmsf.getById(null);
				//				cursor = getPlayerStats(it, cursor);
				//				playerMap.put(cursor.getName(),cursor);
				//				
				//				if (!player.getSurName().equals(cursor.getName()) && !player.getShortName().equals(cursor.getName())) {
				//					// keep looking
				boolean stillLooking = true;
				while(stillLooking) {
					IPlayerMatchStats cursor = pmsf.create();
					cursor = getPlayerStats(it,cursor);
					if (cursor != null) {
						playerMap.put(cursor.getName(),cursor);
						if (cursor != null && (player.getSurName().equals(cursor.getName()) || player.getShortName().equals(cursor.getName()))) {
							configureStats(cursor);
							// if we need to find a position for them, we need to get everyone in the playerMap
							if (!cursor.getPosition().equals(position.RESERVE) && !cursor.getPosition().equals(position.NONE)) {
								stillLooking = false;
								break;
							} else {
								findPosition = true;
							}
						}
					} else {
						break; // at the end of the line
					}
				}
				// if we didn't find it, assume they didn't get a run on and create an AdminTask if they were a starter
				if (stillLooking && !findPosition) {
					stats = noRunOn();
					if (slot > 14) {
						return stats;
					} else {
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Could not find match data row for " + player.getDisplayName());
						setErrorMessage("Couldn't get playing time info for player " + player.getDisplayName() + " in slot " + slot + " from the match report at " + url + " : No line in match stats - created empty stats record for your review");		
						return null;
					}
				}
				if (slot < 15) {  // starters
					try {
						stats.playerOn(0);
					} catch (Exception e) {
						setErrorMessage("Problem setting player " + player.getDisplayName() + " as a starter in match " + match.getDisplayName() + " playing time not set correctly: " + e.getLocalizedMessage());
						return stats;
					}
				}
				pmsf.put(stats);
				foundStats = true;
			}
		}
		// now get the playing time
		countTabs = 0;
		it = lines.iterator();  //restart
		boolean foundTime = false;
		while (it.hasNext() && !foundTime) {
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
					done = ProcessTimeline(it, stats);
				}
				foundTime = true;
			} 
		}
		if (!foundTime) {
			setErrorMessage("Couldn't find timeline tab while getting match stats for " + player.getDisplayName() + " in match " + match.getDisplayName());
		}
		if (foundStats)
			return stats;
		else
			return null;
	}

	private boolean isUpsideDown(List<String> eles) {
		if (eles != null) {
			Iterator<String> iter = eles.iterator();
			if (iter.hasNext()) {
				String line = iter.next();
				// first get here:
				//   <div class="tabbertab">
				//    <h2>Timeline</h2>
				while (iter.hasNext()) {
					if (line.contains("tabbertab"))
					{
						line = iter.next();
						if (line.contains("Timeline"))
						{
							break;
						}
					}
					line = iter.next();
				}

				String tdClass = "liveTblTextBlk";
				int timeEle = 0;
				while (iter.hasNext()) {
					line = iter.next();
					if (line != null && line.length() > -1) {
						if (line.contains(tdClass)) {
							if (!line.split("<|>")[2].contains("+")) {
								timeEle = Integer.parseInt(line.split("<|>")[2]);
							} else {
								timeEle = Integer.parseInt(line.split("<|>")[2].split("[+]")[0]) + Integer.parseInt(line.split("<|>")[2].split("[+]")[1]);
							}
							if (timeEle > 0) {
								return true;
							} else if (timeEle == 0) {
								return false;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private List<String> extractTimelineTable(List<String> toProcess) {
		List<String> subLines = new ArrayList<String>();
		int start = 0;
		int end = 0;
		for (int i = 0; i < toProcess.size(); i++) {
			String line = toProcess.get(i);
			if (line.contains("Timeline")) { // start
				start = i;
			} //start > 0 &&
			if ( start > 0 && line.contains("</table>")) {
				end = i + 1;
				break;
			}
		}
		for (int b = start; b < end; b++) {
			subLines.add(toProcess.get(b));
		}
		return subLines;
	}

	private List<String> flipList(List<String> lines) {
		ListIterator<String> iter = lines.listIterator(lines.size());
		List<String> newList = new ArrayList<String>();
		while(iter.hasPrevious()) {
			String line = iter.previous();
			newList.add(line);
		}
		return newList;
	}

	private List<String> flipRows(List<String> toProcess) {
		List<String> processed = new ArrayList<String>();
		List<String> flippedRow = new ArrayList<String>();
		List<String> goodList = new ArrayList<String>();
		int start = 0;
		int end = 0;
		for(int i = 0; i < toProcess.size(); i++) {
			String curLine = toProcess.get(i);
			if(curLine.contains("</tr>")) {
				start = i;
			}
			if(curLine.contains("<tr ")) {
				end = i + 1;
			} 
			if(start < end) {
				processed = toProcess.subList(start, end);

				ListIterator<String> tdIter = processed.listIterator(processed.size());
				while(tdIter.hasPrevious()) {
					flippedRow.add(tdIter.previous());
				}
				//				writeListToFile(flippedRow, "flipedRows" + i);
				goodList.addAll(flippedRow);
				flippedRow.clear();
			}
		}
		//		writeListToFile(goodList, "goodList");
		return goodList;
	}

	private List<String> processHeader(List<String> toProcess) {
		List<String> processed = new ArrayList<String>();
		for(int i = 0; i < 8; i++) {
			processed.add(toProcess.get(i));
		}
		return processed;
	}

	private List<String> addHeader(List<String> toProcess) {
		List<String> newList = new ArrayList<String>();
		newList.addAll(header);
		newList.addAll(toProcess);
		return newList;
	}

	private List<String> replaceTimeline(List<String> timeline, List<String> wholeList) {
		// find the timeline in the big list and replace it with the flipped list.
		List<String> modified = new ArrayList<String>();
		int start = 0;
		int end = 0;
		for (int i = 0; i < wholeList.size(); i++) {
			String line = wholeList.get(i);
			if (line.contains("Timeline")) { // start
				start = i + 1;
			}
			if (start > 0 && line.contains("</table>")) {
				end = i;
				break;
			}
		}
		for(int x = 0; x < wholeList.size(); x++) {
			if(x >= start && x < end) {
				wholeList.remove(x);
			} else {
				modified.add(wholeList.get(x));
			}
		}

		modified.addAll(start -1,  timeline);

		return modified;

	}

	//	private void writeListToFile(List<String> subLines, String filename) {
	//		if(subLines != null && subLines.size() > 0) {
	//			Iterator<String> iter = subLines.iterator();
	//			PrintWriter writer;
	//			try {
	//				writer = new PrintWriter("C:\\users\\seanm\\Development\\JAVA_SRC\\TEMP_FILES\\" + filename + ".html", "UTF-8");
	//				while(iter.hasNext()) {
	//					writer.println(iter.next());
	//				}
	//				writer.close();
	//			} catch (FileNotFoundException e) {
	//				e.printStackTrace();
	//			} catch (UnsupportedEncodingException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//	}

	private void configureStats(IPlayerMatchStats cursor) {
		stats = cursor;
		if (match != null) {
			stats.setMatchId(match.getId()); // native ID, not scrum's
			if (hov == Home_or_Visitor.HOME) {
				stats.setTeamId(match.getHomeTeamId());
				stats.setTeamAbbr(match.getHomeTeam().getAbbr());
			} else {
				stats.setTeamId(match.getVisitingTeamId());	
				stats.setTeamAbbr(match.getVisitingTeam().getAbbr());
			}
		}

		if (player != null) {
			stats.setPlayerId(player.getId());  // native ID, not scrum's
			stats.setCountryId(player.getCountryId());
		}

		stats.setSlot(slot);

	}

	private IPlayerMatchStats noRunOn() {
		stats = pmsf.create();
		stats.setMatchId(match.getId()); // native ID, not scrum's
		stats.setPlayerId(player.getId());  // native ID, not scrum's
		stats.setSlot(slot);
		if (hov == Home_or_Visitor.HOME)  {
			stats.setTeamId(match.getHomeTeamId());
			stats.setTeamAbbr(match.getHomeTeam().getAbbr());
		} else {
			stats.setTeamId(match.getVisitingTeamId());	
			stats.setTeamAbbr(match.getVisitingTeam().getAbbr());
		}
		stats.setName(player.getSurName());
		stats.setCountryId(player.getCountryId());
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

	private boolean ProcessTimeline(Iterator<String> it, IPlayerMatchStats pms) {
		// home team is in left column, visitors in right
		String us = "liveTblColLeft";
		String them = "liveTblColRgt";
		boolean heIsOn = false;
		if (hov.equals(Home_or_Visitor.VISITOR)) {
			us = "liveTblColRgt";
			them = "liveTblColLeft";
		}

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
			//
			// So we need to cycle through all of the "on" and "off" entries and keep them in a block so if we have a reserve come on who doesn't have
			// his position set (he is Postition.RESERVE), we can figure out what his position is based on the best matching candidate coming off.
			try {
				//				while (!line.contains(pms.getName()) && !line.contains("</tr>")) {
				//					line = getNext(it);
				//					if (line.contains(pms.getName())) {
				//						if (line.split("[<|>|-]")[0].trim().equals(pms.getName()) ||line.split("[<|>|-]")[1].trim().equals(pms.getName()) || line.split("[<|>|-]")[2].trim().equals(pms.getName())) {
				//							if (line.contains("sub")) {
				//								if (line.split("sub")[1].trim().contains("on")) {
				//									pms.playerOn(time);
				//								} else if (line.split("sub")[1].trim().contains("off")) {
				//									pms.playerOff(time);
				//								}
				//							}
				//						}
				//					}
				//				}


				// if we have a block, organize them so we know who is coming off.
				List<String> offList = null;

				boolean findPosition = false;
				if (pms.getPosition().equals(position.NONE) || pms.getPosition().equals(position.RESERVE)) {
					findPosition = true;
					offList = new ArrayList<String>();
				}

				boolean look = false;

				while (!line.contains("</tr>")) {
					line = getNext(it);

					if (line.contains(us)) {
						look = true;
					} else if (line.contains(them)) {
						look = false;
					}

					if (look) {
						String name = findName(line); 

						if (name != null && findPosition) {
							if (line.contains("sub")) {
								if (line.split("sub")[1].trim().contains("off")) {
									offList.add(name);
								}
							}
						}

						// so if this contains the guy we are looking for, mark them as on or off.
						if (name != null && name.equals(pms.getName())) {
							if (line.contains("sub")) {
								if (line.split("sub")[1].trim().contains("on")) {
									pms.playerOn(time);
									heIsOn = true;
								} else if (line.split("sub")[1].trim().contains("off")) {
									pms.playerOff(time);
								}
							}
						}
					}
				}

				//	Additionally, if they have a position of Reserve or None, figure out who they are replacing
				// 	so we can update their position accordingly.
				if (findPosition && heIsOn) {
					// if there is only one person off, that's the one.
					if (offList.size() == 1) {
						if (playerMap.containsKey(offList.get(0))) {
							pms.setPosition(playerMap.get(offList.get(0)).getPosition());
						}
					} else if (offList.size() > 1) {
						// otherwise, try to pick the right one.
						position pos = getSwappedPosition(pms, offList);
						if (pos != null && pos != position.NONE && pos != position.RESERVE) {
							pms.setPosition(pos);
						} else {
							throw new RuntimeException("Could not match the reserve player coming on to anyone coming off so we could determine what his position was.");
						}
					} else {
						throw new RuntimeException("offList not set up properly - must have one or more people coming off as our guy comes on.");
					}
				}

			} catch (Exception e) {
				if (pms != null) {
					setErrorMessage("Problem getting playing time information for player " + player.getDisplayName() + " in match " + match.getDisplayName() + " (" + match.getDate() + ") : " + e.getLocalizedMessage());
				} 
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Parsing timeline issue: " + e.getMessage(), e);

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

	private String findName(String line) {
		// it can be in a few different places in the strtok
		String[] split = line.split("<|>| - ");
		if (split.length > 0 && playerMap.containsKey(split[0].trim())) {
			return split[0].trim();
		} else if (split.length > 1 && playerMap.containsKey(split[1].trim())) {
			return split[1].trim();
		} if (split.length > 2 && playerMap.containsKey(split[2].trim())) {
			return split[2].trim();
		}
		return null;
	}


	/**
	 * @param pms - Reserve player we are trying to determine a position for
	 * @param offList - The players who came off the field as he went on
	 * @return - The "best fit" position, meaning each slot has a "first choice" (jersey 16 => hooker, jerseys 17,18 => prop, etc). If there wasn't one of them we go through the second choices for that jersey. If we can't find anything we return position.NONE
	 */
	private position getSwappedPosition(IPlayerMatchStats pms, List<String> offList) {
		Iterator<String> it = offList.iterator();
		boolean matched = false;

		position pos = position.NONE;

		if (pms.getSlot() == 15) { // 15 is jersey 16 since we started at 0
			// look for a hooker
			while (it.hasNext() && !matched) {
				String candidate = it.next();
				if (playerMap.containsKey(candidate)) {
					if (playerMap.get(candidate).getPosition().equals(position.HOOKER)) {
						pos = position.HOOKER;
						matched = true;
					} else if (playerMap.get(candidate).getPosition().equals(position.PROP)) {
						pos = position.PROP;
					}
				}
			}
		} else if (pms.getSlot() == 16) {
			// look for a prop
			while (it.hasNext() && !matched) {
				String candidate = it.next();
				if (playerMap.containsKey(candidate)) {
					if (playerMap.get(candidate).getPosition().equals(position.PROP)) {
						pos = position.PROP;
						matched = true;
					}
				}
			}
		} else if (pms.getSlot() == 17) {
			// look for a prop
			while (it.hasNext() && !matched) {
				String candidate = it.next();
				if (playerMap.containsKey(candidate)) {
					if (playerMap.get(candidate).getPosition().equals(position.PROP)) {
						pos = position.PROP;
						matched = true;
					}if (playerMap.get(candidate).getPosition().equals(position.LOCK)) {
						pos = position.LOCK;
					}
				}
			}
		} else if (pms.getSlot() == 18) {
			// look for a lock
			while (it.hasNext() && !matched) {
				String candidate = it.next();
				if (playerMap.containsKey(candidate)) {
					if (playerMap.get(candidate).getPosition().equals(position.LOCK)) {
						pos = position.LOCK;
						matched = true;
					} else if (playerMap.get(candidate).getPosition().equals(position.NUMBER8)) {
						pos = position.NUMBER8;
					} else if (playerMap.get(candidate).getPosition().equals(position.FLANKER)) {
						pos = position.FLANKER;
					}
				}
			}
		} else if (pms.getSlot() == 19) {
			// look for a backrow
			while (it.hasNext() && !matched) {
				String candidate = it.next();
				if (playerMap.containsKey(candidate)) {
					if (playerMap.get(candidate).getPosition().equals(position.FLANKER)) {
						pos = position.FLANKER;
						matched = true;
					} else if (playerMap.get(candidate).getPosition().equals(position.NUMBER8)) {
						pos = position.NUMBER8;
					} else if (playerMap.get(candidate).getPosition().equals(position.LOCK)) {
						pos = position.LOCK;
					}  else if (playerMap.get(candidate).getPosition().equals(position.SCRUMHALF)) {
						pos = position.SCRUMHALF;
					}
				}
			}
		} else if (pms.getSlot() == 20) {
			// look for a SH
			while (it.hasNext() && !matched) {
				String candidate = it.next();
				if (playerMap.containsKey(candidate)) {
					if (playerMap.get(candidate).getPosition().equals(position.SCRUMHALF)) {
						pos = position.SCRUMHALF;
						matched = true;
					} else if (playerMap.get(candidate).getPosition().equals(position.FLYHALF)) {
						pos = position.FLYHALF;
					} else if (playerMap.get(candidate).getPosition().equals(position.CENTER)) {
						pos = position.CENTER;
					} else if (playerMap.get(candidate).getPosition().equals(position.SCRUMHALF)) {
						pos = position.SCRUMHALF;
					}
				}
			}
		}  else if (pms.getSlot() == 21) {
			// look for a FH
			while (it.hasNext() && !matched) {
				String candidate = it.next();
				if (playerMap.containsKey(candidate)) {
					if (playerMap.get(candidate).getPosition().equals(position.FLYHALF)) {
						pos = position.FLYHALF;
						matched = true;
					} else if (playerMap.get(candidate).getPosition().equals(position.CENTER)) {
						pos = position.CENTER;
					} else if (playerMap.get(candidate).getPosition().equals(position.WING)) {
						pos = position.WING;
					} else if (playerMap.get(candidate).getPosition().equals(position.FULLBACK)) {
						pos = position.FULLBACK;
					}
				}
			}
		}  else if (pms.getSlot() == 22) {
			// look for a Wing or FB
			while (it.hasNext() && !matched) {
				String candidate = it.next();
				if (playerMap.containsKey(candidate)) {
					if (playerMap.get(candidate).getPosition().equals(position.WING)) {
						pos = position.WING;
						matched = true;
					} else if (playerMap.get(candidate).getPosition().equals(position.FULLBACK)) {
						pos = position.FULLBACK;
					} else if (playerMap.get(candidate).getPosition().equals(position.CENTER)) {
						pos = position.CENTER;
					} else if (playerMap.get(candidate).getPosition().equals(position.FLYHALF)) {
						pos = position.FLYHALF;
					}  else if (playerMap.get(candidate).getPosition().equals(position.PROP)) {  // bloody french
						pos = position.PROP;
					}
				}
			}
		}

		return pos;
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
	@Override
	public IPlayer getPlayer() {
		return player;
	}
	@Override
	public void setPlayer(IPlayer player) {
		this.player = player;
	}
	@Override
	public IMatchGroup getMatch() {
		return match;
	}
	@Override
	public void setMatch(IMatchGroup match) {
		this.match = match;
	}
	@Override
	public Home_or_Visitor getHov() {
		return hov;
	}
	@Override
	public void setHov(Home_or_Visitor hov) {
		this.hov = hov;
	}
	@Override
	public Integer getSlot() {
		return slot;
	}
	@Override
	public void setSlot(Integer slot) {
		this.slot = slot;
	}
	@Override
	public String getUrl() {
		return url;
	}
	@Override
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public void setUrl(String url, Boolean flushFromCache) {
		this.url = url;
		
		if (flushFromCache) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "The UrlCacher actually flushes automatically every 5 minutes so this doesn't actually work. " + url);
		}
		// we now have all IUrlCached objects expire after 5 minutes
//		if (urlCache != null) {
//			urlCache.clear(url);
//		}
	}
	@Override
	public boolean process() {
		if (stats == null) {
			populateStats();
		}

		return (errorMessage == null);
	}
	@Override
	public void set(IPlayerMatchStats stats) {
		this.stats = stats;
	}
	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	@Override
	public IPlayerMatchStats getStats() {
		return stats;
	}

	@Override
	public Boolean hasFlopped() {
		urlCache.setUrl(url);
		List<String> lines = urlCache.get();

		List<String> timeline = lines;

		boolean flopped = !isUpsideDown(timeline);

		boolean present = false;
		Iterator<String> it = lines.iterator();
		String line = it.next();
		int countTabs = 0;
		while (it.hasNext() && !present && countTabs < 5) {
			line = getNext(it);
			// 
			if (line.contains("tabbertab")) {
				line = getNext(it);
				if (line.contains("Match stats")) {
					present = true;
				}
				countTabs++;
			}
		}

		// also include a check as to whether the stats are actually there.
		// if the second tab says "Other Scores", they're not.

		return flopped && present;
	}
}

