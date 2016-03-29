/**
 * 
 */
package net.rugby.foundation.core.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.rugby.foundation.core.server.FacebookProvider.Base64Helper;
import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.mail.UserEmailer;
import net.rugby.foundation.core.shared.IdentityTypes.Actions;
import net.rugby.foundation.core.shared.IdentityTypes.Keys;
import net.rugby.foundation.game1.client.place.Profile;
import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.IAppUser.EmailStatus;
import net.rugby.foundation.model.shared.ITopTenUser;
import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.model.shared.LoginInfo.ProviderType;
import net.rugby.foundation.model.shared.LoginInfo.Selector;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.inject.Inject;
import com.google.appengine.api.utils.SystemProperty;
/**
 * @author home
 *
 */

public class AccountManager implements IAccountManager {

	private IAppUserFactory auf;
	private UserEmailer userEmailer = null;
	private IConfigurationFactory ccf;
	private String charEncoding = "UTF-8";

	@Inject
	public AccountManager(IAppUserFactory auf, IConfigurationFactory ccf) {
		this.auf = auf;
		this.ccf = ccf;
	}

	@Override
	public LoginInfo createAccount(String emailAddress, String nickName, String password, String destination, JSONObject attributes, boolean isOpenId, boolean isFacebook, boolean isOAuth2, HttpServletRequest request) throws NumberFormatException, JSONException, ParseException {

		LoginInfo info = new LoginInfo();
		info.setEmailAddress(emailAddress);
		info.setLoggedIn(false);
		IAppUser u = null;

		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Creating account for " + emailAddress);
		boolean error = false;
		String hash = null;
		//valid email address?
		//	Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$");
		Matcher m = p.matcher(emailAddress.toUpperCase());
		boolean matchFound = m.matches();
		if (!matchFound) {
			// TODO this may be a merge
			info.setStatus(CoreConfiguration.getCreateacctErrorInvalidEmail());
		} else {
			if (!isOpenId && !isFacebook && !isOAuth2) {
				//password length
				if (password.length() < 5) {
					info.setStatus(CoreConfiguration.getCreateacctErrorPasswordTooShort());
					error = true;
				} else {
					//create pw hash
					hash = DigestUtils.md5Hex(password);
				}
			}

			if (error != true) {
				//is this already in use?
				auf.setEmail(emailAddress);
				IAppUser au = auf.get();
				if (au != null) {
					// TODO this may be a merge
					info.setStatus(CoreConfiguration.getCreateacctErrorExists());
					return info;
				} else {
					if (nickName != null)  {  // it's ok if its null - means its just a non-native signup.
						auf.setNickName(nickName);
						au = auf.get();
						if (au != null) {
							info.setStatus(CoreConfiguration.getCreateacctErrorNicknameExists());
							return info;
						}
					}

					auf.setId(null);  // get an empty one
					u = auf.get();
					u.setEmailAddress(emailAddress);
					u.setNickname(nickName);
					u.setPwHash(hash);
					u.setActive(true);
					u.setAdmin(false);
					u.setSuperadmin(false);
					u.setIsOpenId(isOpenId);
					u.setFacebook(isFacebook);
					u.setOath2(isOAuth2);
					u.setNative(!isOpenId && !isFacebook);

					if (u.isNative()) {
						u.setEmailValidated(false);
						u.setEmailValidationCode(randomPassword());						
					} else {
						u.setEmailValidated(true);
					}

					u.setOptOutCode(randomPassword());

					if (attributes != null)
						u = addJSONAttributes(u,attributes);

					u = auf.put(u);
					if (u.getNickname() == null || u.getNickname().isEmpty()) {
						u.setNickname("user"+u.getId().toString());
						u = auf.put(u);
					}


					info = getLoginInfo(u);
				}
			}
		}


		sendAdminEmail("New Fantasy Rugby user", info.getEmailAddress() + "\n" + info.getNickname());
		if (u.isNative()) {
			sendValidationEmail(u, destination);
		}

		return info;

	}

	protected void sendValidationEmail(IAppUser u, String destination) {
		if (userEmailer == null) {
			userEmailer = new UserEmailer();
		}

		String dest = "";
		try {
			dest = URLEncoder.encode(destination, charEncoding);
		} catch (UnsupportedEncodingException e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,e.getLocalizedMessage());
		}

