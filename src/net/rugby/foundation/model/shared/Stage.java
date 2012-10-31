package net.rugby.foundation.model.shared;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class Stage {
	
	private Long id;
	public enum stageType { POOL, KNOCKOUT }
	
	public Stage() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
