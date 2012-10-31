/**
 * 
 */
package net.rugby.foundation.core.client;

import net.rugby.foundation.model.shared.LoginInfo;

/**
 * @author home
 *
 */
public class CoreClientFactoryImpl implements CoreClientFactory {
	final CoreServiceAsync rpcService = ServicesFactory.rpcService;//GWT.create(CoreService.class);//
	private LoginInfo loginInfo = null;
	private Identity identity = null;
	
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

}
