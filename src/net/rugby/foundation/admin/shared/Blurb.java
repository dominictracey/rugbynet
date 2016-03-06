package net.rugby.foundation.admin.shared;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Transient;

import net.rugby.foundation.model.shared.IServerPlace;

import org.joda.time.DateTime;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class Blurb implements IBlurb, Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4793702500418968067L;
	@Id
	protected Long id;
	protected Date created;
	protected Long serverPlaceId;
	@Transient 
	protected IServerPlace serverPlace;
	protected String bodyText;
	protected String linkText;
	protected Long authorId;
	protected Boolean active;
	
	public Blurb(Long id, Date created, Long serverPlaceId,
			IServerPlace serverPlace, String bodyText, String linkText,
			Long authorId, Boolean active) {
		super();
		this.id = id;
		this.created = created;
		this.serverPlaceId = serverPlaceId;
		this.serverPlace = serverPlace;
		this.bodyText = bodyText;
		this.linkText = linkText;
		this.authorId = authorId;
		this.active = active;
	}
	
	public Blurb() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IBlurb#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IBlurb#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IBlurb#getCreated()
	 */
	@Override
	public Date getCreated() {
		return created;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IBlurb#setCreated(org.joda.time.DateTime)
	 */
	@Override
	public void setCreated(Date created) {
		this.created = created;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IBlurb#getServerPlaceId()
	 */
	@Override
	public Long getServerPlaceId() {
		return serverPlaceId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IBlurb#setServerPlaceId(java.lang.Long)
	 */
	@Override
	public void setServerPlaceId(Long serverPlaceId) {
		this.serverPlaceId = serverPlaceId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IBlurb#getServerPlace()
	 */
	@Override
	public IServerPlace getServerPlace() {
		return serverPlace;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IBlurb#setServerPlace(net.rugby.foundation.model.shared.IServerPlace)
	 */
	@Override
	public void setServerPlace(IServerPlace serverPlace) {
		this.serverPlace = serverPlace;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IBlurb#getBodyText()
	 */
	@Override
	public String getBodyText() {
		return bodyText;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IBlurb#setBodyText(java.lang.String)
	 */
	@Override
	public void setBodyText(String bodyText) {
		this.bodyText = bodyText;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IBlurb#getLinkText()
	 */
	@Override
	public String getLinkText() {
		return linkText;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IBlurb#setLinkText(java.lang.String)
	 */
	@Override
	public void setLinkText(String linkText) {
		this.linkText = linkText;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IBlurb#getAuthorId()
	 */
	@Override
	public Long getAuthorId() {
		return authorId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IBlurb#setAuthorId(java.lang.Long)
	 */
	@Override
	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}
	@Override
	public Boolean getActive() {
		return active;
	}
	@Override
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	
}
