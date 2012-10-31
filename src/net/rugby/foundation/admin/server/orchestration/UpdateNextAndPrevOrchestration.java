/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import com.google.appengine.api.taskqueue.TaskOptions;

import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.CompActions;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.model.shared.ICompetition;

/**
 * @author home
 *
 */
@SuppressWarnings("deprecation")
public class UpdateNextAndPrevOrchestration extends OrchestrationCore<ICompetition> {
	
	private ICompetitionFactory cf;
	private IOrchestrationConfigurationFactory ocf;

	public UpdateNextAndPrevOrchestration(ICompetitionFactory cf, IOrchestrationConfigurationFactory ocf) {
		this.cf = cf;
		this.ocf = ocf;
		
	}
//	@Inject 
//	public void setFactories(ICompetitionFactory cf, IOrchestrationConfigurationFactory ocf) {
//		this.cf = cf;
//		this.ocf = ocf;
//		
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
		try {
			if (target.getId() != null) {
				ocf.setCompId(target.getId());
				IOrchestrationConfiguration conf = ocf.get();
				if (conf.getCompActions().get(CompActions.UPDATENEXTANDPREV.getValue())) {
	
					target.setPrevRoundIndex(target.getPrevRoundIndex()+1);
					if (target.getRoundIds().size() > target.getNextRound().getOrdinal()) {
						target.setNextRoundIndex(target.getNextRoundIndex()+1);
					} else {
						target.setNextRoundIndex(-1);
					}
					
					cf.put(target);
				}
			}
		} catch (Throwable caught) {
			String subj = "ERROR! Updating next and previous rounds: ";
			if (target != null) {
				subj += target.getLongName() + "(" + target.getId() + ")";
			}
			AdminEmailer ae = new AdminEmailer();
			ae.setSubject(subj);
			ae.setMessage(caught.getMessage());
			ae.send();	
			
			System.out.println(subj + caught.getLocalizedMessage());
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
