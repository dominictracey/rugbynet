package net.rugby.foundation.admin.server.model;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.Competition;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.TeamGroup;


public class EspnCompetitionFetcher extends JsonFetcher implements IForeignCompetitionFetcher {

	private Map<String, ITeamGroup> teamMap = new HashMap<String, ITeamGroup>();
	//private Map<String, IMatchGroup> matchMap = new HashMap<String, IMatchGroup>();
	private List<IRound> roundMap = new ArrayList<IRound>();

	// http://www.espn.co.uk/rugby/fixtures/_/date/20160902/league/267979
	private String homePage;
	protected Long espnLeagueId;
	//private String resultType;
	private IRoundFactory rf;
	private IMatchGroupFactory mf;
	private IResultFetcherFactory srff;
	private ITeamGroupFactory tf;
	private IConfigurationFactory ccf;
	private String startDate;

	@SuppressWarnings("unused")
	private EspnCompetitionFetcher() {
		// use the quasi-injector
	}

	public EspnCompetitionFetcher(IRoundFactory rf, IMatchGroupFactory mf, IResultFetcherFactory srff, ITeamGroupFactory tf, IConfigurationFactory ccf) {
		this.rf = rf;
		this.mf = mf;
		this.srff = srff;
		this.tf = tf;
		this.ccf = ccf;
	}

	@Override
	public ICompetition getCompetition(String homePage, List<IRound> rounds, List<ITeamGroup> teams) {
		ICompetition comp = new Competition();
		comp.setTeams(teams);
		comp.setRounds(rounds);
		
		comp.setForeignURL(homePage);
		comp.setWeightingFactor(1f);
		if (espnLeagueId != null) {
			comp.setForeignID(espnLeagueId);
		} else {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Didn't have the ESPN league Id saved off");
		}

		comp.setTeams(teams);

		try {

			comp.setShortName("change me");
			comp.setLongName("change me");
			comp.setAbbr("CHG_ME");
			
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
		try {
			
			// the last token of the url is the league id
			String toks[] = homePage.split("/");
			espnLeagueId = Long.parseLong(toks[toks.length-1]);
			
			assert (espnLeagueId != null);

			url = new URL(ccf.get().getBaseNodeUrl() + "/v1/admin/scraper/league/" + espnLeagueId + "/teams");
			
			JSONArray retval = get();		
			
			for (int i=0; i<retval.length(); ++i) {
				ObjectMapper mapper = new ObjectMapper();
				ITeamGroup t = mapper.readValue(retval.getJSONObject(i).toString(), TeamGroup.class);
				ITeamGroup existing = tf.getTeamByScrumName(t.getScrumName());
				if (existing != null) {
					if (existing.getForeignId() == null || existing.getForeignId() == 0L) {
						// self-cleaning oven to add espnId
						existing.setForeignId(t.getForeignId());
						tf.put(existing);
					}
					t = existing;
				}
//				String name = retval.getJSONObject(i).getString("name");
//				ITeamGroup t = getTeam(name);
				teamMap.put(t.getDisplayName(), t);
			}
			
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getMessage());
			return null;
		} 

		return teamMap;
	}

