/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.admin.server.workflow.IWorkflowConfigurationFactory;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.CompActions;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.MatchActions;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.OrchestrationConfiguration;
import net.rugby.foundation.model.shared.DataStoreFactory;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

/**
 * @author home
 *
 */
public class OfyOrchestrationConfigurationFactory implements
		IOrchestrationConfigurationFactory {

	private Long id;
	private Long compId;
	private Objectify ofy;
	private IWorkflowConfigurationFactory wfcf;


	@Inject
	public void setFactories(IWorkflowConfigurationFactory wfcf) {

		this.wfcf = wfcf;

	}
	public OfyOrchestrationConfigurationFactory() {

		ofy = DataStoreFactory.getOfy();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationConfigurationFactory#get()
	 */
	@Override
	public IOrchestrationConfiguration get() {

		if (id != null)
			return ofy.find(new Key<OrchestrationConfiguration>(OrchestrationConfiguration.class, id));
		else if (compId != null)
			return ofy.query(OrchestrationConfiguration.class).filter("compID", compId).get();
		else {
			// give back an empty one with everything turned off
			IOrchestrationConfiguration oc = new OrchestrationConfiguration();
			for (CompActions action : CompActions.values()) {	
				oc.getCompActions().put(action.getValue(),false);
			}
			
			for (MatchActions action : MatchActions.values()) {		
				oc.getMatchActions().put(action.getValue(),false);
			}
			
			oc.setAdminEmail("dominic.tracey@gmail.com");
			oc.setSimpleMatchResultScrum(false);

			return oc;
		}
			
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationConfigurationFactory#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
		this.compId = null;
		
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationConfigurationFactory#setCompId(java.lang.Long)
	 */
	@Override
	public void setCompId(Long compId) {
		this.id = null;
		this.compId = compId;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationConfigurationFactory#getAll()
	 */
	@Override
	public List<IOrchestrationConfiguration> getAll() {
		
		List<IOrchestrationConfiguration> all = new ArrayList<IOrchestrationConfiguration>();
		
		Query<OrchestrationConfiguration> ocq = ofy.query(OrchestrationConfiguration.class);
		for (OrchestrationConfiguration oc: ocq) {
			all.add(oc);
		}
		
		// if we've deleted them all, rebuild with empties
		if (all.isEmpty()) {
			compId = null;
			if (wfcf.get() != null) {
//				for (Long underwayId : wfcf.get().getUnderwayCompetitions()) {
//					id = null;
//					IOrchestrationConfiguration oc = get();
//					oc.setCompID(underwayId);
//					put(oc);
//					all.add(oc);
//				}
			}
		}
		return all;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.factory.IOrchestrationConfigurationFactory#put(net.rugby.foundation.admin.shared.IOrchestrationConfiguration)
	 */
	@Override
	public IOrchestrationConfiguration put(IOrchestrationConfiguration oc) {
		ofy.put(oc);
		return oc;
	}

}
