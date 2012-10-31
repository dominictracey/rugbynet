/**
 * 
 */
package net.rugby.foundation.game1.shared;

import java.util.List;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRound;

/**
 * @author Dominic Tracey
 * This interface provides access to the game leaderboard object hierarchy, which presents to the end user the current and
 *  and past standings for the combination of:
 *  <ul>
 *  <li>game type
 *  <li>competition
 *  </ul>
 *  
 *  In future versions we will support clubhouse subsets of the user population but currently this is global in nature.
 *
 */
public interface ILeaderboard {
	/**
	 * @return the DB id of the row entry
	 */
	Long getId();
	/**
	 * @param id the DB id of the row entry
	 */
	void setId(Long id);
	/**
	 * @return the id of the Competition this leaderboard is for
	 */
	Long getCompId();

	/**
	 * @param compId - the id of the Competition this leaderboard is for
	 */
	void setCompId(Long compId);
	
	/**
	 * @return The competition this leaderboard is represents
	 */
	ICompetition getComp();
	
	/**
	 * @param comp - The competition this leaderboard is represents (only set by factory during injection)
	 */
	void setComp(ICompetition comp);
	
	/**
	 * @return an ArrayList of {@link}ILeaderboardRow objects
	 */
	List<ILeaderboardRow> getRows();
	/**
	 * @param rows - a List of {@link}ILeaderboardRow objects that each contain one entry's scoring for the competition.  (only set by factory during injection)
	 */
	void setRows(List<ILeaderboardRow> rows);
	
	/**
	 * @return A list of the database IDs for the {@link #ILeaderboardRow} objects
	 * 
	 * @see #ILeaderboardRow
	 */
	List<Long> getRowIds();
	
	/**
	 * @param rows - A list of the database IDs for the {@link #ILeaderboardRow} objects (only set by factory during injection)
	 */
	void setRowIds(List<Long> rows);
	/**
	 * @return - The round ID that this Leaderboard was created for. One should be created by orchestrations for each round.
	 */
	Long getRoundId();
	
	
	/**
	 * @param roundId - The round ID that this Leaderboard was created for.
	 */
	void setRoundId(Long roundId);
	
	/**
	 * @return - Round this leaderboard is for
	 */
	IRound getRound();
	
	/**
	 * @param round - Round to associate with this leaderboard
	 */
	void setRound(IRound round);
	
	/**
	 * @return true if everything is ok and ready to save.
	 */
	boolean sanityCheck();
	
	/**
	 * @return The abbreviations of the names of the rounds that can be used as column headers when displaying LB
	 */
	List<String> getRoundNames();
	/**
	 * @param roundNames - The abbreviations of the names of the rounds that can be used as column headers when displaying LB
	 */
	void setRoundNames(List<String> roundNames);
	/**
	 * @return
	 */
	ILeague getLeague();
	/**
	 * @param league
	 */
	void setLeague(ILeague league);
	/**
	 * @return
	 */
	Long getLeagueId();
	/**
	 * @param leagueId
	 */
	void setLeagueId(Long leagueId);
	
}
