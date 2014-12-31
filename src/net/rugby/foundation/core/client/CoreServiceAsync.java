/**
 * 
 */
package net.rugby.foundation.core.client;

import java.util.ArrayList;
import java.util.List;
import com.google.gwt.user.client.rpc.AsyncCallback;

import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.IClubhouseMembership;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.model.shared.ISponsor;
import net.rugby.foundation.model.shared.LoginInfo;

/**
 * @author home
 *
 */
public interface CoreServiceAsync {
	public String END_POINT = "core/CoreService";
	void getComp(Long compId, AsyncCallback<ICompetition> cb);
	void getClubhouses(AsyncCallback<List<IClubhouse> > cb);
	void getConfiguration(AsyncCallback<ICoreConfiguration> cb);
	/**
	 * @param loginUrl
	 * @param asyncCallback
	 */
	void login(AsyncCallback<LoginInfo> asyncCallback);
	void logOff(LoginInfo info, AsyncCallback<LoginInfo> asyncCallback);
	void nativeLogin(String emailAddress, String password, AsyncCallback<LoginInfo> asyncCallback);
	void createAccount(String emailAddress, String nickName,
			String password, boolean isGoogle, boolean isFacebook, AsyncCallback<LoginInfo> asyncCallback);

	void updatePreferences(LoginInfo loginInfo, AsyncCallback<LoginInfo> cb);
	void createClubhouse(String name, String description, Boolean publicClubhouse, AsyncCallback<IClubhouse> cb);
	void joinClubhouse(Long clubhouseId, AsyncCallback<IClubhouse> cb);
	void getClubhouse(Long clubhouseId, AsyncCallback<IClubhouse> cb);
	void getClubhouseMembers(Long clubhouseId, AsyncCallback<List<IClubhouseMembership>> cb);
	/**
	 * @param selector
	 * @param asyncCallback
	 */
	void getOpenIdUrl(LoginInfo.Selector selector, String destination, AsyncCallback<String> asyncCallback);
	/**
	 * @param email
	 * @param screenName
	 * @param asyncCallback
	 */
	void updateAccount(String email, String screenName,
			AsyncCallback<LoginInfo> asyncCallback);
	/**
	 * @param destination
	 * @param asyncCallback
	 */
	void getFacebookLoginUrl(String destination,
			AsyncCallback<String> asyncCallback);
	/**
	 * @param email
	 * @param oldPassword
	 * @param newPassword
	 * @param asyncCallback
	 */
	void changePassword(String email, String oldPassword, String newPassword,
			AsyncCallback<LoginInfo> asyncCallback);
	/**
	 * @param email
	 * @param asyncCallback
	 */
	void forgotPassword(String email, AsyncCallback<LoginInfo> asyncCallback);
	void getContent(Long contentId, AsyncCallback<IContent> asyncCallback);
	void saveContent(IContent content, AsyncCallback<IContent> asyncCallback);
	
//	void getPlace(String guid, AsyncCallback<IServerPlace> asyncCallback);
	void getSponsor(Long id, AsyncCallback<ISponsor> asyncCallback);
	void getResultsForOrdinal(int ordinal, Long virtualCompId, AsyncCallback<ArrayList<IMatchGroup>> asyncCallback);
}
