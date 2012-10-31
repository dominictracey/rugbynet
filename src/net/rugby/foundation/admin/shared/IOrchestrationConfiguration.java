/**
 * 
 */
package net.rugby.foundation.admin.shared;

import java.util.HashMap;
import java.util.Map;

/**
 * @author home
 *
 */
public interface IOrchestrationConfiguration {

	public abstract Long getCompID();

	public abstract void setCompID(Long compID);

	public abstract boolean isSimpleMatchResultScrum();

	public abstract void setSimpleMatchResultScrum(
			boolean simpleMatchResultScrum);

	public abstract String getAdminEmail();

	public abstract void setAdminEmail(String adminEmail);

	public abstract Long getId();

	public abstract void setId(Long id);

	/**
	 * @return
	 */
	public abstract Map<String, Boolean> getMatchActions();

	/**
	 * @param matchActions
	 */
	public abstract void setMatchActions(HashMap<String, Boolean> matchActions);

	/**
	 * @return
	 */
	public abstract Map<String, Boolean> getCompActions();

	/**
	 * @param compActions
	 */
	public abstract void setCompActions(HashMap<String, Boolean> compActions);

}