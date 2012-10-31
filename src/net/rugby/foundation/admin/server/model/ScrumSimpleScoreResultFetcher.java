package net.rugby.foundation.admin.server.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ISimpleScoreMatchResult;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.SimpleScoreMatchResult;

public class ScrumSimpleScoreResultFetcher implements IResultFetcher {

	protected ICompetition comp;
	protected IRound round;

	protected String homeTeamName;
	protected String visitTeamName;
	
	@Override
	public IMatchResult getResult(IMatchGroup match)  {
		IMatchResult result = createResult(match);
		
		String resultURL = comp.getForeignURL() + "?template=results";
		
		Date dateRead = null;
        boolean found = false;
        
        try {
            URL url = new URL(resultURL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = reader.readLine()) != null && !found) {
            	
            	if( line.contains("fixtureTableDate")) {
            		line = reader.readLine();
            		String date = line.split("<|>")[0].trim();
            		if (!date.isEmpty()) {
	            		DateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy");
	            		dateRead = dateFormatter.parse(date);
	            		if (dateRead != null) {
	            			// push the day we found back a day so we can compare (don't take time of day into account in comparison)
	            			Calendar one = new GregorianCalendar();
	            			one.setTime(dateRead);
	            			one.add(Calendar.DATE, 1);
	            			dateRead = one.getTime();
	            			if (dateRead.before(match.getDate())) // not posted yet
	            				break;
	            		}
            		}
            	}
            	if( line.contains("fixtureTblContent")) {
            		line = reader.readLine();  // there are multiple
            		line = reader.readLine();  // </td>
               		line = reader.readLine();  //  <td valign="top" class="fixtureTblContent">
               		  // <a href="/premiership-2011-12/rugby/match/142520.html" class="fixtureTablePreview">London Irish&nbsp;42 - 24&nbsp;Worcester Warriors</a>
               		
               		//sometimes there is a commented out line before the one we want
               		if (line.contains("<!--")) {
               			line = reader.readLine();
               		}
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
		            		
		            		//line = reader.readLine(); //24&nbsp;Worcester Warriors</a>
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
            reader.close();

        } catch (MalformedURLException e) {
            Logger.getLogger("Scrum.com").log(Level.SEVERE, e.getMessage());
            return null;
        } catch (IOException e) {
            Logger.getLogger("Scrum.com").log(Level.SEVERE, e.getMessage());
            return null;
        } catch (ParseException e) {
            Logger.getLogger("Scrum.com").log(Level.SEVERE, e.getMessage());
            return null;
        }
		
        if (found)
        	return result;
        else
        	return null;
	}

	/**
	 * @return
	 */
	protected IMatchResult createResult(IMatchGroup match) {
		SimpleScoreMatchResult result = new SimpleScoreMatchResult();
		
		homeTeamName = match.getHomeTeam().getDisplayName();
		visitTeamName = match.getVisitingTeam().getDisplayName();
			
		result.setMatchID(match.getId());
		result.setRecordedDate(new Date());
		
		return result;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.fetch.IResultFetcher#setComp(net.rugby.foundation.model.shared.ICompetition)
	 */
	@Override
	public void setComp(ICompetition comp) {
		this.comp = comp;
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.fetch.IResultFetcher#setRound(net.rugby.foundation.model.shared.IRound)
	 */
	@Override
	public void setRound(IRound round) {
		this.round = round;
		
	}


}
