package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class ClubhouseMembership implements Serializable, IClubhouseMembership {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private Long id;
	private Long appUserID;
	private Long clubhouseID;
	private String userName;
	private Date joined;
	
	
	public ClubhouseMembership() {}

	public ClubhouseMembership(Long appUserID, Long clubhouseID, String userName,
			Date joined) {
		super();
		this.appUserID = appUserID;
		this.clubhouseID = clubhouseID;
		this.userName = userName;
		this.joined = joined;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouseMembership#getAppUserID()
	 */
	@Override
	public Long getAppUserID() {
		return appUserID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouseMembership#setAppUserID(java.lang.Long)
	 */
	@Override
	public void setAppUserID(Long appUserID) {
		this.appUserID = appUserID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouseMembership#getClubhouseID()
	 */
	@Override
	public Long getClubhouseID() {
		return clubhouseID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouseMembership#setLeagueID(java.lang.Long)
	 */
	@Override
	public void setClubhouseID(Long clubhouseID) {
		this.clubhouseID = clubhouseID;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouseMembership#getUserName()
	 */
	@Override
	public String getUserName() {
		return userName;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouseMembership#setUserName(java.lang.String)
	 */
	@Override
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouseMembership#getJoined()
	 */
	@Override
	public Date getJoined() {
		return joined;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouseMembership#setJoined(java.util.Date)
	 */
	@Override
	public void setJoined(Date joined) {
		this.joined = joined;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouseMembership#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IClubhouseMembership#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	

}
