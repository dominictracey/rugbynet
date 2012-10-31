/**
 * 
 */
package net.rugby.foundation.game1.server.factory;

import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.ILeaderboard;
import net.rugby.foundation.model.shared.IRound;

/**
 * @author Dominic Tracey
 *
 */
public interface ILeaderboardFactory {
	void setId(Long id);
	void setClmAndRound(IClubhouseLeagueMap clm, IRound round);
	
	/**
	 * If id != null gets the Leaderboard with that Id (if it exists - null otherwise).
	 * If round != null league != null gets the leaderboard for that round (if it exists - null otherwise).
	 * If they are both null gets an empty leaderboard.
	 */
	ILeaderboard get();
	
	/**
	 * 
	 * @return a copy of the Leaderboard referenced with setId()
	 */
	ILeaderboard cloneFrom();
	ILeaderboard put(ILeaderboard lb);	
	
	ILeaderboard getNew();

}