	private ITeamGroup getTeam(String teamName) {
		ITeamGroup t = tf.getTeamByScrumName(teamName);
		if (t == null) {
			t = tf.create();
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
		String toks[] = homePage.split("/");
		espnLeagueId = Long.parseLong(toks[toks.length-1]);
		
		assert (espnLeagueId != null);
		
		
		// the date of the first match is in there
		startDate = toks[toks.length-3];
		assert startDate != null;
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
					IRound round = rf.create();
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
	public Map<String, IMatchGroup> getMatches(String urlDate, int offsetWeeks, Map<String, ITeamGroup> teams, Map<String, IMatchGroup> matches) {

		try {
			teamMap = teams;
			//http://www.espn.co.uk/rugby/fixtures/_/date/20160820/league/270559
			
			if (urlDate == null || urlDate.isEmpty()) {
				urlDate = startDate;
			}
			
			assert (espnLeagueId != null);
	
			DateTimeFormatter fmt = DateTimeFormat.forPattern("YYYYMMdd");
			DateTime dt = DateTime.parse(urlDate,fmt);
			dt = dt.plusWeeks(offsetWeeks);
			String dateString = fmt.print(dt);
			Map<String, IMatchGroup> tempMatchMap = getCompsForWeek(espnLeagueId, dateString);
			matches.putAll(tempMatchMap);
			return matches;
			
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getMessage());
			return null;
		} 
	}

	private Map<String, IMatchGroup> getCompsForWeek(Long espnLeagueId2, String dateString) {
		
		try {		
			Map<String, IMatchGroup> tempMatchMap = new HashMap<String, IMatchGroup>();
			url = new URL(ccf.get().getBaseNodeUrl() + "v0.9/admin/scraper/league/" + espnLeagueId + "/date/" + dateString + "/matches");
			
			JSONArray retval = get();			
			
			if (errorMessage != null) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, errorMessage);
				return null;
			} else {
				if (retval != null) {
					for (int i=0; i<retval.length(); ++i) {					
						IMatchGroup m = getMatch(retval.getJSONObject(i), espnLeagueId2);
						tempMatchMap.put(m.getForeignId().toString(), m);
						//matchMap.put(m.getForeignId().toString(), m);
					}
					return tempMatchMap;
				} else {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Items array from REST server is null...");
					return null;
				}
			}
			
			
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getMessage(),e);
			return null;
		} 
		
	}

	private IMatchGroup getMatch(JSONObject jsonObject, Long compForeignId) {
		IMatchGroup m = mf.getMatchByEspnId(Long.parseLong(jsonObject.getString("espnId")));
		if (m == null) {
			m = mf.create();
			m.setForeignId(Long.parseLong(jsonObject.getString("espnId")));
			ITeamGroup ht = teamMap.get(jsonObject.getString("homeTeam"));
			if (ht == null) {
				ht = getTeam(jsonObject.getString("homeTeam"));
				teamMap.put(ht.getDisplayName(),ht);
			}
			m.setHomeTeam(ht);
			ITeamGroup vt = teamMap.get(jsonObject.getString("visitingTeam"));
			if (vt == null) {
				vt = getTeam(jsonObject.getString("visitingTeam"));
				teamMap.put(vt.getDisplayName(),vt);
			}
			m.setVisitingTeam(vt);
			m.setDate(new org.joda.time.DateTime(jsonObject.getString("date")).toDate());
			m.setDisplayName(m.getHomeTeam().getDisplayName() + " vs. " + m.getVisitingTeam().getDisplayName());
			// http://www.espn.co.uk/rugby/match?gameId=290011&league=267979
			m.setForeignUrl("http://www.espn.co.uk/rugby/match?gameId=" + m.getForeignId() + "&league=" + compForeignId);
		}
		return m;
	}
//
//	private Date getDate(String day, String month, String year, String hour, String minute, String zone) {
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MMMM/dd hh:mm z");
//		String date = year+"/"+month+"/"+day+" "+hour+":"+minute+" "+zone;
//		java.util.Date utilDate = null;
//		try {
//			utilDate = formatter.parse(date);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		System.out.println("date:" + date);
//		System.out.println("utilDate:" + utilDate);
//
//		return utilDate;
//	}
//
//	private String getMatchMapKey(IMatchGroup m) {
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(m.getDate());
//		cal.set(Calendar.HOUR_OF_DAY, 0);
//		cal.set(Calendar.MINUTE, 0);
//		cal.set(Calendar.SECOND, 0);
//		cal.set(Calendar.MILLISECOND, 0);
//		return cal.getTime().toString() + "**" + m.getDisplayName();
//	}
//

	@Override
	public Map<String, IMatchGroup> getMatches(String url, Map<String, ITeamGroup> teams) {
		return null;

	}
	

}
