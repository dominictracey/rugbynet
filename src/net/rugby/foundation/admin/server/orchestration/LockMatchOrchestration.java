/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.inject.Inject;

import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.MatchActions;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.game1.server.BPM.RuleCheckLeague;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.Status;

/**
 * @author home
 *
 */
@SuppressWarnings("deprecation")
public class LockMatchOrchestration extends OrchestrationCore<IMatchGroup> {
	
	private IMatchGroupFactory mf;
	private IOrchestrationConfigurationFactory ocf;
	private Long compId;

//	@Inject
//	public void setFactories(IMatchGroupFactory mf, IOrchestrationConfigurationFactory ocf) {
//		this.mf = mf;
//		this.ocf = ocf;
//	}
	
	public LockMatchOrchestration(IMatchGroupFactory mf, IOrchestrationConfigurationFactory ocf) {
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
				if (!target.getLocked()) {
					target.setLocked(true);
					target.setStatus(Status.UNDERWAY_FIRST_HALF);
					mf.put(target);
					AdminEmailer ae = new AdminEmailer();
					ae.setSubject("Match locked: " + target.getDisplayName() + "(" + target.getId() + ")");
					ae.setMessage("It is underway");
					ae.send();
					Logger.getLogger(LockMatchOrchestration.class.getName()).log(Level.WARNING,"Match locked for match " + target.getDisplayName() +"("+ target.getId() + ")");
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
