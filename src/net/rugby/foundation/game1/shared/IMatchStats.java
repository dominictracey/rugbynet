/**
 * 
 */
package net.rugby.foundation.game1.shared;

/**
 * @author home
 *
 */
public interface IMatchStats {

	/**
	 * @return
	 */
	Long getId();

	/**
	 * @param id
	 */
	void setId(Long id);

	/**
	 * @return
	 */
	Long getMatchId();

	/**
	 * @param matchId
	 */
	void setMatchId(Long matchId);

	/**
	 * @return
	 */
	Long getNumPicks();

	/**
	 * @param numPicks
	 */
	void setNumPicks(Long numPicks);

	/**
	 * @return
	 */
	Long getNumHomePicks();

	/**
	 * @param numHomePicks
	 */
	void setNumHomePicks(Long numHomePicks);

}
