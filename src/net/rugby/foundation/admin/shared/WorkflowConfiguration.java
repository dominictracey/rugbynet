package net.rugby.foundation.admin.shared;

import java.io.Serializable;

import javax.persistence.Id;

import net.rugby.foundation.model.shared.IHasId;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class WorkflowConfiguration implements Serializable, IWorkflowConfiguration, IHasId {

	private static final long serialVersionUID = 1L;
		
	@Id	
	private Long id;
		
	private String pipelineId;
	
	// Games for each comp
	public WorkflowConfiguration() {
		
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IWorkflowConfiguration#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IWorkflowConfiguration#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public String getMidweekPipelineId() {
		return pipelineId;
	}
	@Override
	public void setMidweekPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}
	
	
}
