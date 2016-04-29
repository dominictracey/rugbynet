package net.rugby.foundation.admin.server.model;

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

import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.Competition;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.MatchGroup;
//import net.rugby.foundation.model.shared.ISimpleScoreMatchResult;

public class ScrumInternationalCompetitionFetcher implements IForeignCompetitionFetcher {

    private Map<String, ITeamGroup> teamMap = new HashMap<String, ITeamGroup>();
    private Map<String, IMatchGroup> matchMap = new HashMap<String, IMatchGroup>();
    private List<IRound> roundMap = new ArrayList<IRound>();

    private String homePage;
    // private String resultType;
    private IRoundFactory roundFactory;
    private IMatchGroupFactory matchFactory;
    private IResultFetcherFactory resultFetcherFactory;
    private ITeamGroupFactory teamGroupFactory;
    private IUrlCacher urlCacher;
	private ICompetitionFactory cf;;

    public ScrumInternationalCompetitionFetcher(IRoundFactory rf, IMatchGroupFactory mf, IResultFetcherFactory rff, ITeamGroupFactory tf, IUrlCacher uc) {
        this.roundFactory = rf;
        this.matchFactory = mf;
        this.resultFetcherFactory = rff;
        this.teamGroupFactory = tf;
        this.urlCacher = uc;
    }

    @Override
    public ICompetition getCompetition(String homePage, List<IRound> rounds, List<ITeamGroup> teams) {
        ICompetition comp = new Competition();

        for (IRound r : rounds) {
        	comp.getRounds().add(r);
//        	comp.getRoundIds().add(r.getId());
        }
        
        for (ITeamGroup t : teams) {
        	comp.getTeams().add(t);
//        	comp.getTeamIds().add(t.getId());
        }
        
        comp.setBegin(rounds.get(0).getBegin());
        comp.setEnd(rounds.get(rounds.size()-1).getEnd());
        
        comp.setLastSaved(new Date());
        comp.setUnderway(false);
        comp.setNextRoundIndex(0);
        comp.setPrevRoundIndex(-1);
        
        comp.setForeignID(194567L);
        comp.setForeignURL("http://www.espnscrum.com/scrum/rugby/page/194567.html");
        comp.setLongName("New Competition");

        return comp;
    }

