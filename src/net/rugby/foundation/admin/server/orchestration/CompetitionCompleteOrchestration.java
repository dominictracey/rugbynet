/**
 * 
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import net.rugby.foundation.admin.server.workflow.IWorkflowConfigurationFactory;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.CompActions;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.model.shared.ICompetition;

import com.google.appengine.api.taskqueue.TaskOptions;


/**
 * @author home
 *
 */
public class CompetitionCompleteOrchestration extends OrchestrationCore<ICompetition> {
	private ICompetitionFactory cf;
	private IWorkflowConfigurationFactory wfcf;
	private IOrchestrationConfigurationFactory ocf;

	public CompetitionCompleteOrchestration(ICompetitionFactory cf, IWorkflowConfigurationFactory wfcf, IOrchestrationConfigurationFactory ocf) {
		this.cf = cf;
		this.wfcf = wfcf;
		this.ocf = ocf;
	}
//	@Inject 
//	public void setFactories(ICompetitionFactory cf, IWorkflowConfigurationFactory wfcf, IOrchestrationConfigurationFactory ocf) {
//		this.cf = cf;
//		this.wfcf = wfcf;
//		this.ocf = ocf;
//	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IOrchestration#addParams(com.google.appengine.api.taskqueue.TaskOptions)
	 */
	@Override
	public TaskOptions addParams(TaskOptions builder) {
		return builder.param("id",target.getId().toString());
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IOrchestration#execute()
	 */
	@Override
	public void execute() {
		if (target.getId() != null) {
			ocf.setCompId(target.getId());
			IOrchestrationConfiguration conf = ocf.get();
			if (conf.getCompActions().get(CompActions.COMP_COMPLETE.getValue())) {

				target.setUnderway(false);
				cf.put(target);
				
//				// take it out of the workflow configuration as well
//				IWorkflowConfiguration wfc = wfcf.get();
////				wfc.getUnderwayCompetitions().remove(target.getId());
//				wfcf.put(wfc);
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#setExtraKey(java.lang.Long)
	 */
	@Override
	public void setExtraKey(Long id) {
		// not needed
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#getExtraKey()
	 */
	@Override
	public Long getExtraKey() {
		// not needed
		return null;
	}

}
