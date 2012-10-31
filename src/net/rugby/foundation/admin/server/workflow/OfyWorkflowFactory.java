/**
 * 
 */
package net.rugby.foundation.admin.server.workflow;

import net.rugby.foundation.admin.server.rules.ICoreRuleFactory;
import net.rugby.foundation.admin.shared.CompetitionWorkflow;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

/**
 * @author home
 *
 */
public class OfyWorkflowFactory implements IWorkflowFactory {

	private Long id;
	private Objectify ofy;
	private ICompetitionFactory cf;
	private IWorkflowConfigurationFactory wfcf;
	private ICoreRuleFactory crf;
	private IMatchGroupFactory mf;
	
	@Inject
	public OfyWorkflowFactory(ICompetitionFactory cf, ICoreRuleFactory crf, IMatchGroupFactory mf)
	{
		ofy = DataStoreFactory.getOfy();
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
		if (id == null) {
			return new CompetitionWorkflow(cf, crf, mf);
		}
		Key<Workflow> key = new Key<Workflow>(Workflow.class,id);
		IWorkflow  wf = ofy.get(key);
		return wf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.workflow.IWorkflowFactory#put(net.rugby.foundation.admin.server.workflow.IWorkflow)
	 */
	@Override
	public IWorkflow put(IWorkflow wf) {
		wf = (IWorkflow) ofy.put(wf);
		return wf;
	}

}
