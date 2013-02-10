package net.rugby.foundation.admin.shared;

import java.io.Serializable;

import javax.persistence.Id;


import com.googlecode.objectify.annotation.Entity;

@Entity
public class ScrumMatchRatingEngineSchema20130121 implements
		IMatchRatingEngineSchema, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7944894374615161059L;
	@Id
	Long id;
	
	public ScrumMatchRatingEngineSchema20130121() {
		
	}
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getSchemaName() {
		return "2013012";
	}

	@Override
	public String getSchemaDescription() {
		return "Initial revision";
	}

	@Override
	public String getSchemaChangeLog() {
		return "Everybody is a winner/loser";

	}

}
