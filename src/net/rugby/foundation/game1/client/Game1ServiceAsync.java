/**
 * 
 */
package net.rugby.foundation.game1.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

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
public interface Game1ServiceAsync {

	void getEntry(Long id, AsyncCallback<IEntry> cb );
	void createEntry(String name, Long compId, AsyncCallback<IEntry> cb );
	void saveEntry(IEntry entry, AsyncCallback<IEntry> asyncCallback);
	void deleteEntry(IEntry entry, AsyncCallback<Boolean> cb);
	void deleteEntriesForUser(IAppUser user, AsyncCallback<Boolean> cb);
	void getEntriesForCurrentUser(Long compId,  AsyncCallback<ArrayList<IEntry>> cb);
//	void createLeaderboard(Long compId, AsyncCallback<ILeaderboard> cb);
	void getConfiguration(AsyncCallback<IConfiguration> cb);
	void getLeaderboard(Long compId, Long clubhouseId, AsyncCallback<ILeaderboard> cb);
	void createNewClubhouseLeagues(IClubhouse clubhouse, AsyncCallback<Boolean> cb);
	void updateConfiguration(IConfiguration config, List<Long> compsToAdd, List<Long> compsToDrop, AsyncCallback<IConfiguration> cb);
	void getEntryByName(String name, Long compId,  AsyncCallback<IEntry> cb);
	/**
	 * @param matchId
	 * @param asyncCallback
	 */
	void getMatchStats(Long matchId,
			AsyncCallback<List<IMatchStats>> asyncCallback);
	/**
	 * @param config
	 * @param compsToRedo
	 * @param asyncCallback
	 */
	void updateMatchStats(IConfiguration config, List<Long> compsToRedo,
			AsyncCallback<IConfiguration> asyncCallback);
}
