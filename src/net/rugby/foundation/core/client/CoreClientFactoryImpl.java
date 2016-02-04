/**
 * 
 */
package net.rugby.foundation.core.client;

import java.util.HashMap;

import net.rugby.foundation.core.client.nav.INavManager;
import net.rugby.foundation.core.client.nav.NavManager;
import net.rugby.foundation.model.shared.LoginInfo;

/**
 * @author home
 *
 */
public class CoreClientFactoryImpl implements CoreClientFactory {
	final CoreServiceAsync rpcService = ServicesFactory.rpcService;//GWT.create(CoreService.class);//
	private LoginInfo loginInfo = null;
	private Identity identity = null;
	private INavManager navManager;
	private HashMap<String,Long> contentNameMap = null;

	@Override
	public HashMap<String, Long> getContentNameMap() {
		return contentNameMap;
	}

	@Override
	public void setContentNameMap(HashMap<String, Long> contentNameMap) {
		this.contentNameMap = contentNameMap;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreClientFactory#getRpcService()
	 */
	@Override
	public CoreServiceAsync getRpcService() {

		return rpcService;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreClientFactory#setLoginInfo(net.rugby.foundation.model.shared.LoginInfo)
	 */
	@Override
	public void setLoginInfo(LoginInfo info) {
		loginInfo = info;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreClientFactory#getIdentityManager()
	 */
	@Override
	public Identity getIdentityManager() {
		if (identity == null) {
			identity = new Identity(this);
			identity.setClientFactory(this);
		}
		return identity;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.client.CoreClientFactory#getLoginInfo()
	 */
	@Override
	public LoginInfo getLoginInfo() {

		if (loginInfo == null) {
			return new LoginInfo();
		}
		
		return loginInfo;
	}

	@Override
	public INavManager getNavManager() {
		if (navManager == null) {
			navManager = new NavManager();
			navManager.setClientFactory(this);
		}
		return navManager;
	}
	
	@Override
	public native void console(String text)
	/*-{
	    console.log(text);
	}-*/;

}
