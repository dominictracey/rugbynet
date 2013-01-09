/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import java.util.logging.Level;
import java.util.logging.Logger;

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
public class UnlockMatchOrchestration extends OrchestrationCore<IMatchGroup> {
	
	private IMatchGroupFactory mf;
	private IOrchestrationConfigurationFactory ocf;
	private Long compId;
//
//	@Inject
//	public void setFactories(IMatchGroupFactory mf, IOrchestrationConfigurationFactory ocf) {
//		this.mf = mf;
//		this.ocf = ocf;
//	}
//	
	public UnlockMatchOrchestration(IMatchGroupFactory mf, IOrchestrationConfigurationFactory ocf) {
		this.mf = mf;
		this.ocf = ocf;
	}
	
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
		if (compId != null) {
			ocf.setCompId(compId);
			IOrchestrationConfiguration conf = ocf.get();
			if (conf.getMatchActions().get(MatchActions.LOCK.getValue())) {
				if (target.getLocked()) {
					target.setLocked(false);
					target.setStatus(Status.SCHEDULED);
					mf.put(target);
					AdminEmailer ae = new AdminEmailer();
					ae.setSubject("Match unlocked: " + target.getDisplayName() + "(" + target.getId() + ")");
					ae.setMessage("It is set back to scheduled");
					ae.send();
					Logger.getLogger(UnlockMatchOrchestration.class.getName()).log(Level.WARNING,"Match unlocked for match " + target.getDisplayName() +"("+ target.getId() + ")");
				}
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
