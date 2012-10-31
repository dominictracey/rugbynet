/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import net.rugby.foundation.admin.server.orchestration.IOrchestration;

/**
 * @author home
 *
 */
public abstract class OrchestrationCore<T> implements IOrchestration<T> {

	protected T target;
	
	
//	/* (non-Javadoc)
//	 * @see net.rugby.foundation.admin.shared.IOrchestration#addParams(com.google.appengine.api.taskqueue.TaskOptions.Builder)
//	 */
//	@Override
//	public TaskOptions addParams(TaskOptions builder) {
//		return null; 
//	}
//
//	/* (non-Javadoc)
//	 * @see net.rugby.foundation.admin.shared.IOrchestration#execute()
//	 */
//	@Override
//	public void execute() {
//
//	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IOrchestration#setTarget(java.lang.Object)
	 */
	@Override
	public void setTarget(T target) {
		this.target = target;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IOrchestration#getTarget()
	 */
	@Override
	public T getTarget() {
		return target;
	}

}
