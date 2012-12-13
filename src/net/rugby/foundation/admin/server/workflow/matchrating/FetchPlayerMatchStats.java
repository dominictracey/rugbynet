package net.rugby.foundation.admin.server.workflow.matchrating;

import java.util.Iterator;
import java.util.List;

import com.google.appengine.tools.pipeline.Job5;
import com.google.appengine.tools.pipeline.Value;

import net.rugby.foundation.admin.server.UrlCacher;
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.Position;

public class FetchPlayerMatchStats extends Job5<IPlayerMatchStats, IPlayer, IMatchGroup, Home_or_Visitor, Integer, String> {

	private IPlayerMatchStatsFactory pmsf;

	public FetchPlayerMatchStats(IPlayerMatchStatsFactory pmsf) {
		this.pmsf = pmsf;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3101992931956737933L;

	@Override
	public Value<IPlayerMatchStats> run(IPlayer player, IMatchGroup match, Home_or_Visitor hov, Integer slot, String url) {
		IPlayerMatchStats stats = pmsf.getById(null);
		
		
		Boolean found = true;
    	UrlCacher urlCache = new UrlCacher(url);
    	List<String> lines = urlCache.get();
        String line;

		
        if (lines == null) {
        	return null;
        }
        
        // first get the playing time
        
        int countTabs = 0;
        Iterator<String> it = lines.iterator();
        while (it.hasNext() && !found) {
        	
        	line = it.next();
        	// first we scan to the right tab 3rd tab is home, 4th visitor
        	if (line.contains("tabbertab")) {
        		++countTabs;
        	}
        	
        	if ((countTabs == 3 && hov == Home_or_Visitor.HOME) || (countTabs == 4 && hov == Home_or_Visitor.VISITOR)) {
        		line = it.next();
        		line = it.next();
        		
        		for (int i=0; i<slot; ++i) {
        			skipPlayer(it); // 17 lines
        		}
        		
        		//now read the player
        		line = it.next();  //<tr>
        		
        		line = it.next(); //pos
        		stats.setPosition(Position.getFromScrum(line.split(">")[1]));
        		
        		line = it.next(); //name
        		stats.setName(line.split("<|>")[2]); 
        		
        		line = it.next(); //tries/assists
        		stats.setTries(Integer.parseInt(line.split("<|/|>")[2]));
        		stats.setTryAssists(Integer.parseInt(line.split("<|/|>")[3]));
        		
        		line = it.next(); //points
        		stats.setPoints(Integer.parseInt(line.split("<|/|>")[2]));
        		
        		line = it.next(); //points
        		stats.setPoints(Integer.parseInt(line.split("<|/|>")[2]));
        		
        		line = it.next(); //K/P/R
        		stats.setKicks(Integer.parseInt(line.split("<|/|>")[2]));
        		stats.setPasses(Integer.parseInt(line.split("<|/|>")[3]));
        		stats.setRuns(Integer.parseInt(line.split("<|/|>")[4]));
        		
        		line = it.next(); //meters run
        		stats.setMetersRun(Integer.parseInt(line.split("<|/|>")[2]));       		
        		
        		line = it.next(); //clean breaks
        		stats.setCleanBreaks(Integer.parseInt(line.split("<|/|>")[2])); 
        		
        		line = it.next(); 
        		stats.setDefendersBeaten(Integer.parseInt(line.split("<|/|>")[2]));
        		
        		line = it.next();
        		stats.setOffloads(Integer.parseInt(line.split("<|/|>")[2]));
        		
        		line = it.next();
        		stats.setTurnovers(Integer.parseInt(line.split("<|/|>")[2]));
        		
        		line = it.next();  //<!--BP-->?
        		
        		line = it.next(); //tackles/missed
        		stats.setTacklesMade(Integer.parseInt(line.split("<|/|>")[2]));
        		stats.setTacklesMissed(Integer.parseInt(line.split("<|/|>")[3]));
        		
        		line = it.next(); //lineouts won/stolen
        		stats.setLineoutsWonOnThrow(Integer.parseInt(line.split("<|/|>")[2]));
        		stats.setLineoutsStolenOnOppThrow(Integer.parseInt(line.split("<|/|>")[3]));
        		
        		line = it.next();
        		stats.setPenaltiesConceded(Integer.parseInt(line.split("<|/|>")[2]));
        		
        		line = it.next(); 
        		stats.setYellowCards(Integer.parseInt(line.split("<|/|>")[2]));
        		stats.setRedCards(Integer.parseInt(line.split("<|/|>")[3]));
        		
        		stats.setMatchId(match.getId()); // native ID, not scrum's
        		
        		
        		stats.setPlayerId(player.getId());  // native ID, not scrum's
        		if (hov == Home_or_Visitor.HOME) 
        			stats.setTeamId(match.getHomeTeamId());
        		else
        			stats.setTeamId(match.getVisitingTeamId());
        		
        		pmsf.put(stats);
        		
        		found = true;
        	}
        }
		
        if (found = true)
        	return immediate(stats);
        else
        	return null;
	}

	private void skipPlayer(Iterator<String> it) {
		for (int i=0; i<17; ++i) {
			it.next();
		}
		
	}

}
