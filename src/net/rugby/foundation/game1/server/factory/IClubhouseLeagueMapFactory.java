/**
 * 
 */
package net.rugby.foundation.game1.server.factory;

import java.util.List;

import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;

/**
 * @author home
 *
 */
public interface IClubhouseLeagueMapFactory {
	void setId(Long id);
	/**
	 * call this and the call get() - not getList(). It should just get you one.
	 * @param compId
	 * @param clubhouseId
	 */
	void setClubhouseAndCompId(Long compId, Long clubhouseId);
	/**
	 * So either call setId() or call setClubhouseAndCompId() before calling this.
	 * 
	 * 	@return either the requested CLM (creating it if necessary if one doesn't exist for the requested clubhouseId and
	 * compId. 
	 * 
	 * If the Id was set to 0L, it will return an empty CLM.
	 * 
	 * If only a non-0L Id was specified, and it can't find that Id, it will return null.
	 */
	IClubhouseLeagueMap get();
	IClubhouseLeagueMap put(IClubhouseLeagueMap dl);
	
	void setClubhouseId(Long clubhouseId);
	void setCompetitionId(Long competitionId);
	void setLeagueId(Long leagueId);
	List<IClubhouseLeagueMap> getList();
	/**
	 * call setId() first
	 */
	Boolean delete();

}
