/**
 * 
 */
package net.rugby.foundation.game1.server.BPM;

import com.google.appengine.api.taskqueue.TaskOptions;

import net.rugby.foundation.admin.server.orchestration.OrchestrationCore;
import net.rugby.foundation.model.shared.IAppUser;

/**
 * @author home
 *
 */
@SuppressWarnings("deprecation")
public class OrchestrationSendReminder extends OrchestrationCore<IAppUser> {

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
//		Game1Emailer emailer = new Game1Emailer();
//		
//		emailer.setUser(target);
//		emailer.setSubject(target.getNickname());
//		emailer.setMessage("Come back now!");
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#setExtraKey(java.lang.Long)
	 */
	@Override
	public void setExtraKey(Long id) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#getExtraKey()
	 */
	@Override
	public Long getExtraKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
