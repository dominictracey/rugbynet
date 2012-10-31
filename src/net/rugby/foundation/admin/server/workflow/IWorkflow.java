/**
 * 
 */
package net.rugby.foundation.admin.server.workflow;

import java.util.List;

/**
 * @author Dominic Tracey
 * 
 * Can be kicked off from the WorkFlowServlet or JUnit test
 *
 */
public interface IWorkflow {

	enum WorkflowState { RUNNING, SUSPENDED, COMPLETE; }
	
	List<String> getLog();
	void setLog(List<String> log);
	/**
	 * @param event
	 * @param secondaryKey 
	 * @param targetKey 
	 */
	void process(String event, String targetKey, String secondaryKey);
}
