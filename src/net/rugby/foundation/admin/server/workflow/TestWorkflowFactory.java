/**
 * 
 */
package net.rugby.foundation.admin.server.workflow;

import java.util.ArrayList;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.rules.ICoreRuleFactory;
import net.rugby.foundation.admin.server.workflow.IWorkflow.WorkflowState;
import net.rugby.foundation.admin.shared.CompetitionWorkflow;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;

/**
 * @author home
 *
 */
public class TestWorkflowFactory implements IWorkflowFactory {

	private Long id;
	private ICompetitionFactory cf;
	private IWorkflowConfigurationFactory wfcf;
	private ICoreRuleFactory crf;
	private IMatchGroupFactory mf;
	
	@Inject
	public TestWorkflowFactory(ICompetitionFactory cf, ICoreRuleFactory crf, IMatchGroupFactory mf)
	{
		this.cf = cf;
		this.crf = crf;
		this.mf = mf;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.workflow.IWorkflowFactory#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.workflow.IWorkflowFactory#get()
	 */
	@Override
	public IWorkflow get() {
		IWorkflow wf = new CompetitionWorkflow(cf, crf, mf);
		((Workflow)wf).setId(id);
		((Workflow)wf).setState(WorkflowState.RUNNING);
		((Workflow)wf).setType(WorkflowType.COMPETITION);
		((Workflow)wf).setLog(new ArrayList<String>());
		if (id == null) {
			((CompetitionWorkflow)wf).setCompId(null);			
		} else if (id == 40001L) {
			((CompetitionWorkflow)wf).setCompId(1L);			
		}  else if (id == 40002L) {
			((CompetitionWorkflow)wf).setCompId(2L);			
		}
		
		return wf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.workflow.IWorkflowFactory#put(net.rugby.foundation.admin.server.workflow.IWorkflow)
	 */
	@Override
	public IWorkflow put(IWorkflow wf) {
		return wf;
	}

}
