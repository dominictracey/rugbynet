/**
 * 
 */
package net.rugby.foundation.game1.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import net.rugby.foundation.game1.shared.IConfiguration;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.game1.shared.ILeaderboard;
import net.rugby.foundation.game1.shared.IMatchStats;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.IClubhouse;

/**
 * @author home
 *
 */
@RemoteServiceRelativePath("service")
public interface Game1Service extends RemoteService {
	IEntry getEntry(Long id);
	IEntry createEntry(String name, Long compId);
	IEntry saveEntry(IEntry entry);
	Boolean deleteEntry(IEntry entry);
	Boolean deleteEntriesForUser(IAppUser user);
	ArrayList<IEntry> getEntriesForCurrentUser(Long compId);
//	ILeaderboard createLeaderboard(Long compId);
	/**
	 * @return
	 */
	IConfiguration getConfiguration();
	/**
	 * @param compId
	 * @param clubhouseId
	 * @return
	 */
	ILeaderboard getLeaderboard(Long compId, Long clubhouseId);
	/**
	 * @param clubhouse
	 * @return whether the leagues and ClubhouseLeagueMaps were properly created
	 */
	Boolean createNewClubhouseLeagues(IClubhouse clubhouse);
	/**
	 * @param config
	 * @param compsToAdd - List of compIds to add to the game (creates compClubhouse, League and ClubhouseLeagueMap objects)
	 * @param compsToDrop
	 * @return
	 */
	IConfiguration updateConfiguration(IConfiguration config,
			List<Long> compsToAdd, List<Long> compsToDrop);
	/**
	 * Use this to check for duplications of entry names. Hopefully this will return null for you.
	 * @param name
	 * @param compId
	 * @return Entry for that name and comp; null if not found.
	 */
	IEntry getEntryByName(String name, Long compId);
	
	List<IMatchStats> getMatchStats(Long matchId);
	
	/**
	 * @param config
	 * @param compsToAdd - List of compIds to update the MatchStats for
	 * 
	 **/
	IConfiguration updateMatchStats(IConfiguration config, List<Long> compsToRedo);
			
}
