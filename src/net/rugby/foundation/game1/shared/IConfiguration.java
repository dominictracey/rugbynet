/**
 * 
 */
package net.rugby.foundation.game1.shared;

import java.util.List;
import java.util.Map;

/**
 * @author home
 *
 */
public interface IConfiguration {

	/** return Map
	 * Key: 	compId
	 * Value:	leagueId
	 * 
	 * @return
	 */
	public abstract Map<Long, Long> getLeagueIdMap();

	public abstract void setLeagueIdMap(Map<Long, Long> leagueIdMap);

	public abstract Long getId();

	public abstract void setId(Long id);

	/**
	 * @return
	 */
	List<Long> getCompetitionClubhouseLeageMapIds();

	/**
	 * @param competitionClubhouseLeageMapIds
	 */
	void setCompetitionClubhouseLeageMapIds(
			List<Long> competitionClubhouseLeageMapIds);

	/**
	 * @return
	 */
	List<IClubhouseLeagueMap> getCompetitionClubhouseLeageMapList();

	/**
	 * @param competitionClubhouseLeageMapList
	 */
	void setCompetitionClubhouseLeageMapList(
			List<IClubhouseLeagueMap> competitionClubhouseLeageMapList);

	/**
	 * @return
	 */
	List<Long> getClubhouseIds();

	/**
	 * @param clubhouseIds
	 */
	void setClubhouseIds(List<Long> clubhouseIds);

}