    @Override
    public Map<String, ITeamGroup> getTeams() {
        // first get the teams
        String tableURL = homePage; 
        try {
           //  IUrlCacher urlCache = new UrlCacher(tableURL);
            urlCacher.setUrl(tableURL);
            List<String> lines = urlCacher.get();
            
            

            String line = "";

            for (int i = 0; i < lines.size() - 1; i++) {
                line = lines.get(i);
                if (line.contains("fixtureTableDate")) {
                    String htLine = lines.get(i + 2);
                    String vLine = lines.get(i + 4);
                    String hTeam = htLine.split("<|>")[2];
                    String vTeam = vLine.split("<|>")[2];
                    ITeamGroup home = getTeam(hTeam);
                    ITeamGroup visitor = getTeam(vTeam);
                    teamMap.put(hTeam, home);
                    teamMap.put(vTeam, visitor);
                }
            }
        } catch (Throwable e) {
            Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
        return teamMap;
    }

    @Override
    public List<IRound> getRounds(String url, Map<String, IMatchGroup> matches) {
        // so a round goes Wednesday midnight GMT to the following Wednesday
        // midnight...

        // first find first (before first match) and last Wednesday (after last
        // match)
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date firstWednesday = format.parse("12/31/2500");
            Date lastWednesday = format.parse("1/1/2001");
            for (IMatchGroup m : matches.values()) {
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
                one.add(Calendar.DATE, -(dow - 4));
            else
                one.add(Calendar.DATE, -(dow + 3));
            one.set(Calendar.ZONE_OFFSET, 0);
            one.set(Calendar.HOUR, 0);
            firstWednesday = one.getTime();

            one.setTime(lastWednesday);
            one.set(Calendar.ZONE_OFFSET, 0);
            one.set(Calendar.HOUR, 0);
            one.set(Calendar.MINUTE, 1);
            dow = one.get(Calendar.DAY_OF_WEEK);
            if (dow > 4)
                one.add(Calendar.DATE, (11 - dow));
            else
                one.add(Calendar.DATE, (4 - dow));

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

            // now put the matches in the right buckets
            Calendar first = new GregorianCalendar();
            first.setTime(firstWednesday);
            for (IMatchGroup m : matches.values()) {
                Calendar cal = new GregorianCalendar();
                cal.setTime(m.getDate());
                Long timeoffset = cal.getTimeInMillis() - first.getTimeInMillis();
                Long weeks = timeoffset / 1000 * 1 / 60 * 1 / 60 * 1 / 24 * 1 / 7;
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
                    IRound round = roundFactory.create();
                    round.setName("Round " + e);
                    round.setOrdinal(e);
                    round.setAbbr(e.toString());
                    int num = 0;
                    for (IMatchGroup mg : ral) {
                        if (num == 0) {
                            round.setBegin(mg.getDate());
                        }
                        if (num == ral.size() - 1) {
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
    public Map<String, IMatchGroup> getMatches(String url, Map<String, ITeamGroup> teams) {
        try {

            SimpleDateFormat fmatter = new SimpleDateFormat("yyyy-MMMM");
            String yearMonth = fmatter.format(new java.util.Date());
            
            String month = yearMonth.split("-")[1];
            String year = yearMonth.split("-")[0];
            String day = "";
            String hour = "";
            String minute = "";
            String zone = "GMT";

            Long scrumId = 0L;

            // IUrlCacher urlCache = new TestUrlCacher(tableURL);
            urlCacher.setUrl(url);
            List<String> lines = urlCacher.get();

            String line = "";
            Iterator<String> it = lines.iterator();
            if (it.hasNext()) {
                line = it.next();
            }

            while (it.hasNext()) {
                line = it.next();
                if (line.contains("fixtureTableDate")) {
                    // line = it.next();
                    String dayDate = line.trim().split("<|>")[2];
                    day = dayDate.trim().split(" ")[1];
                    
                    // Time first
                    line = it.next();
                    String timeVal = line.trim().split("<|>")[2];
                    if (timeVal.contains(".")) {
                        hour = timeVal.split("\\.")[0];
                        minute = timeVal.split("\\.")[1];
                    }

                    // Home Team
                    line = it.next();
                    String homeName = line.trim().split("<|>")[2];
                    if (!teams.containsKey(homeName)) {
                        teams.put(homeName, getTeam(homeName));
                    }

                    // If there's a score, get it.
                    line = it.next();
                   
                    // let's get the visiting team
                    line = it.next();
                    String visitName = line.trim().split("<|>")[2];
                    if (!teams.containsKey(visitName)) {
                        teams.put(visitName, getTeam(visitName));
                    }
                    IMatchGroup match = matchFactory.create(); // get empty one
                    match.setHomeTeam(teams.get(homeName));
                    match.setVisitingTeam(teams.get(visitName));
                    match.setForeignId(scrumId);
                    if (match instanceof MatchGroup) {
                        ((MatchGroup) match).setDisplayName(teams.get(homeName), teams.get(visitName));
                    }
                    match.setDate(getDate(day, month, year, hour, minute, zone));
                    if (matchMap == null) {
                        matchMap = new HashMap<String, IMatchGroup>();
                    }
                    // check to see if we already got it from the results page
                    boolean alreadyThere = false;
                    if (matchMap.containsKey(getMatchMapKey(match))) {
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
            Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage());
            return null;
        }
        return matchMap;
    }
    
    @Override
    public void setURL(String url) {
        homePage = url;

    }
    
    private ITeamGroup getTeam(String teamName) {
        ITeamGroup t = teamGroupFactory.getTeamByName(teamName);
        if (t == null) {
            t = teamGroupFactory.create();
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

    private Date getDate(String day, String month, String year, String hour, String minute, String zone) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MMMM/dd hh:mm z");
        String date = year + "/" + month + "/" + day + " " + hour + ":" + minute + " " + zone;
        java.util.Date utilDate = null;
        try {
            utilDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

	@Override
	public Boolean updateMatch(IMatchGroup match) {
		assert(false);  // this one isn't used any more?
		return null;
	}
}
