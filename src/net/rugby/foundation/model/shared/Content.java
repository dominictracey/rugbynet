package net.rugby.foundation.model.shared;

import javax.persistence.Id;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class Content {
	@Id
	Long id;
	String body;
	
	public Content() {}

	public String getBody() {
		
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Long getId() {
		return id;
	}

	public Content(Long id, String text) {
		super();
		this.id = id;
		this.body = text;
	}
	
}
