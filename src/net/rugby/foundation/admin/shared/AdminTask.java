package net.rugby.foundation.admin.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class AdminTask implements Serializable, IAdminTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4636473519481469768L;
	
	public AdminTask() {
		
	}
	
	
	
	public AdminTask(Long id, Action action, Long adminId, Date created,
			Date completed, Status status, Priority priority, String summary,
			String details, List<String> log, String promise,
			String pipelineRoot, String pipelineJob) {
		super();
		this.id = id;
		this.action = action;
		this.adminId = adminId;
		this.created = created;
		this.completed = completed;
		this.status = status;
		this.priority = priority;
		this.summary = summary;
		this.details = details;
		this.log = log;
		this.promise = promise;
		this.pipelineRoot = pipelineRoot;
		this.pipelineJob = pipelineJob;
	}



	@Id
	private Long id;
	private Action action;
	private Long adminId;
	private Date created;
	private Date completed;
	private Status status;
	private Priority priority;
	private String summary;
	private String details;
	private List<String> log;
	
	private String promise;
	private String pipelineRoot;
	private String pipelineJob;
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#getAction()
	 */
	@Override
	public Action getAction() {
		return action;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#setAction(net.rugby.foundation.admin.server.model.AdminTask.Action)
	 */
	@Override
	public void setAction(Action action) {
		this.action = action;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#getAdminId()
	 */
	@Override
	public Long getAdminId() {
		return adminId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#setAdminId(java.lang.Long)
	 */
	@Override
	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#getCreated()
	 */
	@Override
	public Date getCreated() {
		return created;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#setCreated(java.util.Date)
	 */
	@Override
	public void setCreated(Date created) {
		this.created = created;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#getCompleted()
	 */
	@Override
	public Date getCompleted() {
		return completed;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#setCompleted(java.util.Date)
	 */
	@Override
	public void setCompleted(Date completed) {
		this.completed = completed;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#getStatus()
	 */
	@Override
	public Status getStatus() {
		return status;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#setStatus(net.rugby.foundation.admin.server.model.AdminTask.Status)
	 */
	@Override
	public void setStatus(Status status) {
		this.status = status;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#getPriority()
	 */
	@Override
	public Priority getPriority() {
		return priority;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#setPriority(net.rugby.foundation.admin.server.model.AdminTask.Priority)
	 */
	@Override
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#getSummary()
	 */
	@Override
	public String getSummary() {
		return summary;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#setSummary(java.lang.String)
	 */
	@Override
	public void setSummary(String summary) {
		this.summary = summary;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#getDetails()
	 */
	@Override
	public String getDetails() {
		return details;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#setDetails(java.lang.String)
	 */
	@Override
	public void setDetails(String details) {
		this.details = details;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#getLog()
	 */
	@Override
	public List<String> getLog() {
		return log;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#setLog(java.util.List)
	 */
	@Override
	public void setLog(List<String> log) {
		this.log = log;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#getPromise()
	 */
	@Override
	public String getPromise() {
		return promise;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#setPromise(java.lang.String)
	 */
	@Override
	public void setPromise(String promise) {
		this.promise = promise;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#getPipelineRoot()
	 */
	@Override
	public String getPipelineRoot() {
		return pipelineRoot;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#setPipelineRoot(java.lang.String)
	 */
	@Override
	public void setPipelineRoot(String pipelineRoot) {
		this.pipelineRoot = pipelineRoot;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#getPipelineJob()
	 */
	@Override
	public String getPipelineJob() {
		return pipelineJob;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IAdminTask#setPipelineJob(java.lang.String)
	 */
	@Override
	public void setPipelineJob(String pipelineJob) {
		this.pipelineJob = pipelineJob;
	}
	@Override
	public void addLogItem(String logThis) {
		if (log == null) {
			log = new ArrayList<String>();
		}
		
		log.add(logThis);
	}
	@Override
	public String getPipelineUrl() {
		return "/_ah/pipeline/status.html?root=" + pipelineRoot + "#pipeline-" + pipelineJob;
	}
	
	
	
}
