package net.rugby.foundation.admin.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class WorkflowConfiguration implements Serializable, IWorkflowConfiguration {

	private static final long serialVersionUID = 1L;
		
	@Id	
	private Long id;
		
	private List<Long> underwayCompetitions = null;
	
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
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IWorkflowConfiguration#getUnderwayCompetitions()
	 */
	@Override
	public List<Long> getUnderwayCompetitions() {
		if (underwayCompetitions == null)
			underwayCompetitions = new ArrayList<Long>();
		
		return underwayCompetitions;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IWorkflowConfiguration#setUnderwayCompetitions(java.util.ArrayList)
	 */
	@Override
	public void setUnderwayCompetitions(List<Long> underwayCompetitions) {
		this.underwayCompetitions = underwayCompetitions;
	}
	
}
