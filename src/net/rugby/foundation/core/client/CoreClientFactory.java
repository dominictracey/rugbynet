/**
 * 
 */
package net.rugby.foundation.core.client;

import net.rugby.foundation.model.shared.LoginInfo;

/**
 * @author home
 *
 */
public interface CoreClientFactory {
	CoreServiceAsync getRpcService();
	void setLoginInfo(LoginInfo info);
	Identity getIdentityManager();
	/**
	 * @return
	 */
	LoginInfo getLoginInfo();
}
