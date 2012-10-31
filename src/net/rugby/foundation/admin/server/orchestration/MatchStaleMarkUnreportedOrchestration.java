/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import com.google.appengine.api.taskqueue.TaskOptions;
import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.MatchActions;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.Status;

/**
 * @author home
 *
 */
@SuppressWarnings("deprecation")
public class MatchStaleMarkUnreportedOrchestration extends OrchestrationCore<IMatchGroup> {
	
	private IMatchGroupFactory mf;
	private Long compId;
	private IOrchestrationConfigurationFactory ocf;

	public MatchStaleMarkUnreportedOrchestration(IMatchGroupFactory mf, IOrchestrationConfigurationFactory ocf) {
		this.mf = mf;
		this.ocf = ocf;
	}
//	@Inject
//	public void setFactories(IMatchGroupFactory mf, IOrchestrationConfigurationFactory ocf) {
//		this.mf = mf;
//		this.ocf = ocf;
//	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#addParams(com.google.appengine.api.taskqueue.TaskOptions)
	 */
	@Override
	public TaskOptions addParams(TaskOptions builder) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#execute()
	 */
	@Override
	public void execute() {
		if (compId != null) {
			ocf.setCompId(compId);
			IOrchestrationConfiguration conf = ocf.get();
			if (conf.getMatchActions().get(MatchActions.MATCH_STALE_MARK_UNREPORTED.getValue())) {
				target.setStatus(Status.UNREPORTED);
				mf.put(target);
				AdminEmailer ae = new AdminEmailer();
				
				ae.setSubject("Match marked unreported: " + target.getDisplayName());
				ae.setMessage("We should really send you a link that you can go in and manually enter the result. The match kicked off at " + target.getDate().toString());
				ae.send();
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#setExtraKey(java.lang.Long)
	 */
	@Override
	public void setExtraKey(Long id) {
		this.compId = id;
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#getExtraKey()
	 */
	@Override
	public Long getExtraKey() {
		return compId;
	}

}
