/**
 * 
 */
package net.rugby.foundation.game1.shared;

import java.util.List;

/**
 * @author home
 *
 */
public interface ILeaderboardRow extends Comparable<ILeaderboardRow> {
	Long getId();
	void setId(Long id);
	Long getAppUserId();
	void setAppUserId(Long appUserId);
	String getAppUserName();
	void setAppUserName(String appUserName);
	Long getEntryId();
	void setEntryId(Long EntryId);
	String getEntryName();
	void setEntryName(String entryName);
	List<Integer> getScores();
	void setScores(List<Integer> scores);
	Integer getRank();
	void setRank(Integer rank);
	Integer getTotal();
	void setTotal(Integer total);

	Integer getTieBreakerFactor();
	void setTieBreakerFactor(Integer tieBreakerFactor);
}
