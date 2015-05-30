/**
 * 
 */
package net.rugby.foundation.core.server;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;

import net.rugby.foundation.model.shared.LoginInfo;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * @author home
 *
 */
public class OAuth2Provider implements IExternalAuthenticatorProvider {

	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	
	//private String federatedIdentity;
	private IAccountManager am;

	private String destination;
	
	/**
	 * private because we need to specify a selector
	 */
	@SuppressWarnings("unused")
	private OAuth2Provider() {
		
	}
	
	/**
	 * @param destination 
	 * @param string
	 */
	public OAuth2Provider(IAccountManager am, String destination) {
		this.am = am;
		this.destination = destination;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IExternalAuthenticatorProvider#getLocalURL(java.lang.String)
	 */
	@Override
	public String getLocalURL() {
		return "/login/oauth2/?destination="+destination;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IExternalAuthenticatorProvider#getExternalURL()
	 */
	@Override
	public String getExternalURL() {
		//String providerUrl = ExternalAuthenticatorFactory.openIdProviders.get(selector);

//		Set<String> attributes = new HashSet<String>();
//		return userService.createLoginURL("/login/oauth2/?destination="+destination, null, ExternalAuthenticatorFactory.openIdProviders.get(LoginInfo.Selector.googlePlus), attributes);

		Logger.getLogger("Login Servlet").log(Level.INFO, "login url is " +  userService.createLoginURL(getLocalURL()));
		return userService.createLoginURL(getLocalURL());
//		return "https://accounts.google.com/o/oauth2/auth?client_id=604804459782-6kuhcfvt3kudspdahvctep2p8rcc1672.apps.googleusercontent.com& response_type=code&scope=openid%20email&redirect_uri=https://dev.rugby.net/login/oauth2/&" +
//			"state=security_token%3D138r5719ru3e1%26url%3Dhttps://oa2cb.example.com/myHome&" +
//			"login_hint=jsmith@example.com&" +
//			"openid.realm=dev.rugby.net&" +
//			"hd=example.com";
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IExternalAuthenticatorProvider#handleLogin(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (user != null)
			try {
				am.processUser(user.getEmail().toLowerCase(), LoginInfo.ProviderType.oauth2, null, req, resp, null);
			} catch (NumberFormatException e) {
				Logger.getLogger("Login Servlet").log(Level.SEVERE,e.getLocalizedMessage(),e);
			} catch (JSONException e) {
				Logger.getLogger("Login Servlet").log(Level.SEVERE,e.getLocalizedMessage(),e);
			} catch (ParseException e) {
				Logger.getLogger("Login Servlet").log(Level.SEVERE,e.getLocalizedMessage(),e);
			}
		else 
			resp.sendRedirect(getExternalURL());
	}
//
//	public String getFederatedIdentity() {
//		return federatedIdentity;
//	}
//
//	public void setFederatedIdentity(String federatedIdentity) {
//		this.federatedIdentity = federatedIdentity;
//	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IExternalAuthenticatorProvider#setSelector(net.rugby.foundation.core.client.Identity.Selector)
	 */
	@Override
	public void setSelector(LoginInfo.Selector selector) {
		
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IExternalAuthenticatorProvider#setDestination(java.lang.String)
	 */
	@Override
	public void setDestination(String destination) {
		this.destination=destination;
		
	}



}
