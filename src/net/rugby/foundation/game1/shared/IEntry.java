/**
 * 
 */
package net.rugby.foundation.game1.shared;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author home
 *
 */
public interface IEntry {
	
	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract Long getCompId();

	public abstract void setCompId(Long compId);

	public abstract List<Long> getRoundEntryIdList();

	public abstract void setRoundEntryIdList(List<Long> roundEntryMap);

	public abstract Date getLastUpdated();

	public abstract void setLastUpdated(Date lastUpdated);

	public abstract Date getCreated();

	public abstract void setCreated(Date created);

	/**
	 * 
	 * @return Key: RoundID, Value: RoundEntry for that round. Key is not RoundEntryId!
	 */
	public Map<Long, IRoundEntry> getRoundEntries();
	
	public void setRoundEntries(Map<Long, IRoundEntry> roundEntries);

	/**
	 * @return
	 */
	public String getName();

	/**
	 * @param name
	 */
	public void setName(String name);
	
	public void setOwnerId(Long id);
	
	public Long getOwnerId();

	/**
	 * @return
	 */
	Integer getScore();

	/**
	 * @param score
	 */
	void setScore(Integer score);
}