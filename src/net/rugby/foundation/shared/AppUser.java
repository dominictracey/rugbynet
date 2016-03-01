/**
 * This is a really unfortunate thing. I moved AppUser from here in the big V3 rewrite. All the current users of the site at the time
 * (February 2012) would get a javax.servlet.ServletException: java.lang.RuntimeException: java.lang.ClassNotFoundException: net.rugby.foundation.shared.AppUser
 * error when they came back to the site with a V2 cookie still good. CoreServiceImpl now auto-logs them off.
 */
package net.rugby.foundation.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

import net.rugby.foundation.model.shared.IAppUser;

/**
 * @author home
 *
 */
@Entity
public class AppUser implements IAppUser, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
  private String emailAddress;
  private String nickname;
  private String pwHash; 
  private boolean active;
  private boolean admin;
  private boolean superadmin;
  private boolean isGoogle;
  private boolean isFacebook;
  private boolean isOAuth2;
  private boolean optOut;
  private Long lastEntryId;
  private Long lastClubhouseId;
  private Long lastCompetitionId;

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#isAdmin()
	 */
	@Override
	public boolean isAdmin() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getEmailAddress()
	 */
	@Override
	public String getEmailAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setEmailAddress(java.lang.String)
	 */
	@Override
	public void setEmailAddress(String emailAddress) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getNickname()
	 */
	@Override
	public String getNickname() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setNickname(java.lang.String)
	 */
	@Override
	public void setNickname(String nickname) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getPwHash()
	 */
	@Override
	public String getPwHash() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setPwHash(java.lang.String)
	 */
	@Override
	public void setPwHash(String pwHash) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getId()
	 */
	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setAdmin(boolean)
	 */
	@Override
	public void setAdmin(boolean admin) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#isActive()
	 */
	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setActive(boolean)
	 */
	@Override
	public void setActive(boolean active) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#isSuperadmin()
	 */
	@Override
	public boolean isSuperadmin() {
		// TODO Auto-generated method stub
		return false;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#isGoogle()
	 */
	@Override
	public boolean isOpenId() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setGoogle(boolean)
	 */
	@Override
	public void setIsOpenId(boolean setIsOpenId) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#isFacebook()
	 */
	@Override
	public boolean isFacebook() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setFacebook(boolean)
	 */
	@Override
	public void setFacebook(boolean isFacebook) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setSuperadmin(boolean)
	 */
	@Override
	public void setSuperadmin(boolean superadmin) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setOptOut(boolean)
	 */
	@Override
	public void setOptOut(boolean optOut) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getOptOut()
	 */
	@Override
	public boolean getOptOut() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setLastEntryId(java.lang.Long)
	 */
	@Override
	public void setLastEntryId(Long lastEntryId) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getLastEntryId()
	 */
	@Override
	public Long getLastEntryId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setLastClubhouseId(java.lang.Long)
	 */
	@Override
	public void setLastClubhouseId(Long lastClubhouseId) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getLastClubhouseId()
	 */
	@Override
	public Long getLastClubhouseId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setLastCompetitionId(java.lang.Long)
	 */
	@Override
	public void setLastCompetitionId(Long lastCompetitionId) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getLastCompetitionId()
	 */
	@Override
	public Long getLastCompetitionId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getFirstName()
	 */
	@Override
	public String getFirstName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setFirstName(java.lang.String)
	 */
	@Override
	public void setFirstName(String firstName) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getLastName()
	 */
	@Override
	public String getLastName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setLastName(java.lang.String)
	 */
	@Override
	public void setLastName(String lastName) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getFacebookLink()
	 */
	@Override
	public String getFacebookLink() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setFacebookLink(java.lang.String)
	 */
	@Override
	public void setFacebookLink(String facebookLink) {
		// TODO Auto-generated method stub
		
	}



	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getFbLocationName()
	 */
	@Override
	public String getFbLocationName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setFbLocationName(java.lang.String)
	 */
	@Override
	public void setFbLocationName(String fbLocationName) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getGender()
	 */
	@Override
	public String getGender() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setGender(java.lang.String)
	 */
	@Override
	public void setGender(String gender) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getTimezone()
	 */
	@Override
	public String getTimezone() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setTimezone(java.lang.String)
	 */
	@Override
	public void setTimezone(String timezone) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getLocale()
	 */
	@Override
	public String getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setLocale(java.lang.String)
	 */
	@Override
	public void setLocale(String locale) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getFbVerified()
	 */
	@Override
	public Boolean getFbVerified() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setFbVerified(java.lang.Boolean)
	 */
	@Override
	public void setFbVerified(Boolean fbVerified) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getFbUpdatedTime()
	 */
	@Override
	public Date getFbUpdatedTime() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setFbUpdatedTime(java.lang.String)
	 */
	@Override
	public void setFbUpdatedTime(Date fbUpdatedTime) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setOpenId(boolean)
	 */
	@Override
	public void setOpenId(boolean isOpenId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getFbId()
	 */
	@Override
	public Long getFbId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setFbId(java.lang.Long)
	 */
	@Override
	public void setFbId(Long fbId) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getFbName()
	 */
	@Override
	public String getFbName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setFbName(java.lang.String)
	 */
	@Override
	public void setFbName(String fbName) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setFbLocationId(java.lang.Long)
	 */
	@Override
	public void setFbLocationId(Long parsed) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getFbLocationId()
	 */
	@Override
	public Long getFbLocationId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#addFederatedIdentity(java.lang.String)
	 */
	@Override
	public void addFederatedIdentity(String identity) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getFederatedIdentities()
	 */
	@Override
	public List<String> getFederatedIdentities() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#isNative()
	 */
	@Override
	public boolean isNative() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setNative(boolean)
	 */
	@Override
	public void setNative(boolean isNative) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setMustChangePassword(boolean)
	 */
	@Override
	public void setMustChangePassword(boolean mustChangePassword) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#isMustChangePassword()
	 */
	@Override
	public boolean isMustChangePassword() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setOath2(boolean isOath2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isOath2() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EmailStatus getEmailStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEmailStatus(EmailStatus emailStatus) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getEmailValidationCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEmailValidationCode(String emailValidationCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getEmailValidated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setEmailValidated(boolean emailValidated) {
		// TODO Auto-generated method stub
		
	}

}
