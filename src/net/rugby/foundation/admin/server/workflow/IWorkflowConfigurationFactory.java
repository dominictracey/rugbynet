/**
 * 
 */
package net.rugby.foundation.admin.server.workflow;

import net.rugby.foundation.admin.shared.IWorkflowConfiguration;

/**
 * @author home
 *
 */
public interface IWorkflowConfigurationFactory {
	IWorkflowConfiguration get();

	/**
	 * @param wfc
	 */
	IWorkflowConfiguration put(IWorkflowConfiguration wfc);
	
}
