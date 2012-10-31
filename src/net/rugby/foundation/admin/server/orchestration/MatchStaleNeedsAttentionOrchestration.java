/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import com.google.appengine.api.taskqueue.TaskOptions;
import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.MatchActions;
import net.rugby.foundation.model.shared.IMatchGroup;

/**
 * @author home
 *
 */
@SuppressWarnings("deprecation")
public class MatchStaleNeedsAttentionOrchestration extends OrchestrationCore<IMatchGroup> {
	private Long compId;
	private IOrchestrationConfigurationFactory ocf;
//
//	@Inject
//	public void setFactories(IMatchGroupFactory mf, IOrchestrationConfigurationFactory ocf) {
//		this.mf = mf;
//		this.ocf = ocf;
//	}

	public MatchStaleNeedsAttentionOrchestration(IOrchestrationConfigurationFactory ocf) {
		this.ocf = ocf;
	}	

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#addParams(com.google.appengine.api.taskqueue.TaskOptions)
	 */
	@Override
	public TaskOptions addParams(TaskOptions builder) {
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
			if (conf.getMatchActions().get(MatchActions.MATCH_STALE_NEEDS_ATTENTION.getValue())) {
				AdminEmailer ae = new AdminEmailer();
				
				ae.setSubject("Match report overdue: " + target.getDisplayName());
		//		int ago;
		//		Calendar cal = new GregorianCalendar();
		//		Date now = new Date();
		//
		//		cal.setTime(target.getDate());
		//		cal.add(Calendar.HOUR, RuleMatchStaleNeedsAttention.STALE_NEED_ATTENTION_OFFSET);
		
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
		compId = id;
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#getExtraKey()
	 */
	@Override
	public Long getExtraKey() {
		return compId;
	}

}
