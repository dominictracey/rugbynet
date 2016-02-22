/**
 * 
 */
package net.rugby.foundation.core.client;

import java.util.HashMap;

import net.rugby.foundation.core.client.nav.INavManager;
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
	
	INavManager getNavManager();
	HashMap<String, Long> getContentNameMap();
	void setContentNameMap(HashMap<String, Long> contentNameMap);
	void console(String text);
	
	void recordAnalyticsEvent(String cat, String action, String label, int val);
}
