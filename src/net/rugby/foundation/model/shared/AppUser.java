package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;


import com.googlecode.objectify.annotation.Entity;

@Entity
public class AppUser implements Serializable, IAppUser, ITopTenUser {

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
	private boolean isNative;
	private boolean isOpenId;
	private boolean isFacebook;
	private boolean isOath2;
	private boolean optOut;
	private Long lastEntryId;
	private Long lastClubhouseId;
	private Long lastCompetitionId;

	// new from Fasebuk
	private String firstName;
	private String lastName;
	private Long fbId;
	private String fbName;
	private String facebookLink;
	private Long fbLocationId;
	private String fbLocationName;
	private String gender;
	private String timezone;
	private String locale;
	private Boolean fbVerified;
	private Date fbUpdatedTime;
	private List<String> federatedIdentities;
	
	//TopTenUser
	private boolean isTopTenContentContributor;
	private boolean isTopTenContentEditor;
	
	private boolean mustChangePassword;

	// mail
	private EmailStatus emailStatus;
	private String emailValidationCode;
	private boolean emailValidated;
	
	public AppUser() {

	}
	public AppUser(String emailAddress, String nickname, String pwHash,
			boolean active, boolean admin, boolean superadmin, boolean isOpenId, boolean isFacebook, boolean isNative, List<String> federatedIdentities) {
		super();
		this.emailAddress = emailAddress;
		this.nickname = nickname;
		this.pwHash = pwHash;
		this.active = active;
		this.admin = admin;
		this.superadmin = superadmin;
		this.isOpenId = isOpenId;
		this.isFacebook = isFacebook;
		this.setNative(isNative);
		this.federatedIdentities = federatedIdentities;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getEmailAddress()
	 */
	@Override
	public String getEmailAddress() {
		return emailAddress;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setEmailAddress(java.lang.String)
	 */
	@Override
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getNickname()
	 */
	@Override
	public String getNickname() {
		return nickname;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setNickname(java.lang.String)
	 */
	@Override
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getPwHash()
	 */
	@Override
	public String getPwHash() {
		return pwHash;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setPwHash(java.lang.String)
	 */
	@Override
	public void setPwHash(String pwHash) {
		this.pwHash = pwHash;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setAdmin(boolean)
	 */
	@Override
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#isAdmin()
	 */
	@Override
	public boolean isAdmin() {
		return admin;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#isActive()
	 */
	@Override
	public boolean isActive() {
		return active;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setActive(boolean)
	 */
	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#isSuperadmin()
	 */
	@Override
	public boolean isSuperadmin() {
		return superadmin;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#isGoogle()
	 */
	@Override
	public boolean isOpenId() {
		return isOpenId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setGoogle(boolean)
	 */
	@Override
	public void setIsOpenId(boolean isOpenId) {
		this.isOpenId = isOpenId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#isFacebook()
	 */
	@Override
	public boolean isFacebook() {
		return isFacebook;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setFacebook(boolean)
	 */
	@Override
	public void setFacebook(boolean isFacebook) {
		this.isFacebook = isFacebook;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setSuperadmin(boolean)
	 */
	@Override
	public void setSuperadmin(boolean superadmin) {
		this.superadmin = superadmin;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setOptOut(boolean)
	 */
	@Override
	public void setOptOut(boolean optOut) {
		this.optOut = optOut;

	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getOptOut()
	 */
	@Override
	public boolean getOptOut() {
		return optOut;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setLastEntryId(java.lang.Long)
	 */
	@Override
	public void setLastEntryId(Long lastEntryId) {
		this.lastEntryId = lastEntryId;

	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getLastEntryId()
	 */
	@Override
	public Long getLastEntryId() {
		return lastEntryId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setLastClubhouseId(java.lang.Long)
	 */
	@Override
	public void setLastClubhouseId(Long lastClubhouseId) {
		this.lastClubhouseId = lastClubhouseId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getLastClubhouseId()
	 */
	@Override
	public Long getLastClubhouseId() {
		return lastClubhouseId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#setLastCompetitionId(java.lang.Long)
	 */
	@Override
	public void setLastCompetitionId(Long lastCompetitionId) {
		this.lastCompetitionId = lastCompetitionId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getLastCompetitionId()
	 */
	@Override
	public Long getLastCompetitionId() {
		return lastCompetitionId;
	}
	@Override
	public String getFirstName() {
		return firstName;
	}
	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	@Override
	public String getLastName() {
		return lastName;
	}
	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	@Override
	public String getFacebookLink() {
		return facebookLink;
	}
	@Override
	public void setFacebookLink(String facebookLink) {
		this.facebookLink = facebookLink;
	}
	@Override
	public Long getFbLocationId() {
		return fbLocationId;
	}
	@Override
	public void setFbLocationId(Long fbLocationId) {
		this.fbLocationId = fbLocationId;
	}
	@Override
	public String getFbLocationName() {
		return fbLocationName;
	}
	@Override
	public void setFbLocationName(String fbLocationName) {
		this.fbLocationName = fbLocationName;
	}
	@Override
	public String getGender() {
		return gender;
	}
	@Override
	public void setGender(String gender) {
		this.gender = gender;
	}
	@Override
	public String getTimezone() {
		return timezone;
	}
	@Override
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	@Override
	public String getLocale() {
		return locale;
	}
	@Override
	public void setLocale(String locale) {
		this.locale = locale;
	}
	@Override
	public Boolean getFbVerified() {
		return fbVerified;
	}
	@Override
	public void setFbVerified(Boolean fbVerified) {
		this.fbVerified = fbVerified;
	}
	@Override
	public Date getFbUpdatedTime() {
		return fbUpdatedTime;
	}
	@Override
	public void setFbUpdatedTime(Date fbUpdatedTime) {
		this.fbUpdatedTime = fbUpdatedTime;
	}
	@Override
	public void setOpenId(boolean isOpenId) {
		this.isOpenId = isOpenId;
	}
	@Override
	public Long getFbId() {
		return fbId;
	}
	@Override
	public void setFbId(Long fbId) {
		this.fbId = fbId;
	}
	@Override
	public String getFbName() {
		return fbName;
	}
	@Override
	public void setFbName(String fbName) {
		this.fbName = fbName;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#addFederatedIdentity(java.lang.String)
	 */
	@Override
	public void addFederatedIdentity(String identity) {
		if (federatedIdentities == null) {
			federatedIdentities = new ArrayList<String>();
		}
		federatedIdentities.add(identity);

	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IAppUser#getFederatedIdentities()
	 */
	@Override
	public final List<String> getFederatedIdentities() {
		if (federatedIdentities == null) {
			federatedIdentities = new ArrayList<String>();
		}
		return federatedIdentities;
	}
	@Override
	public boolean isNative() {
		return isNative;
	}
	@Override
	public void setNative(boolean isNative) {
		this.isNative = isNative;
	}
	
	@Override
	public boolean isMustChangePassword() {
		return mustChangePassword;
	}
	
	@Override
	public void setMustChangePassword(boolean mustChangePassword) {
		this.mustChangePassword = mustChangePassword;
	}
	
	@Override
	public boolean isTopTenContentContributor() {
		return isTopTenContentContributor;
	}
	
	@Override
	public void setTopTenContentContributor(boolean isTopTenContentContributor) {
		this.isTopTenContentContributor = isTopTenContentContributor;
	}
	
	@Override
	public boolean isTopTenContentEditor() {
		return isTopTenContentEditor;
	}
	
	@Override
	public void setTopTenContentEditor(boolean isTopTenContentEditor) {
		this.isTopTenContentEditor = isTopTenContentEditor;
	}
	
	@Override
	public boolean isOath2() {
		return isOath2;
	}
	
	@Override
	public void setOath2(boolean isOath2) {
		this.isOath2 = isOath2;
	}
	
	@Override
	public EmailStatus getEmailStatus() {
		return emailStatus;
	}
	
	@Override
	public void setEmailStatus(EmailStatus emailStatus) {
		this.emailStatus = emailStatus;
	}
	@Override
	public String getEmailValidationCode() {
		return emailValidationCode;
	}
	@Override
	public void setEmailValidationCode(String emailValidationCode) {
		this.emailValidationCode = emailValidationCode;
	}
	@Override
	public boolean getEmailValidated() {
		return emailValidated;
	}
	@Override
	public void setEmailValidated(boolean emailValidated) {
		this.emailValidated = emailValidated;
	}

}
