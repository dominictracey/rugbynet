/**
 * 
 */
package net.rugby.foundation.core.server;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rugby.foundation.model.shared.LoginInfo;

/**
 * @author home
 *
 */
public interface IExternalAuthenticatorProvider {
	
	void setSelector(LoginInfo.Selector selector);
	
	void setDestination(String destination);
	
	/**
	 * @return a URL that starts with /login/ that will start the login process for the client
	 * @param destination where we want to end up after the login process is complete
	 */
	String getLocalURL();
	/**
	 * 
	 * @param selector allows different providers to be specified
	 * @param destination where we want to end up after the login process is complete
	 * @return
	 */
	String getExternalURL();
	
	/**
	 * 
	 * @param req
	 * @param resp implementors should ALWAYS resp.redirect()
	 * @throws IOException 
	 */
	void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException;
	
}
