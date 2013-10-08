package net.rugby.foundation.admin.server.workflow.matchrating;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.tools.pipeline.Job4;
import com.google.appengine.tools.pipeline.PromisedValue;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

import net.rugby.foundation.admin.server.factory.IAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.admin.server.factory.espnscrum.UrlCacher;
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;

public class FetchTeamMatchStats extends Job4<ITeamMatchStats, ITeamGroup, IMatchGroup, Home_or_Visitor, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3101992931956737933L;

	private class Triplet  {
		public String homeVal;
		public String visitVal;
		public String attr;	
	}

	private transient ITeamMatchStatsFactory tmsf;
	private transient IAdminTaskFactory atf;
	private transient String errMess;

	private transient ITeamGroup team;
	private transient IMatchGroup match;
	private transient Home_or_Visitor home_or_visitor;
	private transient String url;
	
	public FetchTeamMatchStats() {

	}




	@Override
	public Value<ITeamMatchStats> run(ITeamGroup team, IMatchGroup match, Home_or_Visitor home_or_visitor, String url) {

		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
		Injector injector = BPMServletContextListener.getInjectorForNonServlets();

		this.tmsf = injector.getInstance(ITeamMatchStatsFactory.class);
		this.atf = injector.getInstance(IAdminTaskFactory.class);

		boolean found = false;

		IUrlCacher urlCache = new UrlCacher(url);
		List<String> lines = urlCache.get();
		String line;
		ITeamMatchStats tms = tmsf.create();
		tms.setTeamId(team.getId());
		tms.setTeamAbbr(team.getAbbr());
		tms.setIsHome(home_or_visitor == Home_or_Visitor.HOME);
		
		tms.setMatchId(match.getId());

		try {
			if (lines == null) {
				errMess = "No lines read from foreign URL";
				return bail(tms,team,match);
			}

			Iterator<String> it = lines.iterator();
			while (it.hasNext() && !found) {

				line = it.next();
				// first we scan to the right date
				if (line.contains("<h2>Match stats")) {

					line = it.next(); // table
					//line = it.next(); // tbody

					Triplet trip = getTriplet(it);  //team names

					line = it.next(); // random blank line

					trip = getTriplet(it);
					if (trip.attr.equals("Tries")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							if (trip.homeVal.contains("(")) {  // e.g. 9 (1 penalty try)
								tms.setTries(Integer.parseInt(trip.homeVal.split("[ |(]")[0]));
							} else {
								tms.setTries(Integer.parseInt(trip.homeVal));
							}
						} else {
							if (trip.visitVal.contains("(")) {  // e.g. 9 (1 penalty try)
								tms.setTries(Integer.parseInt(trip.visitVal.split("[ |(]")[0]));
							} else {
								tms.setTries(Integer.parseInt(trip.visitVal));
							}
						}
					} else {
						errMess += ("Failed to find Tries");
						errMess +=  " Failed to find Tries";
						//
					}

					trip = getTriplet(it);
					if (trip.attr.equals("Conversion goals")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							String made = trip.homeVal.split("from")[0].trim();
							String attempts = trip.homeVal.split("from")[1].trim();
							tms.setConversionsMade(Integer.parseInt(made));
							tms.setConversionsAttempted(Integer.parseInt(attempts));
						} else {
							String made = trip.visitVal.split("from")[0].trim();
							String attempts = trip.visitVal.split("from")[1].trim();
							tms.setConversionsMade(Integer.parseInt(made));
							tms.setConversionsAttempted(Integer.parseInt(attempts));      			}
					} else {
						errMess += ("Failed to find Conversion goals");
						errMess +=  "Failed to find Conversion goals";
					}

					trip = getTriplet(it);
					if (trip.attr.equals("Penalty goals")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							String made = trip.homeVal.split("from")[0].trim();
							String attempts = trip.homeVal.split("from")[1].trim();
							tms.setPenaltiesMade(Integer.parseInt(made));
							tms.setPenaltiesAttempted(Integer.parseInt(attempts));
						} else {
							String made = trip.visitVal.split("from")[0].trim();
							String attempts = trip.visitVal.split("from")[1].trim();
							tms.setPenaltiesMade(Integer.parseInt(made));
							tms.setPenaltiesAttempted(Integer.parseInt(attempts));
						}
					} else {
						errMess += ("Failed to find Penalty goals");
						errMess +=  "Failed to find Penalty goals";
					}

					trip = getTriplet(it); // ignore kick at goal success

					// dropgoals
					trip = getTriplet(it);
					if (trip.attr.equals("Dropped goals")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							//1 (1 missed)
							tms.setDropGoalsMade(Integer.parseInt(trip.homeVal.split(" ")[0]));
							if (trip.homeVal.contains("(")) {
								tms.setDropGoalsAttempted(Integer.parseInt(trip.homeVal.split("[ |(]")[2]) + tms.getDropGoalsMade());

							} else {
								tms.setDropGoalsAttempted(tms.getDropGoalsMade());
							}
						} else {
							tms.setDropGoalsMade(Integer.parseInt(trip.homeVal.split(" ")[0]));  // handle: "0 (1 missed)"
							if (trip.visitVal.contains("(")) {
								tms.setDropGoalsAttempted(Integer.parseInt(trip.visitVal.split("[ |(]")[2]) + tms.getDropGoalsMade());

							} else {
								tms.setDropGoalsAttempted(tms.getDropGoalsMade());
							}
						}
					}


					// Kick/pass/run
					for (int i=0; i<7; ++i) {
						line = it.next();
					}

					trip = getTriplet(it);
					if (trip.attr.equals("Kicks from hand")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							tms.setKicksFromHand(Integer.parseInt(trip.homeVal));
						} else {
							tms.setKicksFromHand(Integer.parseInt(trip.visitVal));
						}
					} else {
						errMess += ("Failed to find Kicks from hand");
						
					}

					trip = getTriplet(it);
					if (trip.attr.equals("Passes")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							tms.setPasses(Integer.parseInt(trip.homeVal));
						} else {
							tms.setPasses(Integer.parseInt(trip.visitVal));
						}
					}  else {
						errMess += ("Failed to find Passes");
						
					}

					trip = getTriplet(it);
					if (trip.attr.equals("Runs")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							tms.setRuns(Integer.parseInt(trip.homeVal));
						} else {
							tms.setRuns(Integer.parseInt(trip.visitVal));
						}
					}  else {
						errMess += ("Failed to find Runs");
						
					}     		

					trip = getTriplet(it);
					if (trip.attr.equals("Metres run with ball")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							tms.setMetersRun(Integer.parseInt(trip.homeVal));
						} else {
							tms.setMetersRun(Integer.parseInt(trip.visitVal));
						}
					}   else {
						errMess += ("Failed to find Metres run with ball");
						
					} 

					// Attacking
					for (int i=0; i<7; ++i) {
						line = it.next();
					}

					trip = getTriplet(it);
					if (trip.attr.contains("Possession")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							String num = stripPercentSign(trip.homeVal);
							tms.setPossesion(Float.parseFloat(num));
						} else {
							String num = stripPercentSign(trip.visitVal);
							tms.setPossesion(Float.parseFloat(num));
						}
					}   else {
						errMess += ("Failed to find Possession");
						
					}

					// sometimes Territory doesn't appear
					boolean skipTerritory = false;
					trip = getTriplet(it);
					if (trip.attr.contains("Territory")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							String num = stripPercentSign(trip.homeVal);
							tms.setTerritory(Float.parseFloat(num));
						} else {
							String num = stripPercentSign(trip.visitVal);
							tms.setTerritory(Float.parseFloat(num));
						}
					}   else {
						errMess += ("Failed to find Territory");
						skipTerritory = true;
					}

					if (!skipTerritory)
						trip = getTriplet(it);

					if (trip.attr.equals("Clean breaks")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							tms.setCleanBreaks(Integer.parseInt(trip.homeVal));
						} else {
							tms.setCleanBreaks(Integer.parseInt(trip.visitVal));
						}
					}  else {
						errMess += ("Failed to find Clean breaks");
						
					}

					trip = getTriplet(it);
					if (trip.attr.equals("Defenders beaten")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							tms.setDefendersBeaten(Integer.parseInt(trip.homeVal));
						} else {
							tms.setDefendersBeaten(Integer.parseInt(trip.visitVal));
						}
					}  else {
						errMess += ("Failed to find Defenders beaten");
						
					}


					trip = getTriplet(it);
					if (trip.attr.equals("Offloads")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							tms.setOffloads(Integer.parseInt(trip.homeVal));
						} else {
							tms.setOffloads(Integer.parseInt(trip.visitVal));
						}
					}  else {
						errMess += ("Failed to find Offloads");
						
					}

					trip = getTriplet(it);
					if (trip.attr.equals("Rucks won")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							String made = trip.homeVal.split("[from|(]")[0].trim();
							String attempts = trip.homeVal.split("[from|(]")[4].trim();
							tms.setRucksWon(Integer.parseInt(made));
							tms.setRucks(Integer.parseInt(attempts));
						} else {
							String made = trip.visitVal.split("[from|(]")[0].trim();
							String attempts = trip.visitVal.split("[from|(]")[4].trim();
							tms.setRucksWon(Integer.parseInt(made));
							tms.setRucks(Integer.parseInt(attempts));
						}
					} else {
						errMess += ("Failed to find Rucks won");
						
					}

					line = it.next();
					trip = getTriplet(it);
					if (trip.attr.equals("Mauls won")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							String made = trip.homeVal.split("[from|(]")[0].trim();
							String attempts = trip.homeVal.split("[from|(]")[4].trim();
							tms.setMaulsWon(Integer.parseInt(made));
							tms.setMauls(Integer.parseInt(attempts));
						} else {
							String made = trip.visitVal.split("[from|(]")[0].trim();
							String attempts = trip.visitVal.split("[from|(]")[4].trim();
							tms.setMaulsWon(Integer.parseInt(made));
							tms.setMauls(Integer.parseInt(attempts));
						}
					} else {
						errMess += ("Failed to find Mauls won");
						
					}

					trip = getTriplet(it);
					if (trip.attr.equals("Turnovers conceded")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							tms.setTurnoversConceded(Integer.parseInt(trip.homeVal));
						} else {
							tms.setTurnoversConceded(Integer.parseInt(trip.visitVal));
						}
					}  else {
						errMess += ("Failed to find Turnovers conceded");
						
					}

					// Defensive
					for (int i=0; i<7; ++i) {
						line = it.next();
					}

					trip = getTriplet(it);
					if (trip.attr.equals("Tackles made/missed")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							String made = trip.homeVal.split("/")[0].trim();
							String missed = trip.homeVal.split("/")[1].trim();
							tms.setTacklesMade(Integer.parseInt(made));
							tms.setTacklesMissed(Integer.parseInt(missed));
						} else {
							String made = trip.visitVal.split("/")[0].trim();
							String missed = trip.visitVal.split("/")[1].trim();
							tms.setTacklesMade(Integer.parseInt(made));
							tms.setTacklesMissed(Integer.parseInt(missed));
						}
					} else {
						errMess += ("Failed to find Tackles made/missed");
						
					}

					// ignore Tackling success rate
					trip = getTriplet(it);

					// Set pieces
					for (int i=0; i<7; ++i) {
						line = it.next();
					}

					trip = getTriplet(it);
					if (trip.attr.equals("Scrums on own feed")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							String won = trip.homeVal.split(" ")[0].trim();
							String lost = trip.homeVal.split(" ")[2].trim();
							tms.setScrumsWonOnOwnPut(Integer.parseInt(won));
							tms.setScrumsPutIn(Integer.parseInt(lost)+Integer.parseInt(won));
						} else {
							String won = trip.visitVal.split(" ")[0].trim();
							String lost = trip.visitVal.split(" ")[2].trim();
							tms.setScrumsWonOnOwnPut(Integer.parseInt(won));
							tms.setScrumsPutIn(Integer.parseInt(lost)+Integer.parseInt(won));
						}
					} else {
						errMess += ("Failed to find Scrums on own feed");
						
					}

					trip = getTriplet(it);
					if (trip.attr.equals("Lineouts on own throw")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							String won = trip.homeVal.split(" ")[0].trim();
							String lost = trip.homeVal.split(" ")[2].trim();
							tms.setLineoutsWonOnOwnThrow(Integer.parseInt(won));
							tms.setLineoutsThrownIn(Integer.parseInt(lost)+Integer.parseInt(won));
						} else {
							String won = trip.visitVal.split(" ")[0].trim();
							String lost = trip.visitVal.split(" ")[2].trim();
							tms.setLineoutsWonOnOwnThrow(Integer.parseInt(won));
							tms.setLineoutsThrownIn(Integer.parseInt(lost)+Integer.parseInt(won));
						}
					} else {
						errMess += ("Failed to find Lineouts on own throw");
						
					}

					// Discipline
					for (int i=0; i<7; ++i) {
						line = it.next();
					}

					trip = getTriplet(it);
					if (trip.attr.equals("Penalties conceded")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							tms.setPenaltiesConceded(Integer.parseInt(trip.homeVal));
						} else {
							tms.setPenaltiesConceded(Integer.parseInt(trip.visitVal));
						}
					}  else {
						errMess += ("Failed to find Penalties conceded");
						
					}

					trip = getTriplet(it);
					if (trip.attr.equals("Yellow/red cards")) {
						if (home_or_visitor.equals(Home_or_Visitor.HOME)) {
							String yellow = trip.homeVal.split("/")[0].trim();
							String red = trip.homeVal.split("/")[1].trim();
							tms.setYellowCards(Integer.parseInt(yellow));
							tms.setRedCards(Integer.parseInt(red));
						} else {
							String yellow = trip.visitVal.split("/")[0].trim();
							String red = trip.visitVal.split("/")[1].trim();
							tms.setYellowCards(Integer.parseInt(yellow));
							tms.setRedCards(Integer.parseInt(red));        			}
						found = true;
					} else {
						errMess += ("Failed to find Yellow/red cards");
						
					}
				}
			}	
		} catch (Exception ex) {
			errMess += ex.getLocalizedMessage();
			return bail(tms,team,match);
		}
		if (found == true) {
			tmsf.put(tms);
			return immediate(tms);
		} else {
			return bail(tms,team,match);
		}
			
	}

	private String stripPercentSign(String homeVal) {
		return homeVal.split("%")[0].trim();
	}



	private Triplet getTriplet(Iterator<String> it) {
		Triplet triplet = new Triplet();
		String line = it.next(); // <tr>
		line = it.next();
		if (line.split("<|>").length == 1) {
			triplet.homeVal = line.split("<|>")[0].trim();
		} else if (line.split("<|>").length > 2)  {
			triplet.homeVal = line.split("<|>")[2].trim();
		} else { //over two lines
			line = it.next();
			triplet.homeVal = line.split("<|>")[0].trim();
		}
		while  (it.hasNext() && !line.contains("</td")) {
			line = it.next();
		}
		line = it.next();
		triplet.attr = line.split("<|>")[2].trim();
		line = it.next();
		if (line.split("<|>").length == 1) {
			triplet.visitVal = line.split("<|>")[0].trim();
		} else if (line.split("<|>").length > 2)  {
			triplet.visitVal = line.split("<|>")[2].trim();
		} else {
			line = it.next();
			triplet.visitVal = line.split("<|>")[0].trim();
		}
		while (it.hasNext() && !line.contains("</td")) {
			line = it.next();
		}
		line = it.next(); //</tr>
		return triplet;
	}

	Value<ITeamMatchStats> bail(ITeamMatchStats tms, ITeamGroup team, IMatchGroup match) {
		// save what we have so far
		tmsf.put(tms);

		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, errMess);
		PromisedValue<ITeamMatchStats> x = newPromise(ITeamMatchStats.class);
		Home_or_Visitor hov = Home_or_Visitor.HOME;
		if (!match.getHomeTeamId().equals(team.getId()))
			hov = Home_or_Visitor.VISITOR;
		IAdminTask task = atf.getNewEditTeamMatchStatsTask("Problem getting team match stats in match " + tms.getMatchId(), errMess, team, match, hov, tms, true, getPipelineKey().getName(), getJobKey().getName(), x.getHandle());		
		atf.put(task);
		return x;
	}
	
	public Value<ITeamMatchStats> handleFailure(Throwable e) {
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Exception thrown fetching team match stats: " + e.getLocalizedMessage());
		//still didn't find, need human to get this going.
		PromisedValue<ITeamMatchStats> x = newPromise(ITeamMatchStats.class);
		String teamName = "unknown";
		if (team != null) {
			teamName = team.getDisplayName();
		}
		IAdminTask task = atf.getNewEditPlayerTask("Something bad happened trying to find match stats for " + teamName, "Nothing saved for player", null, true, getPipelineKey().toString(), getJobKey().toString(), x.getHandle());
		atf.put(task);

		return x;
	}

}
