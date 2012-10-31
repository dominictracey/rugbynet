/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import java.util.List;

import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;

/**
 * @author home
 *
 */
public interface IOrchestrationConfigurationFactory {
	void setId(Long id);
	void setCompId(Long compId);
	IOrchestrationConfiguration get();
	List<IOrchestrationConfiguration> getAll();
	/**
	 * @param oc
	 * @return 
	 */
	IOrchestrationConfiguration put(IOrchestrationConfiguration oc);
	
	
}
