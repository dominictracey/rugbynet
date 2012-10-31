package net.rugby.foundation.model.shared;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class Feature implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	Long id;
	String title;
	Long groupID;
	Boolean today;
	
	public Feature() {
		
	}
	
	public Feature(String title, Long groupID, Boolean today) {
		super();
		this.title = title;
		this.groupID = groupID;
		this.today = today;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getGroupID() {
		return groupID;
	}

	public void setGroupID(Long groupID) {
		this.groupID = groupID;
	}

	public Boolean getToday() {
		return today;
	}

	public void setToday(Boolean today) {
		this.today = today;
	}
	
	
}
