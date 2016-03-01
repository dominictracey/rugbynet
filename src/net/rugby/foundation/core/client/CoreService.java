/**
 * 
 */
package net.rugby.foundation.core.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.IClubhouseMembership;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.ISponsor;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.model.shared.UniversalRound;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Dominic Tracey
 * <p>Provides access to competition-level (non-user) data.</p>
 */
@RemoteServiceRelativePath("CoreService")
public interface CoreService extends RemoteService {
	ICompetition getComp(Long compId);

	List<IClubhouse> getClubhouses();
	

	LoginInfo login();
	/**
	 * @param info
	 * @return
	 */
	LoginInfo logOff(LoginInfo info);
	/**
	 * @param emailAddress
	 * @param password
	 * @return
	 */
	LoginInfo nativeLogin(String emailAddress, String password);
	/**
	 * @param emailAddress
	 * @param nickName
	 * @param password
	 * @param isGoogle
	 * @param isFacebook
	 * @return
	 */
	LoginInfo createAccount(String emailAddress, String nickName,
			String password, String destination, boolean isGoogle, boolean isFacebook, boolean isOAuth2);

	/**
	 * @param loginInfo - Only will update lastEntryId, lastClubhouseId and lastCompetitionId
	 * @return
	 */
	LoginInfo updatePreferences(LoginInfo loginInfo);
	/**
	 * @param name
	 * @param description
	 * @param publicClubhouse - Is this clubhouse visible and available to join for anyone?
	 * @return
	 */
	IClubhouse createClubhouse(String name, String description, Boolean publicClubhouse);
	/**
	 * @param clubhouseId - Id of clubhouse the current user wishes to join
	 * @return
	 */
	IClubhouse joinClubhouse(Long clubhouseId);
	
	/**
	 * @return The Core Configuration object
	 */
	ICoreConfiguration getConfiguration();
	
	/**
	 * @param clubhouseId - Id of clubhouse to return
	 * @return
	 */
	IClubhouse getClubhouse(Long clubhouseId);

	/**
	 * @param clubhouseId
	 * @return If the currently logged on user is a member of the clubhouse, returns a list of IClubhouseMembership objects.
	 */
	List<IClubhouseMembership> getClubhouseMembers(Long clubhouseId);
	
	/**
	 * 
	 * @param selector - The dictionary selector for supported OpenID providers
	 * @param destination - URLEncoded relative address to send the browser after the action is complete
	 * @return the URL to redirect the browser to
	 */
	String getOpenIdUrl(LoginInfo.Selector selector, String destination);
	String getOAuth2Url(String destination);
	LoginInfo updateAccount(String email, String screenName, List<CompetitionType> compList, Boolean optOut);
	
	String getFacebookLoginUrl(String destination);
	
	/**
	 * 
	 * @param email
	 * @param oldPassword
	 * @param newPassword
	 * @return an empty LoginInfo if it couldn't find user or oldPassword wasn't correct
	 */
	LoginInfo changePassword(String email, String oldPassword, String newPassword);
	LoginInfo forgotPassword(String email, String destination);
	
	IContent getContent(Long contentId);
	IContent saveContent(IContent content);
	IContent getContent(String string);

	/**
	 * 
	 * @param the guid of the target location
	 * @return a place structure containing the fields necessary to construct a SeriesPlace for use with the GWT PlaceController
	 */
	//
	
	ISponsor getSponsor(Long id);
	
	ArrayList<IMatchGroup> getResultsForOrdinal(int ordinal, Long virtualCompId);
	
	UniversalRound getUniversalRound(int ordinal);
	
	public HashMap<String,Long> getContentItems();
	public LoginInfo validateEmail(String email, String validationCode);
}
