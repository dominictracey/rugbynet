/**
 * 
 */
package net.rugby.foundation.core.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import net.rugby.foundation.model.shared.LoginInfo;
import com.google.appengine.api.utils.SystemProperty;


/**
 * @author home
 *
 */
public class FacebookProvider implements IExternalAuthenticatorProvider {

	private HttpServletRequest req;

	private final static class Constants { //implements IConstants {

		public static final String FACEBOOK_CODE_URL="https://www.facebook.com/dialog/oauth?client_id=";
		public static final String FACEBOOK_ACCESS_TOKEN_URL="https://graph.facebook.com/oauth/access_token?client_id=";
		public static final String FACEBOOK_GRAPH_URL = "https://graph.facebook.com/me?access_token=";

		// facebook app Ids - @TODO not sure why the second one works local. See GWTFB.
		public static final String FACEBOOK_DEV_APP_ID="499268570161982";
		private static final String FACEBOOK_BETA_APP_ID="401094413237618";
		public static final String FACEBOOK_PROD_APP_ID="191288450939341";
		public static final String FACEBOOK_DEV_SECRET="c3550da86a7233c5398129a2b1317495";
		private static final String FACEBOOK_BETA_SECRET="9a496ea2691617af45c9ee1bd8a52ccb";
		public static final String FACEBOOK_PROD_SECRET="b6fffe19623cae54377c2bfaf8f65efb";

		public static final String FACEBOOK_REDIRECT_KEY = "&redirect_uri=";
		public static final String FACEBOOK_LOCAL_REDIRECT_VAL = "/login/facebook/0/";
		public static final String FACEBOOK_LOCAL_DEV_CODESERVER = "gwt.codesvr=127.0.0.1%3A9997";
		public static final String FACEBOOK_LOCAL_REDIRECT_KEY = "destination=";

		public static String getFacebookCodeUrl() {
			return FACEBOOK_CODE_URL;
		}

		public static String getFacebookAccessTokenUrl() {
			return FACEBOOK_ACCESS_TOKEN_URL;
		}

		public static String getFacebookGraphUrl() {
			return FACEBOOK_GRAPH_URL;
		}

		public static String getFacebookDevAppId() {
			return FACEBOOK_DEV_APP_ID;
		}

		public static String getFacebookProdAppId() {
			return FACEBOOK_PROD_APP_ID;
		}

		public static String getFacebookLocalRedirectKey() {
			return FACEBOOK_LOCAL_REDIRECT_KEY;
		}

		public static String getFacebookLocalDevCodeserver() {
			return FACEBOOK_LOCAL_DEV_CODESERVER;
		}

		public static String getFacebookRedirectKey() {
			return FACEBOOK_REDIRECT_KEY;
		}

		public static String getFacebookLocalRedirectVal() {
			return FACEBOOK_LOCAL_REDIRECT_VAL;
		}

		public static String getFacebookProdSecret() {
			return FACEBOOK_PROD_SECRET;
		}

		public static String getFacebookDevSecret() {
			return FACEBOOK_DEV_SECRET;
		}

		public static String getFacebookBetaAppId() {
			return FACEBOOK_BETA_APP_ID;
		}

		public static String getFacebookBetaSecret() {
			return FACEBOOK_BETA_SECRET;
		}

	}

	public static class Base64Helper {
		static String decode(String input) {
			// add the padding back on and decode
			for (int i=0; i < (4 - input.length() % 4) % 4; ++i) {
				input += "="; 
			}
			
			//TODO REX Took out to get compiling with Maven commons-codec
			return new String(Base64.decodeBase64(input));
//			return new String();

		}

		static String encode(String input) {
			String dest = new String(Base64.encodeBase64(input.getBytes()));
			//strip the padding off before we return
			return dest.replace("=","");
		}
	}

	//private static Constants constants;
	//	UserService userService = UserServiceFactory.getUserService();
	//	User user = userService.getCurrentUser(); // or req.getUserPrincipal()
	private IAccountManager am;
	private String destination = "";
	//private HttpServletResponse resp;

	private String emptyDest = Base64Helper.encode("null");
	/**
	 * @param auf
	 * @param am
	 * @param destination 
	 */
	public FacebookProvider(IAccountManager am, String destination) {

		this.am = am;
		this.destination = destination;
	}

	@SuppressWarnings("unused")
	private FacebookProvider() {
		//call the other one
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
		return  Constants.getFacebookLocalRedirectVal() + "?" + Constants.getFacebookLocalRedirectKey() +  dest;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IExternalAuthenticatorProvider#getExternalURL(java.lang.String, java.lang.String)
	 */
	@Override
	public String getExternalURL() {
		return null; // use the others
	}


	private String getCodeUrl() {
		String providerUrl = null;
		String appId = null;


		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
			appId = Constants.getFacebookDevAppId();
		} else {
			if (req.getServerName().contains("beta")) {
				appId = Constants.getFacebookBetaAppId();
			} else {
				appId = Constants.getFacebookProdAppId();
			}
		}
		// https://www.facebook.com/dialog/oauth?client_id=YOUR_APP_ID&redirect_uri=YOUR_URL&scope=email,read_stream
		try {
			providerUrl = Constants.getFacebookCodeUrl() + appId  + "&scope=email" + Constants.getFacebookRedirectKey() +  getLocalRedirectValue();
		} catch (UnsupportedEncodingException e) {
			Logger.getLogger("FacebookProvider").log(Level.SEVERE,e.getLocalizedMessage(),e);
		}

		return providerUrl;		
	}

