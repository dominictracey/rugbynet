/**
 * 
 */
package net.rugby.foundation.admin.server.workflow;

/**
 * @author home
 *
 */
public interface IWorkflowFactory {
	
	public enum WorkflowType { COMPETITION, GAME1; }
	
	void setId(Long id);

	IWorkflow put(IWorkflow wf);
	/**
	 * @return
	 */
	IWorkflow get();
}
