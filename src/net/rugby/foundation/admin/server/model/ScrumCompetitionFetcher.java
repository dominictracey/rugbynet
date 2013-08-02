package net.rugby.foundation.admin.server.model;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.UrlCacher;
import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.Competition;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.MatchGroup;
import net.rugby.foundation.model.shared.TeamGroup;
import net.rugby.foundation.model.shared.IMatchResult.ResultType;


public class ScrumCompetitionFetcher implements IForeignCompetitionFetcher {

	private Map<String, ITeamGroup> teamMap = new HashMap<String, ITeamGroup>();
	private Map<String, IMatchGroup> matchMap = new HashMap<String, IMatchGroup>();
	private List<IRound> roundMap = new ArrayList<IRound>();

	private String homePage;
	//private String resultType;
	private IRoundFactory rf;
	private IMatchGroupFactory mf;
	private IResultFetcherFactory srff;
	private ITeamGroupFactory tf;

	@SuppressWarnings("unused")
	private ScrumCompetitionFetcher() {
		// use the quasi-injector
	}

	public ScrumCompetitionFetcher(IRoundFactory rf, IMatchGroupFactory mf, IResultFetcherFactory srff, ITeamGroupFactory tf) {
		this.rf = rf;
		this.mf = mf;
		this.srff = srff;
		this.tf = tf;
	}

