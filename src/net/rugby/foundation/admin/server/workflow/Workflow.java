/**
 * 
 */
package net.rugby.foundation.admin.server.workflow;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Id;

import net.rugby.foundation.admin.server.workflow.IWorkflowFactory.WorkflowType;

import com.googlecode.objectify.annotation.Entity;

/**
 * @author home
 * @param <T>
 *
 */
@Entity
public class Workflow implements IWorkflow, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	private WorkflowState state;
	protected Long compId;
	private WorkflowType type;
	private List<String> log = null;
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.workflow.IWorkflow#getLog()
	 */
	@Override
	public List<String> getLog() {
		return log;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.workflow.IWorkflow#setLog(java.util.List)
	 */
	@Override
	public void setLog(List<String> log) {
		this.log = log;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.workflow.IWorkflow#process(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void process(String event, String targetKey, String secondaryKey) {
		// over-ride this
		
	}

	public WorkflowState getState() {
		return state;
	}

	public void setState(WorkflowState state) {
		this.state = state;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCompId() {
		return compId;
	}

	public void setCompId(Long compId) {
		this.compId = compId;
	}

	public WorkflowType getType() {
		return type;
	}

	public void setType(WorkflowType type) {
		this.type = type;
	}

}
