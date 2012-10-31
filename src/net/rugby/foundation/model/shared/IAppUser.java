/**
 * 
 */
package net.rugby.foundation.model.shared;

import java.util.Date;
import java.util.List;

/**
 * @author home
 *
 */
public interface IAppUser {

	public abstract boolean isAdmin();

	public abstract String getEmailAddress();

	public abstract void setEmailAddress(String emailAddress);

	public abstract String getNickname();

	public abstract void setNickname(String nickname);

	public abstract String getPwHash();

	public abstract void setPwHash(String pwHash);

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract void setAdmin(boolean admin);

	public abstract boolean isActive();

	public abstract void setActive(boolean active);

	public abstract boolean isSuperadmin();

	public abstract boolean isOpenId();

	public abstract void setIsOpenId(boolean isOpenId);

	public abstract boolean isFacebook();

	public abstract void setFacebook(boolean isFacebook);

	public abstract void setSuperadmin(boolean superadmin);
	
	public abstract void setOptOut(boolean optOut);
	public abstract boolean getOptOut();

	/**
	 * @param lastEntryId
	 */
	public abstract void setLastEntryId(Long lastEntryId);
	public abstract Long getLastEntryId();

	/**
	 * @param lastClubhouseId
	 */
	public abstract void setLastClubhouseId(Long lastClubhouseId);
	public abstract Long getLastClubhouseId();

	/**
	 * @param lastCompetitionId
	 */
	public abstract void setLastCompetitionId(Long lastCompetitionId);
	public abstract Long getLastCompetitionId();

	/**
	 * @return
	 */
	String getFirstName();

	/**
	 * @param firstName
	 */
	void setFirstName(String firstName);

	/**
	 * @return
	 */
	String getLastName();

	/**
	 * @param lastName
	 */
	void setLastName(String lastName);

	/**
	 * @return
	 */
	String getFacebookLink();

	/**
	 * @param facebookLink
	 */
	void setFacebookLink(String facebookLink);

	/**
	 * @return
	 */
	Long getFbLocationId();

	/**
	 * @param parsed
	 */
	void setFbLocationId(Long parsed);

	/**
	 * @return
	 */
	String getFbLocationName();

	/**
	 * @param fbLocationName
	 */
	void setFbLocationName(String fbLocationName);

	/**
	 * @return
	 */
	String getGender();

	/**
	 * @param gender
	 */
	void setGender(String gender);

	/**
	 * @return
	 */
	String getTimezone();

	/**
	 * @param timezone
	 */
	void setTimezone(String timezone);

	/**
	 * @return
	 */
	String getLocale();

	/**
	 * @param locale
	 */
	void setLocale(String locale);

	/**
	 * @return
	 */
	Boolean getFbVerified();

	/**
	 * @param fbVerified
	 */
	void setFbVerified(Boolean fbVerified);

	/**
	 * @return
	 */
	Date getFbUpdatedTime();

	/**
	 * @param fbUpdatedTime
	 */
	void setFbUpdatedTime(Date fbUpdatedTime);

	/**
	 * @param isOpenId
	 */
	void setOpenId(boolean isOpenId);

	/**
	 * @return
	 */
	Long getFbId();

	/**
	 * @param fbId
	 */
	void setFbId(Long fbId);

	/**
	 * @return
	 */
	String getFbName();

	/**
	 * @param fbName
	 */
	void setFbName(String fbName);
	
	/**
	 * @param identity - to add
	 */
	void addFederatedIdentity(String identity);
	
	/**
	 * @return List of federated identities associated with this user
	 */
	List<String> getFederatedIdentities();

	/**
	 * @return - Is this account one with a password we keep (rather than being externally authenticated)
	 */
	boolean isNative();

	/**
	 * @param isNative
	 */
	void setNative(boolean isNative);

	/**
	 * @param mustChangePassword
	 */
	void setMustChangePassword(boolean mustChangePassword);

	/**
	 * @return
	 */
	boolean isMustChangePassword();

}