	@Override
	public ICompetition getCompetition(String homePage, List<IRound> rounds, List<ITeamGroup> teams) {
		ICompetition comp = new Competition();
		comp.setForeignURL(homePage);
		if (homePage.split("[/|.]").length > 7) {
			int i = 0;
			boolean found = false;
			String parts[] = homePage.split("[/|.]");
			while (i < parts.length && found == false) {
				if (parts[i].equals("series"))
					found = true;
				++i;
			}
			if (found)
				comp.setForeignID(Long.parseLong(parts[i]));
			else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Couldn't get scrum id from " + homePage + " (couldn't find series token)");
			}
		} else {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Couldn't get scrum id from " + homePage + " (too short)");
		}

		comp.setTeams(teams);

		try {
			//            URL url = new URL(homePage);
			//            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			UrlCacher urlCache = new UrlCacher(homePage);
			List<String> lines = urlCache.get();
			String line = "";
			Iterator<String> it = lines.iterator();
			if (it.hasNext()) {
				line = it.next();
			}
			boolean longNameFound = false;
			boolean shortNameFound = false;

			while (it.hasNext()) {
				line = it.next();
				if( line.contains("scrumTitle") && longNameFound == false) {
					comp.setLongName(line.split("<|>")[2]);
					longNameFound = true;
				} else if ( line.contains("ScrumSectionHeader\">About") && !shortNameFound) {
					line = it.next();
					line = it.next();
					if (line.split("<|>").length > 1) {
						comp.setShortName(line.split("<|>")[1]);
						shortNameFound = true;
					}
				}
			}

			// set the begin and end dates
			DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
			Date first = null;
			Date last = null;
			try {
				first = format.parse("12/31/2500");
				last = format.parse("1/1/2001");
			} catch (ParseException e) {
				e.printStackTrace();
			}		

			for (IRound r: rounds) {
				if (r.getEnd() != null) {
					if (r.getEnd().after(last)) {
						last = r.getEnd();
					}
				} else {
					Logger.getLogger("Error").log(Level.SEVERE, "Round end not set for round #" + r.getAbbr());
				}
				if (r.getBegin() != null) {
					if (r.getBegin().before(first)) {
						first = r.getBegin();
					}		
				} else {
					Logger.getLogger("Error").log(Level.SEVERE, "Round begin not set for round #" + r.getAbbr());
				}
			}

			// so we need to get some results before these can be incremented
			comp.setPrevRoundIndex(-1);
			comp.setNextRoundIndex(0);

			comp.setBegin(first);
			comp.setEnd(last);

			Date today = new Date();
			if (last.after(today))
				comp.setUnderway(true);
			else
				comp.setUnderway(false);


		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getMessage());
			return null;
		} 

		return comp;
	}


	@Override
	public Map<String, ITeamGroup> getTeams() {
		//first get the teams
		String tableURL = homePage + "?template=pointstable";
		try {
			UrlCacher urlCache = new UrlCacher(tableURL);
			List<String> lines = urlCache.get();
			String line = "";
			Iterator<String> it = lines.iterator();
			if (it.hasNext()) {
				line = it.next();
			}

			boolean hasPosRow = false;
			while (it.hasNext()) {
				line = it.next();
				if( line.contains("pointsTblHdr")) {
					line = it.next(); 
					if (line.contains("Result Date"))  // more table stuff at bottom of page can be skipped.
						break;
					if (line.contains("Pos"))
						hasPosRow = true;  // have to read an extra line to get past the team
				}
				if( line.contains("pointsTblContWht")) {
					if (hasPosRow) {
						line = it.next();  // pos line
					}
					line = it.next();  // team line
					String teamName = line.split("<|>")[2];
					ITeamGroup t = getTeam(teamName);


					teamMap.put(teamName, t);
				}
			}

		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getMessage());
			return null;
		} 

		return teamMap;
	}

	private ITeamGroup getTeam(String teamName) {
		ITeamGroup t = tf.getTeamByName(teamName);
		if (t == null) {
			tf.setId(null);
			t = tf.getTeam();
			t.setDisplayName(teamName);


			// one name or two?
			if (teamName.split(" ").length > 1) {
				t.setAbbr(teamName.split(" ")[1]);
			} else {
				t.setAbbr(teamName);
			}
		}
		return t;
	}

	@Override
	public void setURL(String url) {
		homePage = url;

	}

	@Override
	public List<IRound> getRounds(String url,
			Map<String, IMatchGroup> matches) {

		// so a round goes Wednesday midnight GMT to the following Wednesday midnight...

		//first find first (before first match) and last Wednesday (after last match)
		DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		try {
			Date firstWednesday = format.parse("12/31/2500");		
			Date lastWednesday = format.parse("1/1/2001");
			for (IMatchGroup m: matches.values()) {
				if (m.getDate().after(lastWednesday)) {
					lastWednesday = m.getDate();
				}
				if (m.getDate().before(firstWednesday)) {
					firstWednesday = m.getDate();
				}				
			}
			Calendar one = new GregorianCalendar();
			one.setTime(firstWednesday);
			int dow = one.get(Calendar.DAY_OF_WEEK);
			// want to get to 4 (wednesday)
			if (dow >= 4)
				one.add(Calendar.DATE, -(dow-4));
			else
				one.add(Calendar.DATE, -(dow+3));
			one.set(Calendar.ZONE_OFFSET, 0);
			one.set(Calendar.HOUR, 0);
			firstWednesday = one.getTime();

			one.setTime(lastWednesday);
			one.set(Calendar.ZONE_OFFSET, 0);
			one.set(Calendar.HOUR, 0);
			one.set(Calendar.MINUTE, 1);
			dow = one.get(Calendar.DAY_OF_WEEK);
			if (dow > 4)
				one.add(Calendar.DATE, (11-dow));
			else
				one.add(Calendar.DATE, (4-dow));

			lastWednesday = one.getTime();


			Calendar counter = new GregorianCalendar();
			counter.setTime(firstWednesday);
			Calendar end = new GregorianCalendar();
			end.setTime(lastWednesday);
			List<List<IMatchGroup>> roundLists = new ArrayList<List<IMatchGroup>>();
			while (counter.before(end)) {
				roundLists.add(new ArrayList<IMatchGroup>());
				counter.add(Calendar.DATE, 7);
			}

			//now put the matches in the right buckets
			Calendar first = new GregorianCalendar();
			first.setTime(firstWednesday);
			for (IMatchGroup m: matches.values()) {
				Calendar cal = new GregorianCalendar();
				cal.setTime(m.getDate());
				Long timeoffset = cal.getTimeInMillis() - first.getTimeInMillis();
				Long weeks = timeoffset/1000*1/60*1/60*1/24*1/7;
				int index = weeks.intValue();
				int slot = 0;
				if (roundLists.get(index).size() == 0)
					roundLists.get(index).add(m);
				else {
					Calendar toInsert = new GregorianCalendar();
					toInsert.setTime(m.getDate());
					Calendar cursor = new GregorianCalendar();
					cursor.setTime(roundLists.get(index).get(slot).getDate());
					while (toInsert.after(cursor) && slot < roundLists.get(index).size()) {
						cursor.setTime(roundLists.get(index).get(slot).getDate());
						slot++;
					}
					roundLists.get(index).add(slot, m);
				}
			}

			// now create the rounds
			Integer e = 1;
			for (List<IMatchGroup> ral : roundLists) {
				if (ral.size() > 0) {
					rf.setId(null);
					IRound round = rf.getRound();
					round.setName("Round " + e);
					round.setOrdinal(e);
					round.setAbbr(e.toString());
					int num = 0;
					for (IMatchGroup mg : ral) {
						if (num == 0) {
							round.setBegin(mg.getDate());
						} 
						if (num == ral.size()-1) {
							round.setEnd(mg.getDate());
						}
						round.addMatch(mg);
						num++;
					}
					roundMap.add(round);
					e++;
				}
			}


		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}


		return roundMap;
	}

	@Override
	public Map<String, IMatchGroup> getMatches(String baseUrl,
			Map<String, ITeamGroup> teams) {

		try {
			IResultFetcher pastMatchFetcher = srff.getResultFetcher(null, null, ResultType.MATCHES);  //don't need any of the parameters
			matchMap = pastMatchFetcher.getMatches(baseUrl, teams);

			String tableURL = baseUrl + "?template=fixtures";
			String month = "";
			String year = "";
			String day = "";
			String hour = "";
			String minute = "";
			String zone = "";
			Long scrumId = 0L;


			UrlCacher urlCache = new UrlCacher(tableURL);
			List<String> lines = urlCache.get();
			String line = "";
			Iterator<String> it = lines.iterator();
			if (it.hasNext()) {
				line = it.next();
			}

			Boolean finished = false;
			Boolean hasLink = false;
			while (it.hasNext()) {
				line = it.next();
				if( line.contains("fixtureTblDateColHdr")) {
					//            		if (firstIn) { // have to skip some stuff
					//	            		for (int i=0; i<5;++i) {
					//	            			line = it.next();
					//	            		}
					//	            		firstIn=false;
					//            		}

					assert (line.contains("name"));
					String monthYear = line.split("<|>")[4];
					month = monthYear.split(" ")[0];
					year = monthYear.split(" ")[1];
				} else if (line.contains("fixtureTableDate")) {
					line = it.next();
					String dayDate = line.trim().split("<|>")[0];
					day = dayDate.trim().split(" ")[1];
					line = it.next();
					line = it.next();
					line = it.next();
					line = it.next(); //  Harlequins v Connacht, The Stoop, London
					// may be a link like <a class="fixtureTablePreview" href="/scrum/rugby/match/142516.html">Bath Rugby v Harlequins, Recreation Ground, Bath</a>
					String tmp = line;
					if (line.contains("fixtureTablePreview") && !line.contains("<!--")) {
						hasLink = true;
						tmp = line.split(">")[1];
					} else {
						hasLink = false;
						if (line.contains("<!--"))  {
							//read one more line and trim as above.
							line = it.next();
							tmp = line.split(">")[1];
							hasLink = true;
						}
					}

					// get the scrum match Id out
					String spl[] = line.split("[/|.]");
					int i=0;
					while (i < spl.length && !spl[i].equals("match")) {
						++i;
					}

					if (i+1<spl.length) {
						scrumId = Long.parseLong(spl[i+1]);
					}

					String homeName = "";
					String visitName = "";
					i = 0;
					String tok = tmp.trim().split(" ")[i++];
					while (!tok.equals("v")) {
						if (!homeName.equals(""))
							homeName += " ";
						homeName += tok;
						if (i < tmp.trim().split(" ").length)
							tok = tmp.trim().split(" ")[i++];
						else {
							finished = true;
							break;
						}

					}

					if (finished)
						break;
					tok = tmp.trim().split(" |,|<")[i++];

					while (!tok.equals("")) {
						if (!visitName.equals(""))
							visitName += " ";
						if (!tok.contains("br/>"))
							visitName += tok;
						else
							break;
						if (i < tmp.trim().split(" |,|<").length)
							tok = tmp.trim().split(" |,|<")[i++];
						else {
							break;
						}
					}           		

					visitName = visitName.trim();
					System.out.println("match:" + homeName + " v " + visitName);

					if (!teams.containsKey(homeName)) {
						// create teams on the fly if needed. We may not have gotten them from the ?template=pointstable
						teams.put(homeName,getTeam(homeName));
					}

					if (!teams.containsKey(visitName)) {
						// create teams on the fly if needed. We may not have gotten them from the ?template=pointstable
						teams.put(visitName,getTeam(visitName));
					}

					mf.setId(null);
					IMatchGroup match = mf.getGame();  // get empty one
					match.setHomeTeam(teams.get(homeName));
					//match.setHomeTeamId(teams.get(homeName).getId());
					match.setVisitingTeam(teams.get(visitName));
					//match.setVisitingTeamId(teams.get(visitName).getId());
					match.setForeignId(scrumId);
					if (match instanceof MatchGroup) {
						((MatchGroup)match).setDisplayName(teams.get(homeName), teams.get(visitName)); 
					}

					if (line.trim().split(">").length > 1) {
						String times = line.trim().split(">")[1].trim();
						if (hasLink) {
							times = line.trim().split(">")[3].trim();
						}
						String gmt = times.split(",")[1];
						hour = gmt.trim().split(":")[0];
						minute = gmt.trim().split(":| ")[1];
						zone = gmt.trim().split(":| ")[2];
					} else {
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "No time found for match " + match.getDisplayName());
					}
					
					match.setDate(getDate(day, month, year, hour, minute, zone));

					if (matchMap == null) {
						matchMap = new HashMap<String,IMatchGroup>();
					}

					// check to see if we already got it from the results page
					boolean alreadyThere = false;
					if (matchMap.containsKey(getMatchMapKey(match))) {
						// the times will be different so nix them before comparing
						Calendar cal = Calendar.getInstance();
						cal.setTime(match.getDate());
						cal.set(Calendar.HOUR_OF_DAY, 0);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
						cal.set(Calendar.MILLISECOND, 0);

						if (matchMap.get(getMatchMapKey(match)).getDate().equals(cal.getTime())) {
							alreadyThere = true;
						}
					}

					if (!alreadyThere) {
						matchMap.put(getMatchMapKey(match), match);
					}
				}
			}

		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getMessage());
			return null;
		} 

		return matchMap;
	}

	private Date getDate(String day, String month, String year, String hour, String minute, String zone) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MMMM/dd hh:mm z");
		String date = year+"/"+month+"/"+day+" "+hour+":"+minute+" "+zone;
		java.util.Date utilDate = null;
		try {
			utilDate = formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println("date:" + date);
		System.out.println("utilDate:" + utilDate);

		return utilDate;
	}

	private String getMatchMapKey(IMatchGroup m) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(m.getDate());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime().toString() + "**" + m.getDisplayName();
	}
}
