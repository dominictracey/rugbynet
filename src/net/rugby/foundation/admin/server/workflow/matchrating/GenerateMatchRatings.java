package net.rugby.foundation.admin.server.workflow.matchrating;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.UrlCacher;
import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchRating;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;

import com.google.appengine.tools.pipeline.FutureList;
import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;

public class GenerateMatchRatings extends Job1<List<IMatchRating>, IMatchGroup> {

	private static final long serialVersionUID = 483113213168220162L;
	private static final int MAX_ROSTER = 23;
	private IPlayerFactory pf;
	private ITeamGroupFactory tf;
	private ITeamMatchStatsFactory tmsf;
	private IPlayerMatchStatsFactory pmsf;
	private ICountryFactory cf;
	
	public enum Home_or_Visitor { HOME, VISITOR }
	
	//@Inject
	public GenerateMatchRatings(IPlayerFactory pf, ITeamGroupFactory tf, ITeamMatchStatsFactory tmsf, IPlayerMatchStatsFactory pmsf, ICountryFactory cf) {
		this.pf = pf;
		this.tf = tf;
		this.tmsf = tmsf;
		this.pmsf = pmsf;
		this.cf = cf;
	}
	
	
	/**
	 * return IPlayer reference
	 * params String compName
	 * 			Long scrumId
	 * 			Long adminID
	 */		
	@Override
	public Value<List<IMatchRating>> run(IMatchGroup match) {
		
		String url = match.getForeignUrl();

		Logger.getLogger("FetchedPlayer").log(Level.INFO,"Starting generate match ratings for url " + url);

	    FutureValue<ITeamGroup> homeTeam = futureCall(new FetchTeamFromScrumReport(tf), immediate(Home_or_Visitor.HOME), immediate(url));
	    FutureValue<ITeamGroup> visitTeam = futureCall(new FetchTeamFromScrumReport(tf), immediate(Home_or_Visitor.VISITOR), immediate(url));
	    
	    FutureValue<ITeamMatchStats> homeTeamStats = futureCall(new FetchTeamMatchStats(tmsf), homeTeam, immediate(Home_or_Visitor.HOME), immediate(url));
	    FutureValue<ITeamMatchStats> visitorTeamStats = futureCall(new FetchTeamMatchStats(tmsf), visitTeam, immediate(Home_or_Visitor.VISITOR), immediate(url));
	    
	    List<Long> homeIds = getIds(Home_or_Visitor.HOME, url);
	    List<Long> visitIds = getIds(Home_or_Visitor.VISITOR, url);
	    
	    List<FutureValue<IPlayer>> homePlayers = new ArrayList<FutureValue<IPlayer>>();
	    List<FutureValue<IPlayer>> visitorPlayers = new ArrayList<FutureValue<IPlayer>>();
	    
	    for (Long id : homeIds) {
	    	FutureValue<IPlayer> homePlayer = futureCall(new FetchPlayerByScrumId(pf, cf), immediate((ICompetition)null), immediate("player name"),  immediate(url), immediate(id), immediate(1L));
	    	homePlayers.add(homePlayer);
	    }
	    
	    for (Long id : visitIds) {
	    	FutureValue<IPlayer> visitPlayer = futureCall(new FetchPlayerByScrumId(pf, cf), immediate((ICompetition)null), immediate("player name"),  immediate(url), immediate(id), immediate(1L));
	    	visitorPlayers.add(visitPlayer);
	    }	   

	    List<FutureValue<IPlayerMatchStats>> homePlayerMatchStats = new ArrayList<FutureValue<IPlayerMatchStats>>();
	    List<FutureValue<IPlayerMatchStats>> visitorPlayerMatchStats = new ArrayList<FutureValue<IPlayerMatchStats>>();

	    int count = 0;
	    for (FutureValue<IPlayer> fp : homePlayers) {
	    	FutureValue<IPlayerMatchStats> stats = futureCall(new FetchPlayerMatchStats(pmsf), fp, immediate(match), immediate(Home_or_Visitor.HOME), immediate(count), immediate(url));
	    	homePlayerMatchStats.add(stats);
	    }
	    
	    FutureList<IPlayerMatchStats> hpms = new FutureList<IPlayerMatchStats>(homePlayerMatchStats);
	   
	    count = 0;
	    for (FutureValue<IPlayer> fp : visitorPlayers) {
	    	FutureValue<IPlayerMatchStats> stats = futureCall(new FetchPlayerMatchStats(pmsf), fp, immediate(match), immediate(Home_or_Visitor.VISITOR), immediate(count), immediate(url));
	    	visitorPlayerMatchStats.add(stats);
	    }
	    FutureList<IPlayerMatchStats> vpms = new FutureList<IPlayerMatchStats>(visitorPlayerMatchStats);
	    
	    assert(visitorPlayerMatchStats.size() == MAX_ROSTER);

	    // now we can invoke the engine
	    FutureValue<List<IMatchRating>> ratings = futureCall(new CreateMatchRatings(), hpms, vpms, homeTeamStats, visitorTeamStats);
	    
		return ratings;

	}


	private List<Long> getIds(Home_or_Visitor home, String url) {
		boolean found = false;
		boolean isVisitor = false;
		
		if (home == Home_or_Visitor.VISITOR) {
			isVisitor = true;
		}
		
		List<Long> ids = new ArrayList<Long>();
    	UrlCacher urlCache = new UrlCacher(url);
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
        		for (int i=0; i<38; ++i) {
        			line = it.next(); 
        		}
        		
        		
        		//line = it.next(); // tbody
        		
        		// get 15 home starters
        		for (int i=0; i<15; ++i) {
        			Long id = getId(it);
        			if (!isVisitor)
        				ids.add(id);
        		}
        		
        		// skip to subs
        		for (int i=0; i<7; ++i) {  
        			line = it.next();
        		}
  
        		// get 8 home subs
        		for (int i=0; i<7; ++i) {
        			Long id = getId(it);
        			if (!isVisitor)
        				ids.add(id);
        		}
        		
        		
        		//skip to end of home div
        		for (int i=0; i<3; ++i) {
        			line = it.next(); 
        		}
        		
        		// sitting on home's closing </td>
        		assert(line.contains("</td>"));
        		//skip down to visiting team players
        		for (int i=0; i<8; ++i) {
        			line = it.next(); 
        		}
        		
        		// get 15 visiting team players
        		for (int i=0; i<15; ++i) {
        			Long id = getId(it);
        			if (isVisitor)
        				ids.add(id);
        		}
        		
           		// skip to subs
        		for (int i=0; i<7; ++i) {  
        			line = it.next();
        		}
  
        		// get 8 home subs
        		for (int i=0; i<7; ++i) {
        			Long id = getId(it);
        			if (isVisitor)
        				ids.add(id);
        		}

        		found = true;
        		
        	}
        }
        
        if (found) {
        	return ids;
        } else {
        	return null;
        }
	}
	
	Long getId(Iterator<String> it) {
		String line = "";
		// there are 15 lines to a player section
		for (int i=0; i<7; ++i) {
			line = it.next();
		}
	
		//<a href="/scrum/rugby/player/14650.html" class="liveLineupTextblk" target="_top">Conrad Smith</a>
		String id = "";
		if (line.split("[/|.]").length > 4) {
			id = line.split("[/|.]")[4].trim();
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
		
		// just read innermost </tr>
		for (int i=0; i<3; ++i) {
			line = it.next();
		}
		
		// iterator on outer </tr>
		if (id.isEmpty()) {
			return null;
		} else {
			return Long.parseLong(id);
		}
		
	}
}
