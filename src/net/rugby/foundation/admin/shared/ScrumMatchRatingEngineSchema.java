package net.rugby.foundation.admin.shared;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;


import com.googlecode.objectify.annotation.Entity;

@Entity
public class ScrumMatchRatingEngineSchema implements
		IRatingEngineSchema, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7944894374615161059L;
	@Id
	protected Long id;
	protected String name;
	protected String description;
	protected String changeLog;
	protected Boolean isDefault;
	protected Date created;
	
	public ScrumMatchRatingEngineSchema() {
		
	}
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setChangeLog(String changeLog) {
		this.changeLog = changeLog;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setName(String schemaName) {
		this.name = schemaName;
	}

	@Override
	public Boolean getIsDefault() {
		return isDefault;
	}

	@Override
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	@Override
	public String getChangeLog() {
		return changeLog;

	}

	@Override
	public void setCreated(Date date) {
		this.created = date;
		
	}

	@Override
	public Date getCreated() {
		return created;
	}


}
