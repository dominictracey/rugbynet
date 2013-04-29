package net.rugby.foundation.admin.server.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.UrlCacher;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.ISimpleScoreMatchResult;
import net.rugby.foundation.model.shared.IMatchGroup.Status;

public class ScrumSuperRugbySimpleScoreResultFetcher extends ScrumSimpleScoreResultFetcher implements IResultFetcher {

	public ScrumSuperRugbySimpleScoreResultFetcher(IMatchGroupFactory mf, IMatchResultFactory mrf) {
		super(mf,mrf);
	}

	@Override
	public IMatchResult getResult(IMatchGroup match)  {
		IMatchResult result = createResult(match);
		
		String resultURL = comp.getForeignURL() + "?template=results";
		
		Date dateRead = null;
        boolean found = false;
        boolean foundDate = false;
        
        try {
//            URL url = new URL(resultURL);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        	UrlCacher urlCache = new UrlCacher(resultURL);
        	List<String> lines = urlCache.get();
            String line;

            Iterator<String> it = lines.iterator();
            while (it.hasNext() && !found) {
            	
            	line = it.next();
            	// first we scan to the right date
            	if( line.contains("fixtureTableDate")) {
            		line = it.next();
            		String date = line.split("<|>")[0].trim();
            		if (!date.isEmpty()) {
	            		DateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy");
	            		dateRead = dateFormatter.parse(date);
	            		if (dateRead != null) {
	            			// push the day we found back a day so we can compare (don't take time of day into account in comparison)
	            			Calendar cal = new GregorianCalendar();
	            			cal.setTime(match.getDate());
	            			cal.set(Calendar.HOUR_OF_DAY, 0);  
	            			cal.set(Calendar.MINUTE, 0);  
	            			cal.set(Calendar.SECOND, 0);  
	            			cal.set(Calendar.MILLISECOND, 0); 
	            			Date matchDate = cal.getTime();
	            			if (dateRead.equals(matchDate)) {
	            				foundDate = true;
	            				break;
	            			} //else if (dateRead.after(matchDate)) { // we haven't found it and are looking at later dates so it's not posted yet
	            				//break;
	            			//}
	            		}
            		}
            	}
            }
            
            if (foundDate) {
	            while ((line = it.next()) != null && !found) {
	            	if( line.contains("fixtureTblContent")) {
	            		line = it.next();  // there are multiple
	            		line = it.next();  // </td>
	               		line = it.next();  //  <td valign="top" class="fixtureTblContent">
	               		  // <a href="/premiership-2011-12/rugby/match/142520.html" class="fixtureTablePreview">London Irish&nbsp;42 - 24&nbsp;Worcester Warriors</a>
	               		
	               		//sometimes there is a commented out line before the one we want
	               		if (line.contains("<!--")) {
	               			line = it.next();
	               		}
	               		// get the match URL and id saved
	               		saveMatchInfo(match, line);
	               		
	               		// postponed matches seem to not have a link to anywhere after a while - either way, no score
	               		if (line.contains("P v P")) {
	               			if (line.contains(homeTeamName) && line.contains(visitTeamName)) {
	               				result.setStatus(Status.POSTPONED); 
	               				found = true;
	               			}
	               		} else {
	               			if (line.split("<|>").length > 2) {
			            		String homeTeamStuff = line.split("<|>")[2];  //  London Irish&nbsp;42 - 24&nbsp;Worcester Warriors
			            		
			            		String foundHomeName = homeTeamStuff.split("&nbsp;")[0];
			            		if (foundHomeName.equals(homeTeamName)) {
			            			((ISimpleScoreMatchResult)result).setHomeScore(Integer.parseInt(homeTeamStuff.split("&nbsp;|-")[1].trim()));
			            		}
			            		
			            		//line = it.next(); //24&nbsp;Worcester Warriors</a>
			            		String foundVisitName = line.split("&nbsp;|<")[3];
			            		if (foundVisitName.equals(visitTeamName)) {
			            			((ISimpleScoreMatchResult)result).setVisitScore(Integer.parseInt(homeTeamStuff.split("&nbsp;|-")[2].trim()));
			               			if (((ISimpleScoreMatchResult)result).getVisitScore() > ((ISimpleScoreMatchResult)result).getHomeScore())
			               				result.setStatus(Status.FINAL_VISITOR_WIN);
			               			else if (((ISimpleScoreMatchResult)result).getVisitScore() < ((ISimpleScoreMatchResult)result).getHomeScore())
			               				result.setStatus(Status.FINAL_HOME_WIN);
			               			else if (((ISimpleScoreMatchResult)result).getVisitScore() == ((ISimpleScoreMatchResult)result).getHomeScore())
			               				result.setStatus(Status.FINAL_DRAW);
			            			found = true;
			            		}
			 
			            	}
		            	}
	            	}
	            }
            }

        } catch (ParseException e) {
            Logger.getLogger("Scrum.com").log(Level.SEVERE, e.getMessage());
            return null;
        }
		
        if (found) {
			mrf.put(result);
			match.setSimpleScoreMatchResultId(result.getId());
			match.setSimpleScoreMatchResult((ISimpleScoreMatchResult) result);
			match.setStatus(result.getStatus());
			match.setLocked(true);
			mf.put(match);
        
        	return result;
        } else
        	return null;
	}

	private void saveMatchInfo(IMatchGroup match, String line) {
		 // <a href="/premiership-2011-12/rugby/match/142520.html" class="fixtureTablePreview">London Irish&nbsp;42 - 24&nbsp;Worcester Warriors</a>
		if (line.split("\"").length > 2) {
			String foreignUrl = line.split("\"")[1];
			match.setForeignUrl("http://www.espnscrum.com" + foreignUrl);
			// and the matchId
			if (foreignUrl.split("[/|.]").length > 3) {
				match.setForeignId(Long.parseLong(foreignUrl.split("[/|.]")[4]));
			}
		}
	}


}
