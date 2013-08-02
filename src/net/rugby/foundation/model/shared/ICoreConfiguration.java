/**
 * 
 */
package net.rugby.foundation.model.shared;

import java.util.List;
import java.util.Map;

/**
 * @author home
 *
 */
public interface ICoreConfiguration {
	public abstract void addCompetition(Long id, String name);
	public abstract Map<Long, String> getCompetitionMap();
	
	public abstract Long getDefaultCompId();
	/**
	 * @param defaultCompId
	 */
	void setDefaultCompId(Long defaultCompId);
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
	List<Long> getCompsUnderway();
	/**
	 * @param compsUnderway
	 */
	void setCompsUnderway(List<Long> compsUnderway);
	/**
	 * @param compId
	 */
	void addCompUnderway(Long compId);
	
	boolean deleteComp(Long compId);
}