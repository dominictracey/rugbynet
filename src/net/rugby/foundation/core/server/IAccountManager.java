/**
 * 
 */
package net.rugby.foundation.core.server;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.LoginInfo;

/**
 * @author home
 *
 */
public interface IAccountManager {
	LoginInfo getLoginInfo(IAppUser u);
//	void sendAdminEmail(String subject, String message);
	LoginInfo updateAccount(IAppUser au, String email, String screenName, HttpServletRequest request);
	
	/**
	 * @param emailAddress
	 * @param nickName
	 * @param password
	 * @param attributes
	 * @param isOpenId
	 * @param isFacebook
	 * @param request
	 * @return
	 * @throws JSONException 
	 * @throws NumberFormatException 
	 * @throws ParseException 
	 */
//	IAppUser createAccount(String emailAddress, String nickName,
//			String password, JSONObject attributes, boolean isOpenId,
//			boolean isFacebook, HttpServletRequest request) throws NumberFormatException, JSONException, ParseException;

	/**
	 * @param email
	 * @param providerType
	 * @param selector
	 * @param req
	 * @param resp
	 * @param attributes
	 * @throws IOException
	 * @throws ParseException 
	 * @throws JSONException 
	 * @throws NumberFormatException 
	 */
	void processUser(String email, LoginInfo.ProviderType providerType,
			LoginInfo.Selector selector, HttpServletRequest req,
			HttpServletResponse resp, JSONObject attributes) throws IOException, NumberFormatException, JSONException, ParseException;
	/**
	 * @param u - user to get email if their opt-out settings allow
	 * @param subject
	 * @param message
	 */
	void sendUserEmail(IAppUser u, String subject, String message);
	/**
	 * @param email
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	LoginInfo changePassword(String email, String oldPassword,
			String newPassword);
	/**
	 * @param email
	 * @return
	 */
	LoginInfo forgotPassword(String email, String destination);
	LoginInfo createAccount(String emailAddress, String nickName,
			String password, String destination, JSONObject attributes, boolean isOpenId,
			boolean isFacebook, boolean isOAuth2, HttpServletRequest request) throws NumberFormatException, JSONException, ParseException;

	LoginInfo validateEmail(String email, String validationCode);
	LoginInfo resendValidationEmail(String email);
	String createDigestAccount(String e);
}