		// chop the /s/ from the end of the BaseToptenUrl
		String linkTarget = ccf.get().getBaseToptenUrl() + "s/#Profile:" + Keys.action + "=" + Actions.validateEmail + "&" + Keys.email + "=" + u.getEmailAddress() + "&" + Keys.validationCode + "=" + u.getEmailValidationCode() + "&" + Keys.destination + "=" + dest;
		boolean configured = userEmailer.configure("Account verification link from The Rugby Net", "Account Services", "Click here to activate your account", linkTarget, "If the link above doesn't work, you can enter your validation code (" + u.getEmailValidationCode() +") in the sign up window.", "", u);
		if (configured) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING,"Sent email validation link " + u.getEmailValidationCode() + " to " + u.getEmailAddress());
			userEmailer.send();
		}
	}

	@Override
	public LoginInfo getLoginInfo(IAppUser u) {
		LoginInfo loginInfo = new LoginInfo();
		//loginInfo.setLoggedIn(true);
		loginInfo.setLoggedIn(false);
		loginInfo.setEmailValidated(u.getEmailValidated());
		loginInfo.setEmailAddress(u.getEmailAddress());
		loginInfo.setNickname(u.getNickname());
		loginInfo.setLogoutUrl("");    	
		loginInfo.setIsOpenId(u.isOpenId());
		loginInfo.setFacebook(u.isFacebook());
		loginInfo.setAdmin(u.isAdmin());
		loginInfo.setLastEntryId(u.getLastEntryId());
		loginInfo.setLastClubhouseId(u.getLastClubhouseId());
		loginInfo.setLastCompetitionId(u.getLastCompetitionId());
		loginInfo.setMustChangePassword(u.isMustChangePassword());

		if (u instanceof ITopTenUser) {
			loginInfo.setTopTenContentContributor(((ITopTenUser)u).isTopTenContentContributor());
			loginInfo.setTopTenContentEditor(((ITopTenUser)u).isTopTenContentEditor());
		}

		loginInfo.setCompList(u.getCompList());
		loginInfo.setOptOut(u.getOptOut());

		// see if they have done the draft and round picks yet.
		//ArrayList<Group> groups = getGroupsByGroupType(GroupType.MY);
		//	  for (int i=0; i<10; i++) {
		//		loginInfo.getRoundsComplete().add(new Boolean(false));
		//	  }
		//    if (!groups.isEmpty()) {
		//  	  for (Group g : groups) {
		//  		  //@TODO grep 5 fix
		//  		  if (((MyGroup)g).getRound() == 5L)
		//  		  	loginInfo.setDraftID(g.getId());
		//  		  loginInfo.getTeamIDs().add(g.getId());
		//  		  loginInfo.getRoundsComplete().set(((MyGroup)g).getRound(), true);
		//  	  }
		//    }

		return loginInfo;
	}

	private void sendAdminEmail(String subject, String message) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("dominic.tracey@gmail.com", "rugby.net "));
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress("dominic.tracey@gmail.com", "Dominic Tracey"));
			msg.setSubject(subject);
			msg.setText(message);
			Transport.send(msg);
			Logger.getLogger(AccountManager.class.getName()).log(Level.INFO,"Sent mail to " + msg.getRecipients(javax.mail.Message.RecipientType.TO)[0].toString());

		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendUserEmail(IAppUser u, String subject, String message) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("no-reply@rugby.net", "Fantasy Rugby - NoReply"));
			if (!u.getOptOut()) {
				msg.addRecipient(Message.RecipientType.TO,
						new InternetAddress(u.getEmailAddress(), "Fantasy Rugby"));
				msg.setSubject(subject);
				msg.setText(message);
				Transport.send(msg);
				Logger.getLogger(AccountManager.class.getName()).log(Level.INFO,"Sent mail to " + msg.getRecipients(javax.mail.Message.RecipientType.TO)[0].toString());
			}
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param email
	 * @param screenName
	 * @return
	 */
	@Override
	public LoginInfo updateAccount(IAppUser au, String email, String screenName, HttpServletRequest request) {

		// is the nickname valid?
		auf.setNickName(screenName);
		IAppUser u = auf.get();

		String error = "";
		if (u != null && !u.getId().equals(au.getId()) ) {
			// already in use
			error = "That screen name is already in use. Please pick another.";
		} else {
			au.setNickname(screenName);
			auf.put(au);
		}


		HttpSession session = request.getSession();
		LoginInfo info = getLoginInfo(au);
		info.setLoggedIn(true);
		if (!error.isEmpty()) {
			info.setStatus(error);
		}

		session.setAttribute("loginInfo", info);

		return info;
	}

	private IAppUser addJSONAttributes(IAppUser au, JSONObject attributes) throws NumberFormatException, JSONException, ParseException {
		if (attributes.has("id")) {
			Long parsed = Long.parseLong(attributes.getString("id"));
			au.setFbId(parsed);
		}
		if (attributes.has("name")) {
			au.setFbName(attributes.getString("name"));
		}		
		if (attributes.has("first_name")) {
			au.setFirstName(attributes.getString("first_name"));
		}		
		if (attributes.has("last_name")) {
			au.setLastName(attributes.getString("last_name"));
		}	
		if (attributes.has("link")) {
			au.setFacebookLink(attributes.getString("link"));
		}	
		if (attributes.has("location")) {
			JSONObject location = attributes.getJSONObject("location");
			if (location.has("id")) {
				Long parsed = Long.parseLong(location.getString("id"));
				au.setFbLocationId(parsed);
			}
			if (location.has("name")) {
				au.setFbLocationName(location.getString("name"));
			}
		}
		if (attributes.has("gender")) {
			au.setGender(attributes.getString("gender"));
		}	
		if (attributes.has("timezone")) {
			au.setTimezone(((Integer)attributes.getInt("timezone")).toString());
		}	
		if (attributes.has("locale")) {
			au.setLocale(attributes.getString("locale"));
		}	
		if (attributes.has("verified")) {
			if (attributes.getBoolean("verified") == true)
				au.setFbVerified(true);
			else
				au.setFbVerified(false);				
		}	
		if (attributes.has("updated_time")) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			Date date = format.parse(attributes.getString("updated_time"));
			au.setFbUpdatedTime(date);
		}	

		auf.put(au);

		return au;

	}

	@Override
	public void processUser(String email, LoginInfo.ProviderType providerType, LoginInfo.Selector selector, HttpServletRequest req, HttpServletResponse resp, JSONObject attributes) throws IOException, NumberFormatException, JSONException, ParseException {
		LoginInfo loginInfo = new LoginInfo();

		auf.setEmail(email.toLowerCase());
		IAppUser u = auf.get();

		// if we don't find them, we need to create a new profile for them
		if (u == null) {
			boolean error = false;
			try {
				// create their AppUser account with a generic screen name, then send them to change it
				loginInfo = createAccount(email.toLowerCase(), null, null, "", attributes, providerType.equals(LoginInfo.ProviderType.openid), providerType.equals(LoginInfo.ProviderType.facebook), providerType.equals(LoginInfo.ProviderType.oauth2),req);

				if (loginInfo.isLoggedIn()) {
					u = auf.get();
					assert(u != null);
				}
			} catch (NumberFormatException e) {
				Logger.getLogger("Login Servlet").log(Level.SEVERE,e.getLocalizedMessage(),e);
				error = true;
			} catch (JSONException e) {
				Logger.getLogger("Login Servlet").log(Level.SEVERE,e.getLocalizedMessage(),e);
				error = true;
			} catch (ParseException e) {
				Logger.getLogger("Login Servlet").log(Level.SEVERE,e.getLocalizedMessage(),e);
				error = true;
			}

			if (!error) {
				validateProvider(u, providerType, selector, attributes);
				loginInfo = getLoginInfo(u);
				assert(loginInfo != null && loginInfo.isLoggedIn() == true);
				loginInfo.setProviderType(providerType);
				loginInfo.setSelector(selector);
				HttpSession session = req.getSession();
				session.setAttribute("loginInfo", loginInfo);	


				String destination = req.getParameter(Keys.destination.toString());
				// note we have to unfortunately encode the destination ourselves because we use the GWT encoder in the Profile code which doesn't work on the server side
				Profile place = new Profile(Actions.updateScreenName, providerType, selector, null); 

				Profile.Tokenizer tokenizer = new Profile.Tokenizer();
				String url = "/s/#Profile:";

				if ((providerType.equals(LoginInfo.ProviderType.facebook) || providerType.equals(LoginInfo.ProviderType.oauth2) || providerType.equals(LoginInfo.ProviderType.openid)) && destination != null) {
					destination = Base64Helper.decode(destination);
				}
				url += tokenizer.getToken(place) + "&destination=" + URLEncoder.encode(destination, resp.getCharacterEncoding());

				resp.sendRedirect(url);
			} else {
				// something bad happened trying to create the user.
				resp.sendRedirect("/404.html"); // @TODO - need error page that doesn't provide too much info.
			}
		} else {
			// if we did find them, make sure they are all set
			validateProvider(u, providerType, selector, attributes);

			HttpSession session = req.getSession();

			loginInfo = getLoginInfo(u);
			loginInfo.setProviderType(providerType);
			loginInfo.setLoggedIn(true);
			loginInfo.setSelector(selector);
			session.setAttribute("loginInfo", loginInfo);	
			String destination = req.getParameter(Keys.destination.toString());
			Profile place = new Profile(Actions.done, providerType, selector, null);
			Profile.Tokenizer tokenizer = new Profile.Tokenizer();
			String url = "/s/#Profile:";

			u.setLastLogin(new Date());
			auf.put(u);

			if ((providerType.equals(LoginInfo.ProviderType.facebook) || providerType.equals(LoginInfo.ProviderType.oauth2)) && destination != null) {
				destination = Base64Helper.decode(destination);
			}

			url += tokenizer.getToken(place) + "&destination="  + URLEncoder.encode(destination, resp.getCharacterEncoding());
			resp.sendRedirect(url);
		}
	}

	/** A "sanity check" to keep the user account in good shape in case the user is connecting via different authenticators
	 * @param u
	 * @param providerType
	 * @param selector
	 * @param attributes 
	 * @throws ParseException 
	 * @throws JSONException 
	 * @throws NumberFormatException 
	 */
	private void validateProvider(IAppUser u, ProviderType providerType, Selector selector, JSONObject attributes) throws NumberFormatException, JSONException, ParseException {
		boolean needSave = false;
		if (providerType.equals(ProviderType.openid)) {
			if (!u.getFederatedIdentities().contains(ExternalAuthenticatorFactory.openIdProviders.get(selector))) {
				u.addFederatedIdentity(ExternalAuthenticatorFactory.openIdProviders.get(selector));
				needSave = true;
			}
			if (!u.isOpenId()) {
				u.setOpenId(true);
				needSave = true;
			}
			if (u.getPwHash() != null || u.isNative()) {
				u.setPwHash(null);
				u.setNative(false);
				needSave = true;
			}
		} else if (providerType.equals(ProviderType.facebook)) {
			if (!u.isFacebook()) {
				u.setFacebook(true);
				needSave = true;
			}
			if (u.getPwHash() != null || u.isNative()) {
				u.setPwHash(null);
				u.setNative(false);
				needSave = true;
			}
			if (u.getFbId() == null) {
				u = addJSONAttributes(u,attributes);
				needSave = true;
			}
		}  else if (providerType.equals(ProviderType.oauth2)) {
			if (!u.isOath2()) {
				u.setOath2(true);
				needSave = true;
			}
			if (u.getPwHash() != null || u.isNative()) {
				u.setPwHash(null);
				u.setNative(false);
				needSave = true;
			}
		}

		if (needSave) {
			auf.put(u);
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IAccountManager#changePassword(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public LoginInfo changePassword(String email, String oldPassword, String newPassword) {
		LoginInfo loginInfo = new LoginInfo();

		auf.setEmail(email.toLowerCase());
		IAppUser u = auf.get();

		if (u == null) {
			return loginInfo; //empty
		}

		String hash = DigestUtils.md5Hex(oldPassword);
		if (u.getPwHash() == null || !u.getPwHash().equals(hash))  {
			loginInfo.setStatus("Temporary password incorrect");
			loginInfo.setEmailAddress(email);
			return loginInfo; //empty		
		}

		hash = DigestUtils.md5Hex(newPassword);
		u.setPwHash(hash);
		u.setMustChangePassword(false);

		// they had to have gotten an email for this. There is a flow that they can get here without doing the email validation link so...
		u.setEmailValidated(true);
		u.setLastLogin(new Date());
		auf.put(u);

		loginInfo = getLoginInfo(u);
		loginInfo.setLoggedIn(true);
		return loginInfo;
	}

	/**
	 * http://nscraps.com/Java/748-random-password-generator-java-example-source-code.htm
	 */
	private String randomPassword() {
		String str=new String("G12HIJdefgPQRSTUVWXYZabc56hijklmnopqAB78CDEF0KLMNO3rstu4vwxyz9");
		StringBuffer sb=new StringBuffer();
		String ar=null;
		Random r = new Random();
		int te=0;
		for(int i=1;i<=6;i++){
			te=r.nextInt(62);
			ar=ar+str.charAt(te);
			sb.append(str.charAt(te));
		}

		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreService#forgotPassword(java.lang.String)
	 */
	@Override
	public LoginInfo forgotPassword(String email, String destination) {
		try {
			// so we can get three results here:
			// 1. Native account exists, password reset and email sent
			//		-> show ManageProfile with change password panel
			// 2. The email provided is not native (externally authenticated)
			//		-> set error on Login panel telling them to pick authenticator button
			// 3. The email is not recognized
			//		-> show ManageProfile saying we couldn't find the email and asking them to sign up

			LoginInfo loginInfo = new LoginInfo();

			auf.setEmail(email.toLowerCase());
			IAppUser u = auf.get();

			if (u == null) {
				return loginInfo; //empty (#3)
			}

			if (u.isFacebook() || u.isOath2() || u.isOpenId()) {  // #2
				loginInfo = getLoginInfo(u);
				loginInfo.setLoggedIn(false);
				return loginInfo;
			}

			// Leaving just #3
			// generate a random password for them
			String password = randomPassword();
			String hash = DigestUtils.md5Hex(password);
			u.setPwHash(hash);
			u.setMustChangePassword(true);
			auf.put(u);
			// send them email
			//sendUserEmail(u, "Password reset from The Rugby Net", "Hi, we received a request to reset your password. Your temporary password is " + password + " - please use it to login and set up a new password. If you didn't ask to have your password reset, sorry for the inconvenience.");
			if (userEmailer == null) {
				userEmailer = new UserEmailer();
			}

			String dest = "";
			try {
				dest = URLEncoder.encode(destination, charEncoding);
			} catch (UnsupportedEncodingException e) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,e.getLocalizedMessage());
			}

			String linkTarget = ccf.get().getBaseToptenUrl() + "s/#Profile:" + Keys.action + "=" + Actions.changePassword + "&" + Keys.email + "=" + u.getEmailAddress() + "&" + Keys.temporaryPassword + "=" + password + "&" + Keys.destination + "=" + dest;
			boolean configured = userEmailer.configure("Password reset link from The Rugby Net", "Account Services", "Click here to create a new password", linkTarget, "If the link above doesn't work, you can enter your temporary password (<bold>" + password +"</bold>) in the change password page.", "", u);
			if (configured) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING,"Sent password change link " + password + " to " + u.getEmailAddress());
				userEmailer.send();
			}	

			loginInfo = getLoginInfo(u);
			loginInfo.setLoggedIn(false);
			return loginInfo;

		}  catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}	
	}

	@Override
	public LoginInfo validateEmail(String email, String validationCode) {
		try {
			auf.setEmail(email);
			IAppUser user = auf.get();

			if (user != null && user.isNative()) {
				if (validationCode.trim().equals(user.getEmailValidationCode())) {
					user.setEmailValidated(true);
					user.setEmailStatus(EmailStatus.VALIDATED);
					user.setLastLogin(new Date());
					auf.put(user);

					// and they are logged on now
					LoginInfo loginInfo = getLoginInfo(user);
					assert(loginInfo != null);
					loginInfo.setLoggedIn(true);
					loginInfo.setStatus("Email successfully validated");
					return loginInfo;
				}
			}
			LoginInfo loginInfo = new LoginInfo();
			loginInfo.setEmailAddress(email);
			loginInfo.setStatus("Problems validating email");
			return loginInfo;
		}  catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}	
	}

	@Override
	public LoginInfo resendValidationEmail(String email) {
		auf.setEmail(email);
		IAppUser u = auf.get();
		if (u != null) {
			sendValidationEmail(u, "");
			LoginInfo loginInfo = getLoginInfo(u);
			loginInfo.setLoggedIn(false);
			loginInfo.setStatus("Your validation code has been resent, check your email for the link. If it doesn't work, you can enter the code in the email above.");
			return loginInfo;
		}
		return null;
	}

	@Override
	public String createDigestAccount(String e) {

		try {
			//check if email is already in our database
			auf.setEmail(e);
			if (auf.get() == null){
				//user is not in the database
				//create a new appUser object
				auf.setId(null);
				IAppUser user = auf.get();
				//setup the appUser
				user.setEmailAddress(e);
				user.setEmailStatus(EmailStatus.DIGEST);
				user.setPwHash("###");
				user.setMustChangePassword(true);
				user.setOptOutCode(randomPassword());
				user.setEmailValidated(true);
				user.setNative(true);
				//save the appUser to the Datastore
				user = auf.put(user);

				if (user.getNickname() == null || user.getNickname().isEmpty()) {
					user.setNickname("user"+user.getId().toString());
					user = auf.put(user);
				} 
				return e+": success";
			} else {

				return e+": already in use";
			}
		}
		catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return "Failed";
		}
	}
}

