package net.rugby.foundation.admin.server.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.admin.server.factory.espnscrum.UrlCacher;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ISimpleScoreMatchResult;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.MatchGroup;
import net.rugby.foundation.model.shared.SimpleScoreMatchResult;

public class ScrumSimpleScoreResultFetcher implements IResultFetcher {

	protected ICompetition comp;
	protected IRound round;

	protected String homeTeamName;
	protected String visitTeamName;
	protected IMatchGroupFactory mf;

	// when we are parsing all the matches out, this is where we keep the date of the matches being read
	private Date dateCursor;
	protected IMatchResultFactory mrf;
	protected IUrlCacher uc;
	 
	public ScrumSimpleScoreResultFetcher(IMatchGroupFactory mf, IMatchResultFactory mrf, IUrlCacher uc) {
		this.mf = mf;
		this.mrf = mrf;
		this.uc = uc;
	}

	@Override
	public IMatchResult getResult(IMatchGroup match)  {
		IMatchResult result = createResult(match);

		String resultURL = comp.getForeignURL() + "?template=results";

		Date dateRead = null;
		boolean found = false;

		try {
			//URL url = new URL(resultURL);
			//BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
//			IUrlCacher urlCache = new UrlCacher(resultURL);
			uc.setUrl(resultURL);
			List<String> lines = uc.get();
			String line = "";
			Iterator<String> it = lines.iterator();
			if (it.hasNext()) {
				line = it.next();
			}
			while (it.hasNext() && !found) {

				if( line.contains("fixtureTableDate")) {
					line = it.next();
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
					line = it.next();  // there are multiple
					line = it.next();  // </td>
					line = it.next();  //  <td valign="top" class="fixtureTblContent">
					// <a href="/premiership-2011-12/rugby/match/142520.html" class="fixtureTablePreview">London Irish&nbsp;42 - 24&nbsp;Worcester Warriors</a>

					//sometimes there is a commented out line before the one we want
					if (line.contains("<!--")) {
						line = it.next();
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
			//reader.close();

//		} catch (MalformedURLException e) {
//			Logger.getLogger("Scrum.com").log(Level.SEVERE, e.getMessage());
//			return null;
//		} catch (IOException e) {
//			Logger.getLogger("Scrum.com").log(Level.SEVERE, e.getMessage());
//			return null;
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

		homeTeamName = match.getHomeTeam().getScrumName();
		visitTeamName = match.getVisitingTeam().getScrumName();

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

	@Override
	public Map<String, IMatchGroup> getMatches(String url,
			Map<String, ITeamGroup> teams) {

		try {
			Map<String, IMatchGroup> result = new HashMap<String, IMatchGroup>();

			String resultURL = url + "?noredir=1;template=results";

			IUrlCacher urlCache = new UrlCacher(resultURL);
			List<String> lines = urlCache.get();
			String line;

			boolean found = false;

			Iterator<String> it = lines.iterator();
			while (it.hasNext() && !found) {

				line = it.next();
				// first we scan to the top of the table
				// <td class="fixtureTblColHdr" colspan="3">Match</td>
				if (line != null && line.contains("<td class=\"fixtureTblColHdr\" colspan=\"3\">Match</td>")) {
					found = true;
				}
			}

			
			boolean more = it.hasNext();
			if (more) {
				line = it.next();
			}
			
			while (found && more) {
				line = it.next();
				// read until </tbody>
				if (line.contains("</tbody>") || line.contains("</table>")) {
					more = false;
				} else {
					
					assert(line.contains("<tr>"));
					IMatchGroup match = getMatch(it, teams);
					if (match != null) {
						result.put(getMatchMapKey(match), match);
						if (match.getDisplayName().equals("Saracens vs. London Irish")) {
							Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, match.getDisplayName());
						}
					}
				}
			}
			
			if (found)
				return result;
			else
				return null;

		} catch (Exception e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Error in getMatches " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 *  This will process one row of the ?template=results table. There are three flavors of rows:
	 *  	1) Match with date in first column if this is the first match on that date
	 *  	2) Match with no date in the first column if it isn't the first match on that date
	 *  	3) Blank row after last match on a date
	 * @param it - string iterator for the ?template=result page pointing to the <tr> of a row
	 * @param teams - Map key: team displayName, value ITeamGroup
	 * @return IMatchGroup if match in row, null otherwise
	 */
	private IMatchGroup getMatch(Iterator<String> it, Map<String, ITeamGroup> teams) {



		String line = it.next();

		// see if we are the blank line (see #3 above)
		if (line.contains("colspan=\"5\"")) {
			line = it.next();
			assert(line.contains("</tr>"));
			return null;
		} 

		IMatchGroup match = mf.create();  // get empty match

		// see if we have a new date
		if (line.contains("fixtureTableDate")) {
			setDate(it);
		}

		match.setDate(dateCursor);

		// now skip to the second cell that has 
		int count = 0;
		while (it.hasNext() && count < 2) {
			if (line.contains("fixtureTblContent")) {
				count++;
			}

			line = it.next();
		}

		if (!it.hasNext()) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Ran off end of file looking for match content");
			return null;
		}

		// sometimes a comment here
		if (line.contains("<!--")) {
			line = it.next();
		}

		// TODO Implement postponed matches from past events
		//		// postponed matches seem to not have a link to anywhere after a while - either way, no score
		if (line.contains("P v P")) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Found a postponed match and we haven't implemented the handler for it yet.");
			return null;
		}
		//			if (line.contains(homeTeamName) && line.contains(visitTeamName)) {
		//				result.setStatus(Status.POSTPONED); 
		//				found = true;
		//			}
		//		}

		// <a href="/premiership-2011-12/rugby/match/142520.html" class="fixtureTablePreview">London Irish&nbsp;42 - 24&nbsp;Worcester Warriors</a>
		String parts[] = line.split("[/|.]");
		if (parts.length > 6) {
			int i = 0;
			boolean found = false;
			while (i < parts.length && found == false) {
				if (parts[i].equals("match"))
					found = true;
				++i;
			}
			if (found)  {
				match.setForeignId(Long.parseLong(parts[i]));
				//match.setForeignUrl("http://www.espnscrum.com/scrum/rugby/current/match/" + parts[i] + ".html?view=scorecard" );
				
			}  else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Couldn't get scrum id from " + line + " (couldn't find match token)");
			}
		}

