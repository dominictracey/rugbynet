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

import net.rugby.foundation.core.server.FacebookProvider.Base64Helper;
import net.rugby.foundation.model.shared.LoginInfo;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * @author home
 *
 */
public class OpenIdProvider implements IExternalAuthenticatorProvider {

	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	
	private String federatedIdentity;
	private IAccountManager am;
	private LoginInfo.Selector selector;
	private String destination;
	private String emptyDest = Base64Helper.encode("null");
	
	/**
	 * private because we need to specify a selector
	 */
	@SuppressWarnings("unused")
	private OpenIdProvider() {
		
	}
	
	/**
	 * @param destination 
	 * @param string
	 */
	public OpenIdProvider(LoginInfo.Selector selector, IAccountManager am, String destination) {
		this.selector = selector;
		this.am = am;
		this.destination = destination;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IExternalAuthenticatorProvider#getLocalURL(java.lang.String)
	 */
	@Override
	public String getLocalURL() {
		String dest = "";
		if (destination != null && !destination.isEmpty()) {
			dest = Base64Helper.encode(destination);
		} else {
			dest = emptyDest;
		}
		return "/login/openid/"+selector.toString()+"/?destination="+dest;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IExternalAuthenticatorProvider#getExternalURL()
	 */
	@Override
	public String getExternalURL() {
		String providerUrl = ExternalAuthenticatorFactory.openIdProviders.get(selector);
		Set<String> attributes = new HashSet<String>();
		return userService.createLoginURL("/login/openid/"+selector.toString()+"/?destination="+destination, null, providerUrl, attributes);
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IExternalAuthenticatorProvider#handleLogin(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (user != null)
			try {
				am.processUser(user.getEmail().toLowerCase(), LoginInfo.ProviderType.openid, selector, req, resp, null);
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

	public String getFederatedIdentity() {
		return federatedIdentity;
	}

	public void setFederatedIdentity(String federatedIdentity) {
		this.federatedIdentity = federatedIdentity;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IExternalAuthenticatorProvider#setSelector(net.rugby.foundation.core.client.Identity.Selector)
	 */
	@Override
	public void setSelector(LoginInfo.Selector selector) {
		this.selector = selector;
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IExternalAuthenticatorProvider#setDestination(java.lang.String)
	 */
	@Override
	public void setDestination(String destination) {
		this.destination=destination;
		
	}



}
