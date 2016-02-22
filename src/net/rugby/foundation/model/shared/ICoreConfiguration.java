/**
 * 
 */
package net.rugby.foundation.model.shared;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rugby.foundation.model.shared.CoreConfiguration.Environment;

/**
 * @author home
 *
 */
public interface ICoreConfiguration extends IHasId {
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
	Environment getEnvironment();
	void setEnvironment(Environment environment);
	String getBaseToptenUrl();
	String getFacebookAppid();
	void removeCompUnderway(Long compId);
//	String getBaseToptenUrlForFacebook();
	String getEngineUrl();
	public abstract Long getGlobalCompId();
	public abstract void setGlobalCompId(Long globalCompId);
	HashMap<Long, HashMap<RatingMode, Long>> getSeriesMap();
	void setSeriesMap(HashMap<Long, HashMap<RatingMode, Long>> seriesMap);
	List<Long> getCompsForClient();
	void setCompsForClient(List<Long> compsUnderway);
	void addCompForClient(Long compId);
	void removeCompForClient(Long compId);
	void setCurrentUROrdinal(int currentUROrdinal);
	int getCurrentUROrdinal();
}