		// Now get the team names
		parts = line.split("<|>|&nbsp;");
		String homeName = null;
		String visitName = null;
		if (parts.length > 3) {
			if (teams.containsKey(parts[2])) {
				homeName = parts[2];
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Couldn't find a home team in the team map in " + line );
				return null;
			}


			if (teams.containsKey(parts[4])) {
				visitName = parts[4];
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Couldn't find a visit team in the team map in " + line );
				return null;
			}
		}

		if (homeName != null) {
			match.setHomeTeam(teams.get(homeName));
		}

		if (visitName != null) {
			match.setVisitingTeam(teams.get(visitName));
		}

		if (match instanceof MatchGroup) {
			((MatchGroup)match).setDisplayName(teams.get(homeName), teams.get(visitName)); 
		}

		//		String times = line.trim().split(">")[1].trim();
		//		if (hasLink) {
		//			times = line.trim().split(">")[3].trim();
		//			}
		//		String gmt = times.split(",")[1];
		//		hour = gmt.trim().split(":")[0];
		//		minute = gmt.trim().split(":| ")[1];
		//		zone = gmt.trim().split(":| ")[2];
		//		match.setDate(getDate(day, month, year, hour, minute, zone));

		// read to the </tr>
		boolean found = false;
		while (it.hasNext() && !found) {
			line = it.next();
			if (line.contains("</tr>")) {
				found = true;
			}
		}
		
		if (!found) {
			String displayName = "No match";
			if (match != null)
				displayName = match.getDisplayName();
			
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Couldn't find end of row for match " + displayName);
		}
		return match;
	}

	private void setDate(Iterator<String> it) {
		String line = it.next();
		String date = line.split("<|>")[0].trim();
		if (!date.isEmpty()) {
			DateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy");
			try {
				dateCursor = dateFormatter.parse(date);
			} catch (ParseException e) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Couldn't find a date in the line passed " + line );

				e.printStackTrace();
			}

		}
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
	public Boolean isAvailable(IMatchGroup match) {
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Don't use the base class ScrumSimpleScoreResultFetcher to check isAvailable, use the Super Rugby one." );
		return null;
	}


	}
