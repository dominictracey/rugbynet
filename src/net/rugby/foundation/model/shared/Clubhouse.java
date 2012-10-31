package net.rugby.foundation.model.shared;

import java.io.Serializable;
import javax.persistence.Id;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class Clubhouse implements Serializable, IClubhouse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private Long id;
	private String name;
	private String description;
	private Long lastWinnerID;
	private Long homeID;
	private Long ownerID;
	private String joinLink;
	private Boolean active;
	private Boolean publicClubhouse;
	
	public Clubhouse() {}

	public Clubhouse(String name, String description, Long lastWinnerID,
			Long homeID, Long ownerID, String joinLink) {
		super();
		this.name = name;
		this.description = description;
		this.lastWinnerID = lastWinnerID;
		this.homeID = homeID;
		this.ownerID = ownerID;
		this.joinLink = joinLink;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouse#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouse#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouse#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouse#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouse#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouse#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouse#getLastWinnerID()
	 */
	@Override
	public Long getLastWinnerID() {
		return lastWinnerID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouse#setLastWinnerID(java.lang.Long)
	 */
	@Override
	public void setLastWinnerID(Long lastWinnerID) {
		this.lastWinnerID = lastWinnerID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouse#getHomeID()
	 */
	@Override
	public Long getHomeID() {
		return homeID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouse#setHomeID(java.lang.Long)
	 */
	@Override
	public void setHomeID(Long homeID) {
		this.homeID = homeID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouse#getOwnerID()
	 */
	@Override
	public Long getOwnerID() {
		return ownerID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouse#setOwnerID(java.lang.Long)
	 */
	@Override
	public void setOwnerID(Long ownerID) {
		this.ownerID = ownerID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouse#getJoinLink()
	 */
	@Override
	public String getJoinLink() {
		return joinLink;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouse#setJoinLink(java.lang.String)
	 */
	@Override
	public void setJoinLink(String joinLink) {
		this.joinLink = joinLink;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouse#getActive()
	 */
	@Override
	public Boolean getActive() {
		return active;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouse#setActive(java.lang.Boolean)
	 */
	@Override
	public void setActive(Boolean active) {
		this.active = active;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouse#setPublicClubhouse(java.lang.Boolean)
	 */
	@Override
	public void setPublicClubhouse(Boolean publicClubhouse) {
		this.publicClubhouse = publicClubhouse;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouse#setPublicClubhouse()
	 */
	@Override
	public Boolean setPublicClubhouse() {
		return publicClubhouse;
	}

}
