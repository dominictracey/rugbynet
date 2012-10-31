/**
 * 
 */
package net.rugby.foundation.admin.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Serialized;

/**
 * @author Dominic Tracey
 *<br>
 *	Provides information to orchestration layer on how things should be processed.
 */
@Entity
public class OrchestrationConfiguration implements Serializable, IOrchestrationConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private Long id;
	private Long compID; 
	private boolean simpleMatchResultScrum = false;
	private String adminEmail = "dominic.tracey@gmail.com";
	
	@Serialized
	HashMap<String, Boolean> matchActions = new HashMap<String, Boolean>();
	
	@Serialized
	HashMap<String, Boolean> compActions = new HashMap<String, Boolean>();

	public OrchestrationConfiguration() {
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IOrchestrationConfiguration#getCompID()
	 */
	@Override
	public Long getCompID() {
		return compID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IOrchestrationConfiguration#setCompID(java.lang.Long)
	 */
	@Override
	public void setCompID(Long compID) {
		this.compID = compID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IOrchestrationConfiguration#isSimpleMatchResultScrum()
	 */
	@Override
	public boolean isSimpleMatchResultScrum() {
		return simpleMatchResultScrum;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IOrchestrationConfiguration#setSimpleMatchResultScrum(boolean)
	 */
	@Override
	public void setSimpleMatchResultScrum(boolean simpleMatchResultScrum) {
		this.simpleMatchResultScrum = simpleMatchResultScrum;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IOrchestrationConfiguration#getAdminEmail()
	 */
	@Override
	public String getAdminEmail() {
		return adminEmail;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IOrchestrationConfiguration#setAdminEmail(java.lang.String)
	 */
	@Override
	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IOrchestrationConfiguration#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IOrchestrationConfiguration#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public Map<String, Boolean> getMatchActions() {
		return matchActions;
	}
	@Override
	public void setMatchActions(HashMap<String, Boolean> matchActions) {
		this.matchActions = matchActions;
	}
	@Override
	public Map<String, Boolean> getCompActions() {
		return compActions;
	}
	@Override
	public void setCompActions(HashMap<String, Boolean> compActions) {
		this.compActions = compActions;
	}
	
}
