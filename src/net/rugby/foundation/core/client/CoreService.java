/**
 * 
 */
package net.rugby.foundation.core.client;

import java.util.List;

import net.rugby.foundation.model.shared.Content;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.IClubhouseMembership;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.LoginInfo;
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
			String password, boolean isGoogle, boolean isFacebook);

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
	
	LoginInfo updateAccount(String email, String screenName);
	
	String getFacebookLoginUrl(String destination);
	
	/**
	 * 
	 * @param email
	 * @param oldPassword
	 * @param newPassword
	 * @return an empty LoginInfo if it couldn't find user or oldPassword wasn't correct
	 */
	LoginInfo changePassword(String email, String oldPassword, String newPassword);
	LoginInfo forgotPassword(String email);
	
	IContent getContent(Long contentId);
	IContent saveContent(IContent content);
}
