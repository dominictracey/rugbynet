/**
 * 
 */
package net.rugby.foundation.admin.shared;

/**
 * @author home
 *
 */
public interface IWorkflowConfiguration {

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract void setMidweekPipelineId(String pipelineId);

	public abstract String getMidweekPipelineId();

}