	private String getAccessTokenUrl(String code, String server) {
		String providerUrl = null;

		String appId = null;
		String secret = null;
		// use it to get the access token
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
			appId = Constants.getFacebookDevAppId();
			secret = Constants.getFacebookDevSecret();
		} else {
			if (server.contains("beta")) {
				appId = Constants.getFacebookBetaAppId();
				secret = Constants.getFacebookBetaSecret();
			} else {
				appId = Constants.getFacebookProdAppId();
				secret = Constants.getFacebookProdSecret();
			}
		}
		// https://graph.facebook.com/oauth/access_token?
		// client_id=YOUR_APP_ID&redirect_uri=YOUR_URL&
		//	     client_secret=YOUR_APP_SECRET&code=THE_CODE_FROM_ABOVE
		try {
			providerUrl = Constants.getFacebookAccessTokenUrl() + appId + "&client_secret=" + secret + "&code=" + code + Constants.getFacebookRedirectKey() + getLocalRedirectValue();
		} catch (UnsupportedEncodingException e) {
			Logger.getLogger("FacebookProvider").log(Level.SEVERE,e.getLocalizedMessage(),e);
		}

		return providerUrl;		
	}

	private String getLocalRedirectValue() throws UnsupportedEncodingException {
		String server = req.getServerName();
		String dev_gwtserver = "";
		String port = "";
		if (req.getLocalPort() != 80 && req.getLocalPort() != 0) {
			port = ":" + Integer.toString(req.getLocalPort());
		}
//		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
//			dev_gwtserver += Constants.getFacebookLocalDevCodeserver();
//		}

		if (destination == null) {
			destination = "";
		}

		String url = "http://" + server + port + Constants.getFacebookLocalRedirectVal()  + "?" +

			 Constants.getFacebookLocalRedirectKey() + Base64Helper.encode(destination) +
			 "&" + dev_gwtserver ;
		//"&cheese=cheese" ;

		return url;
		//return URLEncoder.encode(url, resp.getCharacterEncoding());
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IExternalAuthenticatorProvider#handleLogin(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		this.req = req;
		//this.resp = resp;

		// get the code out of the parameters
		String code = req.getParameter("code");

		if (code == null || code.isEmpty()) { // this is the first call from the client - just build them the initial redirect URL for facebook
			String codeUrl = getCodeUrl();
			resp.sendRedirect(codeUrl);
		} else { // this is the redirect from facebook
			// get the access token
			String accessTokenUrlString = getAccessTokenUrl(code,req.getServerName());
			URL url = new URL(accessTokenUrlString);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;

			String accessToken = "";
			while ((line = reader.readLine()) != null) {
				if (line.contains("access_token")) {
					String seps="[=&]";
					String tok[] = line.split(seps);
					accessToken = tok[1];
				}
			}

			assert !accessToken.isEmpty();
			if (accessToken == null || accessToken.isEmpty()) {
				// if they didn't authorize just send them to the destination
				String server = req.getServerName();
				String port = "";
				if (req.getLocalPort() != 80 && req.getLocalPort() != 0) {
					port = ":" + Integer.toString(req.getLocalPort());
				}

				if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
					String dev_gwtserver = ""; //Constants.getFacebookLocalDevCodeserver();
					resp.sendRedirect("http://" + server + port + "/" + dev_gwtserver + "#" + destination);
				}
			} else {
				try {

					// now that we have the access token we can get the data
					url = new URL(Constants.getFacebookGraphUrl() + accessToken);
					BufferedReader userInfoReader = new BufferedReader(new InputStreamReader(url.openStream()));
					StringBuilder builder = new StringBuilder();
					while ((line = userInfoReader.readLine()) != null) {
						builder.append(line).append("\n");
					}
					JSONTokener tokener = new JSONTokener(builder.toString());
					JSONObject attributes = new JSONObject(tokener);

					// find the email in the array
					String email = attributes.getString("email");

					assert (email != null);
					am.processUser(email, LoginInfo.ProviderType.facebook, null, req, resp, attributes);

				} catch (JSONException e) {
					Logger.getLogger("Login Servlet").log(Level.SEVERE,e.getLocalizedMessage(),e);
				} catch (NumberFormatException e) {
					Logger.getLogger("Login Servlet").log(Level.SEVERE,e.getLocalizedMessage(),e);
				} catch (ParseException e) {
					Logger.getLogger("Login Servlet").log(Level.SEVERE,e.getLocalizedMessage(),e);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IExternalAuthenticatorProvider#setSelector(net.rugby.foundation.core.client.Identity.Selector)
	 */
	@Override
	public void setSelector(LoginInfo.Selector selector) {
		// ignore

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.IExternalAuthenticatorProvider#setDestination(java.lang.String)
	 */
	@Override
	public void setDestination(String destination) {
		this.destination = destination;

	}



}
