/**
 * 
 */
package net.rugby.foundation.core.server;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.utils.SystemProperty;
import com.google.inject.Inject;
import net.rugby.foundation.core.shared.IdentityTypes;
import net.rugby.foundation.core.shared.IdentityTypes.Actions;
import net.rugby.foundation.core.shared.IdentityTypes.Keys;
//import net.rugby.foundation.core.client.Identity.Actions;
//import net.rugby.foundation.core.client.Identity.Keys;
import net.rugby.foundation.core.server.FacebookProvider.Base64Helper;
import net.rugby.foundation.model.shared.LoginInfo;


/**
 * @author home
 *
 */
public class ExternalAuthenticatorFactory implements
		IExternalAuthticatorProviderFactory {
	
	private IAccountManager am;

	@Inject
	public ExternalAuthenticatorFactory(IAccountManager am) {
		this.am = am;
	}


//	public String getUrl(Actions action, LoginInfo.ProviderType providerType, LoginInfo.Selector selector, String destination) {
//		String retval = null; 
//		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) 
//			retval = "/topten.html?gwt.codesvr=127.0.0.1:9997#Profile:";
//		else
//			retval = "/topten.html?#Profile:";
//
//
//		
//		if (action != null)
//			retval += Keys.action.toString() + "=" + action.toString();	
//		
//		if (providerType != null) {
//			retval += "&" + Keys.providerType.toString() + "=" + providerType.toString();
//		}
//		
//		if (selector != null) {
//			retval += "&" + Keys.selector.toString() + "=" + selector.toString();					
//		}
//		if (destination != null) {
////			try {
//				retval += "&" + Keys.destination.toString() + "=" + destination;// + URLEncoder.encode(destination, "UTF-8");
////			} catch (UnsupportedEncodingException e) {
////				Logger.getLogger("AccountManager").log(Level.SEVERE, e.getLocalizedMessage(), e);
////			}			
//		}
//		return  retval;
//	}
//		
	@Override
	public IExternalAuthenticatorProvider get(HttpServletRequest req) {
		
		String tok[] = req.getRequestURL().toString().split("[/?=]");
		// we should have 8 tokens:
		// http:
		// blank
		// server:port
		// "login"
		// providerType
		// selector
		// "destination"
		// urlencoded destination
		
		// may be 8?
//		assert tok.length == 6;
//		if (tok.length != 6) 
//			return null;
		
		assert (tok[3].toLowerCase().equals("login"));
		if (!tok[3].toLowerCase().equals("login"))
			return null;
		
//		assert (!req.getParameter("destination").isEmpty());
//		if (req.getParameter("destination").isEmpty())
//			return null;
		
		assert (tok[4].toLowerCase().equals(LoginInfo.ProviderType.openid.toString()) || tok[4].toLowerCase().equals(LoginInfo.ProviderType.facebook.toString()) || tok[4].toLowerCase().equals(LoginInfo.ProviderType.oauth2.toString()));

		String providerTypeS = tok[4];
		LoginInfo.ProviderType providerType = IdentityTypes.getProviderType(providerTypeS);

		
		String destination = req.getParameter("destination");

		if (providerType.equals(LoginInfo.ProviderType.oauth2)) {
			if (destination != null)
				destination = Base64Helper.decode(destination);
			return get(LoginInfo.ProviderType.oauth2, null, destination);
		} else if (providerType.equals(LoginInfo.ProviderType.facebook)) {
			if (destination != null)
				destination = Base64Helper.decode(destination);
			return get(LoginInfo.ProviderType.facebook, null, destination);
		} else { //OpenId
			if (destination != null)
				destination = Base64Helper.decode(destination);
			assert (tok.length > 4);
			String selectorS = tok[5];
			LoginInfo.Selector selector = IdentityTypes.getSelector(selectorS);
			return get(LoginInfo.ProviderType.openid, selector, destination);
		}
	}
		
	

	public static final Map<LoginInfo.Selector, String> openIdProviders;
	static {
		openIdProviders = new HashMap<LoginInfo.Selector, String>();
		openIdProviders.put(LoginInfo.Selector.google, "www.google.com/accounts/o8/id");
		openIdProviders.put(LoginInfo.Selector.googlePlus, "accounts.google.com/o/oauth2/auth");
		openIdProviders.put(LoginInfo.Selector.yahoo, "yahoo.com");
		openIdProviders.put(LoginInfo.Selector.myspace, "myspace.com");
		openIdProviders.put(LoginInfo.Selector.aol, "aol.com");
		openIdProviders.put(LoginInfo.Selector.myopenid_com, "myopenid.com");
	}
	

	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IExternalAuthticatorProviderFactory#get(java.lang.String)
	 */
	@Override
	public IExternalAuthenticatorProvider get(LoginInfo.ProviderType type, LoginInfo.Selector selector, String destination) {
		if (type.equals(LoginInfo.ProviderType.facebook)) {
			return new FacebookProvider(am, destination);
		} else if (type.equals(LoginInfo.ProviderType.openid)) {
			if (openIdProviders.containsKey(selector))
				return new OpenIdProvider(selector, am, destination);
		}  else if (type.equals(LoginInfo.ProviderType.oauth2)) {
				return new OAuth2Provider(am, destination);
		}
		return null;
	}

}
