/**
 * 
 */
package net.rugby.foundation.admin.shared;

import java.util.List;

/**
 * @author home
 *
 */
public interface IWorkflowConfiguration {

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract List<Long> getUnderwayCompetitions();

	public abstract void setUnderwayCompetitions(
			List<Long> underwayCompetitions);

}