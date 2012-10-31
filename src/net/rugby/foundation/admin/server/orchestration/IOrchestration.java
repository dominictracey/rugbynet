/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * @author Dominic Tracey
 *
 */

@SuppressWarnings("deprecation")
public interface IOrchestration<T> {
	/**
	 * Used by the Workflow to put the orchestration on the queue for execution
	 * @param builder 
	 * @return builder
	 * @see {@link com.google.appengine.api.taskqueue.TaskOptions.Builder}
	 */
	TaskOptions addParams(TaskOptions builder);
	/**
	 * Called by the Orchestration servlet to invoke activity
	 */
	void execute();
	
	void setExtraKey(Long id);
	Long getExtraKey();
	
	void setTarget(T target);
	T getTarget();
}

