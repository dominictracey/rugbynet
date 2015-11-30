package net.rugby.foundation.topten.model.shared;

import java.io.Serializable;
import java.util.Date;

import net.rugby.foundation.model.shared.IServerPlace;

public class Feature implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9151064316877908756L;
	
	protected IServerPlace sPlace;
	protected Date published;
	protected String title;
	
	
	public Feature() {
		
	}
	
	/**
	 * @param sPlace
	 * @param published
	 * @param title
	 */
	public Feature(IServerPlace sPlace, Date published, String title) {
		super();
		this.sPlace = sPlace;
		this.published = published;
		this.title = title;
	}
	
	public IServerPlace getsPlace() {
		return sPlace;
	}
	public void setsPlace(IServerPlace sPlace) {
		this.sPlace = sPlace;
	}
	public Date getPublished() {
		return published;
	}
	public void setPublished(Date published) {
		this.published = published;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
