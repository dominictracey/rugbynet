package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class LoginInfo implements Serializable, ITopTenRoleProvider {

	public static enum ProviderType { openid, facebook, oauth2 }

	public static enum Selector { google, googlePlus, yahoo, myspace, aol, myopenid_com }

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean loggedIn = false;
	private String loginUrl;
	private String logoutUrl;
	private String signupUrl;
	private String emailAddress;
	private String nickname;
	private boolean isOpenId;
	private boolean isFacebook;
	private boolean isAdmin;
	private String status;
	private Long draftID = 0L;
	private Long lastEntryId = 0L;
	private Long lastClubhouseId = 0L;
	private Long lastCompetitionId = 0L;
	private LoginInfo.ProviderType providerType = null;
	private LoginInfo.Selector selector = null;
	private boolean mustChangePassword = false;

	private boolean isTopTenContentContributor = false;
	private boolean isTopTenContentEditor = false;
	
	public boolean isAdmin() {
		return isAdmin;
	}

	private List<Long> teamIDs = new ArrayList<Long>();  //lineups for rounds
	private List<Boolean> roundsComplete = new ArrayList<Boolean>();

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public boolean isOpenId() {
		return isOpenId;
	}

	public void setIsOpenId(boolean isOpenId) {
		this.isOpenId = isOpenId;
	}

	public boolean isFacebook() {
		return isFacebook;
	}

	public void setFacebook(boolean isFacebook) {
		this.isFacebook = isFacebook;
	}

	public String getSignupUrl() {
		return signupUrl;
	}

	public void setSignupUrl(String signupUrl) {
		this.signupUrl = signupUrl;
	}

	public List<Long> getTeamIDs() {
		return teamIDs;
	}

	public void setTeamIDs(List<Long> teamIDs) {
		this.teamIDs = teamIDs;
	}

	public List<Boolean> getRoundsComplete() {
		return roundsComplete;
	}

	public void setRoundsComplete(List<Boolean> roundsComplete) {
		this.roundsComplete = roundsComplete;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getDraftID() {
		return draftID;
	}

	public void setDraftID(Long draftID) {
		this.draftID = draftID;
	}

	public Long getLastEntryId() {
		return lastEntryId;
	}

	public void setLastEntryId(Long lastEntryId) {
		this.lastEntryId = lastEntryId;
	}

	public Long getLastClubhouseId() {
		return lastClubhouseId;
	}

	public void setLastClubhouseId(Long lastClubhouseId) {
		this.lastClubhouseId = lastClubhouseId;
	}

	public Long getLastCompetitionId() {
		return lastCompetitionId;
	}

	public void setLastCompetitionId(Long lastCompetitionId) {
		this.lastCompetitionId = lastCompetitionId;
	}

	public LoginInfo.ProviderType getProviderType() {
		return providerType;
	}

	public void setProviderType(LoginInfo.ProviderType providerType) {
		this.providerType = providerType;
	}

	public LoginInfo.Selector getSelector() {
		return selector;
	}

	public void setSelector(LoginInfo.Selector selector) {
		this.selector = selector;
	}

	/**
	 * @return whether the user must change their password on next login
	 */
	public boolean getMustChangePassword() {
		return mustChangePassword;
	}

	public void setMustChangePassword(boolean mustChangePassword) {
		this.mustChangePassword = mustChangePassword;
	}

	@Override
	public boolean isTopTenContentEditor() {

		return isTopTenContentEditor;
	}

	@Override
	public boolean isTopTenContentContributor() {
		return isTopTenContentContributor;
	}


	@Override
	public void setTopTenContentEditor(boolean set) {
		isTopTenContentEditor = set;
	}

	@Override
	public void setTopTenContentContributor(boolean set) {
		isTopTenContentContributor = set;
	}

}
