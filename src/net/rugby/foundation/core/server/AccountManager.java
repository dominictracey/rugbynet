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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.rugby.foundation.core.server.FacebookProvider.Base64Helper;
import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.core.shared.IdentityTypes.Actions;
import net.rugby.foundation.core.shared.IdentityTypes.Keys;
import net.rugby.foundation.game1.client.place.Profile;
import net.rugby.foundation.model.shared.CoreConfiguration;
import net.rugby.foundation.model.shared.IAppUser;
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

	@Inject
	public AccountManager(IAppUserFactory auf) {
		this.auf = auf;
	}

	@Override
	public IAppUser createAccount(String emailAddress, String nickName, String password, JSONObject attributes, boolean isOpenId, boolean isFacebook, HttpServletRequest request) throws NumberFormatException, JSONException, ParseException {

		LoginInfo info = new LoginInfo();
		info.setLoggedIn(false);
		IAppUser u = null;
		
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
			if (!isOpenId && !isFacebook) {
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
				} else {
					if (nickName != null)  {  // it's ok if its null - means its just a non-native signup.
						auf.setNickName(nickName);
						au = auf.get();
						if (au != null)
							info.setStatus(CoreConfiguration.getCreateacctErrorNicknameExists());
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
					u.setNative(!isOpenId && !isFacebook);
					
					if (attributes != null)
						u = addJSONAttributes(u,attributes);
					
					u = auf.put(u);
					if (u.getNickname() == null || u.getNickname().isEmpty()) {
						u.setNickname("user"+u.getId().toString());
						u = auf.put(u);
					}

					HttpSession session = request.getSession();
					info = getLoginInfo(u);
					session.setAttribute("loginInfo", info);
				}
			}
		}


		sendAdminEmail("New Fantasy Rugby user", info.getEmailAddress() + "\n" + info.getNickname());
		return u;

	}

	@Override
	public LoginInfo getLoginInfo(IAppUser u) {
		LoginInfo loginInfo = new LoginInfo();
		loginInfo.setLoggedIn(true);
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
		au.setEmailAddress(email);
		au.setNickname(screenName);
		auf.put(au);

		HttpSession session = request.getSession();
		LoginInfo info = getLoginInfo(au);
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
			au.setTimezone(attributes.getString("timezone"));
		}	
		if (attributes.has("locale")) {
			au.setLocale(attributes.getString("locale"));
		}	
		if (attributes.has("verified")) {
			if (attributes.getString("verified").equals("true"))
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

			try {
				// create their AppUser account with a generic screen name, then send them to change it
				u = createAccount(email.toLowerCase(), null, null, attributes, providerType.equals(LoginInfo.ProviderType.openid), providerType.equals(LoginInfo.ProviderType.facebook), req);

			} catch (NumberFormatException e) {
				Logger.getLogger("Login Servlet").log(Level.SEVERE,e.getLocalizedMessage(),e);
			} catch (JSONException e) {
				Logger.getLogger("Login Servlet").log(Level.SEVERE,e.getLocalizedMessage(),e);
			} catch (ParseException e) {
				Logger.getLogger("Login Servlet").log(Level.SEVERE,e.getLocalizedMessage(),e);
			}

			validateProvider(u, providerType, selector, attributes);
			
			assert(loginInfo != null && loginInfo.isLoggedIn() == true);
			loginInfo.setProviderType(providerType);
			loginInfo.setSelector(selector);
			HttpSession session = req.getSession();
			session.setAttribute("loginInfo", loginInfo);	


			String destination = req.getParameter(Keys.destination.toString());
			// note we have to unfortunately encode the destination ourselves because we use the GWT encoder in the Profile code which doesn't work on the server side
			Profile place = new Profile(Actions.updateScreenName, providerType, selector, null); 
			
			Profile.Tokenizer tokenizer = new Profile.Tokenizer();
			String url = "";
			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) 
				url = "/s/?gwt.codesvr=127.0.0.1:9997#Profile:";
			else
				url = "/s/?#Profile:";

			if (providerType.equals(LoginInfo.ProviderType.facebook) && destination != null) {
				destination = Base64Helper.decode(destination);
			}
			url += tokenizer.getToken(place) + "&destination=" + URLEncoder.encode(destination, resp.getCharacterEncoding());
			resp.sendRedirect(url);
		} else {
			// if we did find them, make sure they are all set
			validateProvider(u, providerType, selector, attributes);
			
			HttpSession session = req.getSession();

			loginInfo = getLoginInfo(u);
			loginInfo.setProviderType(providerType);
			loginInfo.setSelector(selector);
			session.setAttribute("loginInfo", loginInfo);	
			String destination = req.getParameter(Keys.destination.toString());
			Profile place = new Profile(Actions.done, providerType, selector, null);
			Profile.Tokenizer tokenizer = new Profile.Tokenizer();
			String url = "";
			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) 
				url = "/s/?gwt.codesvr=127.0.0.1:9997#Profile:";
			else
				url = "/s/?#Profile:";

			if (providerType.equals(LoginInfo.ProviderType.facebook) && destination != null) {
				destination = Base64Helper.decode(destination);
			}
			
			url += tokenizer.getToken(place) + "&destination=" + destination;
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
			return loginInfo; //empty		
		}

		hash = DigestUtils.md5Hex(newPassword);
		u.setPwHash(hash);
		u.setMustChangePassword(false);

		auf.put(u);

		loginInfo = getLoginInfo(u);
		
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
	public LoginInfo forgotPassword(String email) {
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

			if (u.isFacebook() || u.isOpenId()) {  // #2
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
			sendUserEmail(u, "Password reset from Rugby.net", "Hi, we received a request to reset your password. Your temporary password is " + password + " - please use it to login and set up a new password. If you didn't ask to have your password reset, sorry for the inconvenience.");

			loginInfo = getLoginInfo(u);
			loginInfo.setLoggedIn(false);
			return loginInfo;
			
		}  catch (Throwable ex) {
			Logger.getLogger("Core Service").log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}	
	}
}
