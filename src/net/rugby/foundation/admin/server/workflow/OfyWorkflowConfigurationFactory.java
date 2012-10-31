/**
 * 
 */
package net.rugby.foundation.admin.server.workflow;

import java.util.List;

import com.google.inject.Inject;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.admin.shared.IWorkflowConfiguration;
import net.rugby.foundation.admin.shared.WorkflowConfiguration;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.ICompetition;

/**
 * @author home
 *
 */
public class OfyWorkflowConfigurationFactory implements
		IWorkflowConfigurationFactory {
	
	private Objectify ofy;
	private ICompetitionFactory cf;

	@Inject
	public void setFactories(ICompetitionFactory cf) {
		this.cf = cf;
		
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
			
			List<ICompetition> list = cf.getUnderwayComps();
			for (ICompetition comp : list) {
				wfc.getUnderwayCompetitions().add(comp.getId());
			}
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
