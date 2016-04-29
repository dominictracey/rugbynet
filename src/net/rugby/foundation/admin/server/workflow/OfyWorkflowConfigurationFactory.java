/**
 * 
 */
package net.rugby.foundation.admin.server.workflow;

import net.rugby.foundation.admin.shared.IWorkflowConfiguration;
import net.rugby.foundation.admin.shared.WorkflowConfiguration;
import net.rugby.foundation.model.shared.DataStoreFactory;

import com.google.inject.Inject;
import com.googlecode.objectify.Objectify;

/**
 * @author home
 *
 */
public class OfyWorkflowConfigurationFactory implements
		IWorkflowConfigurationFactory {
	
	private Objectify ofy;

	@Inject
	public void setFactories() {
		
	}
	
	public OfyWorkflowConfigurationFactory() {

		ofy = DataStoreFactory.getOfy();
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IWorkflowConfigurationFactory#get()
	 */
	@Override
	public IWorkflowConfiguration get() {
		
		//there should just be one...
		IWorkflowConfiguration wfc = ofy.query(WorkflowConfiguration.class).get();
		
		// if there isn't one, create one with all the underway comps
		if (wfc == null) {
			wfc = new WorkflowConfiguration();
			put(wfc);
		}
		return wfc;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IWorkflowConfigurationFactory#put(net.rugby.foundation.admin.shared.IWorkflowConfiguration)
	 */
	@Override
	public IWorkflowConfiguration put(IWorkflowConfiguration wfc) {
		ofy.put(wfc);
		return wfc;
	}

}
