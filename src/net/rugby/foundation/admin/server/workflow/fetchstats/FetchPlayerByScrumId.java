package net.rugby.foundation.admin.server.workflow.fetchstats;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import net.rugby.foundation.admin.server.factory.IAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.admin.server.factory.espnscrum.UrlCacher;
import net.rugby.foundation.admin.server.workflow.RetryRequestException;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import com.google.appengine.tools.pipeline.Job5;
import com.google.appengine.tools.pipeline.PromisedValue;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

public class FetchPlayerByScrumId extends Job5<Long, String, String, Long, Long, Long> {

	private static final long serialVersionUID = 483113213168220162L;
	private transient IPlayerFactory pf;
	private transient ICountryFactory cf;
	private transient IAdminTaskFactory atf;
	private transient String playerName;

	private transient IMatchGroupFactory mgf;
	private transient IMatchGroup match;
	
	private static Injector injector = null;
	
	public FetchPlayerByScrumId() {

	}

	/**
	 * return IPlayer reference
	 * params String compName
	 * 			Long scrumId
	 * 			Long adminID
	 * @throws RetryRequestException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */		
	@Override
	public Value<Long> run(String playerName, String referringURL, Long scrumPlayerId, Long adminId, Long matchId) throws RetryRequestException, MalformedURLException, IOException {
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINER);
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINER, "Looking for " + playerName);
		
		if (injector == null) {
			injector = BPMServletContextListener.getInjectorForNonServlets();
		}
		
		this.pf = injector.getInstance(IPlayerFactory.class);
		this.cf = injector.getInstance(ICountryFactory.class);
		this.atf = injector.getInstance(IAdminTaskFactory.class);
		this.mgf = injector.getInstance(IMatchGroupFactory.class);
		
		this.playerName = playerName;

		this.match = mgf.get(matchId);
		
		// first see if we have it in the database
		IPlayer dbPlayer = pf.getByScrumId(scrumPlayerId);
		//player.setDisplayName("Hugo Southwell");

		// will return a "blank player" if it can't find it in the DB so check if the returned player has a scrum ID set.
		if (dbPlayer != null && dbPlayer.getScrumId() != null) {
			return immediate(dbPlayer.getId());
		} else { // didn't find, so go looking
			Value<Long> playerId = getPlayerFromScrum(scrumPlayerId);

			if (playerId != null) {
				return playerId;
			} else {
				// maybe is 404 (fail) or maybe timed out (retry).
				String url = "http://www.espnscrum.com/scrum" + "/rugby/player/" + scrumPlayerId + ".html";
				if (getResponseCode(url) == 404) {
					//still didn't find, need human to get this going.
					IPlayer p = pf.create();
					p.setScrumId(scrumPlayerId);
					p.setDisplayName(playerName);
					p.setGivenName(playerName.split(" ")[0].trim());
					p.setSurName(playerName.substring(p.getGivenName().length()).trim());
					setShortName(p, playerName);
					p.setCountry(cf.getByName("None"));
					pf.put(p);
					PromisedValue<Long> x = newPromise(Long.class);
					IAdminTask task = atf.getNewEditPlayerTask("Something bad happened trying to find " + playerName + " using referring URL " + referringURL, "Nothing saved for player", p, true, getPipelineKey().toString(), getJobKey().toString(), x.getHandle());
					atf.put(task);
					p.getBlockingTaskIds().add(task.getId());
					pf.put(p);
//					match.setWorkflowStatus(WorkflowStatus.BLOCKED);
//					mgf.put(match);
					return x;					
				} else {
					throw new RetryRequestException("Didn't find player information for " + playerName + " from " + match.getDisplayName() + " at " + DateTime.now().toString() + " using url " + url);
				}

			}

		}
	}

	private int getResponseCode(String urlString) throws MalformedURLException, IOException {
	    URL u = new URL(urlString); 
	    HttpURLConnection huc =  (HttpURLConnection)  u.openConnection(); 
	    huc.setRequestMethod("GET"); 
	    huc.connect(); 
	    return huc.getResponseCode();
	}

	/*
	 * So we need to get at least a name, country and birthdate to let the workflow continue. Otherwise send a promised value.
	 */
	private Value<Long> getPlayerFromScrum(Long scrumPlayerId)  {
		
		IPlayer player = pf.create();  //empty

		String playerURL = "http://www.espnscrum.com/scrum" + "/rugby/player/" + scrumPlayerId + ".html";
		List<String> errorDetails = new ArrayList<String>();
		Date dateRead = null;
		boolean found = false;

		try {

			IUrlCacher urlCache = new UrlCacher(playerURL);
			List<String> lines = urlCache.get();
			String line;
			
			if (lines == null) {
				return null;
			}

			Iterator<String> it = lines.iterator();
			while (it.hasNext() && !found) {

				line = it.next();
				// first we scan to the name
				if (line.contains("scrumPlayerName")) {
					setPlayerNames(line, player, errorDetails);
				} else if (line.contains("scrumPlayerCountry")) {
					// added comma to the regex, will this then pick up the first country if they have two listed?
					ICountry country = cf.getByName(line.split("<|>|\\/")[2].trim());
					player.setCountry(country);
					if (country == null) {
						// not finding a country is non-blocking
						country = cf.getByName("None");
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Unable to find player " + player.getDisplayName() + "'s country: " + line.split("<|>")[2].trim());
						errorDetails.add("Couldn't resolve country");
					} else {
						player.setCountryId(country.getId());
					}
				} else if (line.contains("Full name")) {
					try {
						String fullName = line.split("</b>|</div>")[1].trim();
						
						setShortName(player, fullName);
						
						if (!player.getGivenName().isEmpty() && !player.getGivenName().isEmpty() && 
								player.getCountry() != null && !player.getShortName().isEmpty()) {
							found = true;  //names and country are good enough to allow workflow to continue
						}
					} catch (Exception e) {
						errorDetails.add("Couldn't set player's short name");
					}
				} else if (line.contains("Born")) {
					line = it.next();
					if (line != null && !line.contains(",")) {
						line = it.next();
					}
					if (!line.contains(",")) {
						errorDetails.add("Couldn't find valid birthday");
					} else {
						String monthday = line.split(",")[0].trim();
						String year = line.split(",")[1].trim();
						DateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
						dateRead = dateFormatter.parse(monthday + ", " + year);
						if (dateRead != null) {
							player.setBirthDate(dateRead);
							
						} else {
							Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Unable to find birthday for player " + player.getDisplayName() + "'s country: " + line.split("<|>")[2].trim());
							errorDetails.add("Invalid birthday");
						}
					} 
				} else if (line.contains("All Tests")) {
					line = it.next();
					line = it.next();
					Integer numCaps = 0;
					try {
						numCaps = Integer.parseInt(line.split("<|>")[2].trim());
						player.setNumCaps(numCaps);
					} catch  (NumberFormatException e) {
						String name = "unknown";
						if (player.getDisplayName() != null) {
							name = player.getDisplayName();
						}
						errorDetails.add("Couldn't set number of caps");
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Bad number of caps extraction for player " + name);
					}
					
				}  else if (line.contains("Height")) {
					String weightImp = line.split("</b>|</div>")[1].trim();
					String feet = weightImp.trim().split("ft")[0];
					String inches = weightImp.trim().split("ft|in")[1];
					try {
						if (!feet.isEmpty() && !inches.isEmpty()) {
							Float height = ((Float.valueOf(feet) * 12) + Float.valueOf(inches)) * 2.54F;
							player.setHeight(height);
						}
					} catch (NumberFormatException e) {
						String name = "unknown";
						if (player.getDisplayName() != null) {
							name = player.getDisplayName();
						}
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Bad height calculation for player " + name);
						errorDetails.add("Couldn't set height");
					}
				}  else if (line.contains("Weight")) {
					String weightImp = line.split("</b>|</div>")[1].trim();
					String pounds = weightImp.trim().split("lb")[0];
					try {
						if (!pounds.isEmpty()) {
							Float weight = Float.valueOf(pounds) * 0.453592F;
							player.setWeight(weight);
						}
					} catch (NumberFormatException e) {
						String name = "unknown";
						if (player.getDisplayName() != null) {
							name = player.getDisplayName();
						}
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Bad weight calculation for player " + name);
						errorDetails.add("Couldn't set weight");
					}
				}
			}
		} catch (Exception e) {
			// the whole parser blew up so we need a promised value for this player
			Logger.getLogger("Scrum.com").log(Level.SEVERE, e.getMessage());
			errorDetails.add("Parse failure " + e.getMessage());
		}

		if (player != null) {
			player.setScrumId(scrumPlayerId);
			if (player.getNumCaps() == null) {
				player.setNumCaps(0);
			}
			
			// if we have enough to keep going we don't need a PromisedValue, just a "clean up" task
			if (errorDetails.size() > 0)  {
				if (found) {
					pf.put(player); // need an id
					IAdminTask task = atf.getNewEditPlayerTask("Error gathering player information for " + player.getDisplayName(), errorDetails.toString(), player, true, null,null,null);
					atf.put(task);
					player.getTaskIds().add(task.getId());
					pf.put(player);
//					if (match.getWorkflowStatus() != WorkflowStatus.BLOCKED) {
//						match.setWorkflowStatus(WorkflowStatus.TASKS_PENDING);
//						mgf.put(match);
//					}
					return immediate(player.getId());
				} else {
					// if we don't have enough to keep going we can save the player as is and send back a promise
					pf.put(player); // need an id
					PromisedValue<Long> x = newPromise(Long.class);
					String name; 
					if (player.getDisplayName().isEmpty())
						name = playerName;
					else
						name = player.getDisplayName();
					IAdminTask task = atf.getNewEditPlayerTask("Couldn't get sufficient info for player " + name, errorDetails.toString(), player, true, getPipelineKey().getName(), getJobKey().getName(), x.getHandle());
					atf.put(task);
					player.getBlockingTaskIds().add(task.getId());
					pf.put(player);
					
//					match.setWorkflowStatus(WorkflowStatus.BLOCKED);
//					mgf.put(match);
					return x;
				}
			} else {
				if (found) {
					pf.put(player);
					return immediate(player.getId());
				} else {
					// no errors but not found?!?
					return null;
				}
			} 
		} else {
			// if player is null this is bad;
			return null;
		}
	}

	private void setPlayerNames(String line, IPlayer player, List<String> errorDetails) {
		// must be able to handle CJ van der Linde, Jean de Villiers, Jannie du Plessis and Fourie du Preez
		player.setDisplayName(line.split("<|>")[2].trim());
		if (!player.getDisplayName().isEmpty()) {
			int length = player.getDisplayName().split(" ").length;
			if (length > 1) {
				String last = player.getDisplayName().split(" ")[length-1];
				String secondToLast = player.getDisplayName().split(" ")[length-2];
				boolean afrikaans2 = secondToLast.equals(secondToLast.toLowerCase());
				boolean afrikaans3 = false;
				// if the second- and third-to-last name is not capitalized, it is part of the surname.
				if (player.getDisplayName().split(" ").length > 2) {
					// if the second to last word is lower case, then it is part of the surname
					if (afrikaans2) {
						player.setSurName(secondToLast + " " + last);
					}
					String thirdToLast = player.getDisplayName().split(" ")[length-3];
					afrikaans3 = thirdToLast.equals(thirdToLast.toLowerCase());
					
					if (afrikaans3) {
						player.setSurName(thirdToLast + " " + player.getSurName());
					}

				}
				
				if (!afrikaans2 && !afrikaans3) {
					player.setSurName(player.getDisplayName().split(" ")[length-1]);
				} else if (afrikaans3) {
					length -= 2; // don't take the second to last token in the first name
				} else { //afrikaans2
					length--;
				}
				
				String givenName = player.getDisplayName().split(" ")[0];
				for (int i=1; i<length-1; ++i) {
					givenName += player.getDisplayName().split(" ")[i];
				}
				
				player.setGivenName(givenName);
			} else {
				errorDetails.add("Only one name");
			}
		} else {
			errorDetails.add("Display name appears empty");
		}
		
	}

	private void setShortName(IPlayer player, String fullName) {
		// the short name is the first letters of all names except last <space> last name. 
		//e.g. Full name Jonathan James Vaughan Davies ==> JJV Davies
		// also have to deal with Bismarck Wilhelm du Plessis ==> BW du Plessis
		String shortName = "";
		int size = fullName.split("[ |-]+").length - player.getSurName().split(" ").length;
		for (int i=0; i<size; ++i) {
			shortName += fullName.split("[ |-]+")[i].substring(0, 1);
		}
		shortName += " " + player.getSurName();
		player.setShortName(shortName);
		
	}
	
//	public Value<IPlayerMatchStats> handleFailure(Throwable e) {
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Exception thrown fetching player by scrumId: " + e.getLocalizedMessage());
//		//still didn't find, need human to get this going.
//		PromisedValue<IPlayerMatchStats> x = newPromise(IPlayerMatchStats.class);
//		IAdminTask task = atf.getNewEditPlayerTask("Something bad happened trying to find " + playerName + " using referring URL " + referringURL, "Nothing saved for player", null, true, getPipelineKey().toString(), getJobKey().toString(), x.getHandle());
//		atf.put(task);
//
//		return x;
//	}
}
