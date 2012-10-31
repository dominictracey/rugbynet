/**
 * 
 */
package net.rugby.foundation.admin.server.workflow;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.admin.shared.IWorkflowConfiguration;
import net.rugby.foundation.admin.shared.WorkflowConfiguration;

/**
 * @author home
 *
 */
public class TestWorkflowConfigurationFactory implements
		IWorkflowConfigurationFactory {

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IWorkflowConfigurationFactory#get()
	 */
	@Override
	public IWorkflowConfiguration get() {
		IWorkflowConfiguration wfc = new WorkflowConfiguration();
		List<Long> underwayCompetitions = new ArrayList<Long>();
		underwayCompetitions.add(1L);	
		underwayCompetitions.add(2L);
		wfc.setUnderwayCompetitions(underwayCompetitions);
		return wfc;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IWorkflowConfigurationFactory#put(net.rugby.foundation.admin.shared.IWorkflowConfiguration)
	 */
	@Override
	public IWorkflowConfiguration put(IWorkflowConfiguration wfc) {
		
		return wfc;
	}

}
