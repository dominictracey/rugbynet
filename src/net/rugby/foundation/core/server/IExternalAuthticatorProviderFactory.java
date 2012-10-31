/**
 * 
 */
package net.rugby.foundation.core.server;

import javax.servlet.http.HttpServletRequest;

import net.rugby.foundation.model.shared.LoginInfo;
import net.rugby.foundation.model.shared.LoginInfo.ProviderType;
import net.rugby.foundation.model.shared.LoginInfo.Selector;

/**
 * @author home
 *
 */
public interface IExternalAuthticatorProviderFactory {

	/**
	 * @param type
	 * @param selector
	 * @param destination
	 * @return
	 */
	IExternalAuthenticatorProvider get(LoginInfo.ProviderType type, LoginInfo.Selector selector,
			String destination);

	/**
	 * @param req
	 * @return
	 */
	IExternalAuthenticatorProvider get(HttpServletRequest req);
}
