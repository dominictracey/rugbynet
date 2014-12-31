package net.rugby.foundation.model.shared;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

@Entity
public class Sponsor implements IHasId, Serializable, ISponsor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9034183730566519162L;
	@Id
	protected Long id;
	@Unindexed
	protected String tagline;
	protected String abbr;
	@Unindexed
	protected String name;
	@Unindexed
	protected String email;
	@Unindexed
	protected String contactName;
	protected boolean active;
	
	@Override
	public boolean isActive() {
		return active;
	}
	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ISponsor#getTagline()
	 */
	@Override
	public String getTagline() {
		return tagline;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ISponsor#setTagline(java.lang.String)
	 */
	@Override
	public void setTagline(String tagline) {
		this.tagline = tagline;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ISponsor#getAbbr()
	 */
	@Override
	public String getAbbr() {
		return abbr;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ISponsor#setAbbr(java.lang.String)
	 */
	@Override
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ISponsor#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ISponsor#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ISponsor#getEmail()
	 */
	@Override
	public String getEmail() {
		return email;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ISponsor#setEmail(java.lang.String)
	 */
	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ISponsor#getContactName()
	 */
	@Override
	public String getContactName() {
		return contactName;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ISponsor#setContactName(java.lang.String)
	 */
	@Override
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	@Override
	public void setId(Long id) {
		this.id = id;

	}

	@Override
	public Long getId() {
		return id;
	}
	
	

}
