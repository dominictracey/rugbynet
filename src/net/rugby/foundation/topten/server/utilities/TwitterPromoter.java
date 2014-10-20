package net.rugby.foundation.topten.server.utilities;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.gwt.user.client.Window.Location;

import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.CoreConfiguration.Environment;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;
import net.rugby.foundation.topten.server.utilities.IPromotionHandler;

public class TwitterPromoter implements IPromotionHandler {

	private ITeamGroupFactory tgf;
	private ITopTenListFactory ttlf;
	private IConfigurationFactory ccf;

	public TwitterPromoter(ITeamGroupFactory tgf, ITopTenListFactory ttlf, IConfigurationFactory ccf) {
		this.tgf = tgf;
		this.ttlf = ttlf;
		this.ccf = ccf;
	}

	@Override
	public String process(ITopTenList ttl) {

		String retval = "<p>******** TWITTER *********</p>\n";
		String longUrl = "http://rugby.net/s/" + ttl.getGuid(); // "http://www.rugby.net/fb/topten.html?listId="+ttl.getId()+"#List:listId="+ttl.getId();

		String shortURL = "short url";
		
		if (ccf.get().getEnvironment().equals(Environment.LOCAL)) {
			longUrl += "?gwt.codesvr=127.0.0.1:9997";
			shortURL = longUrl;
		} else {
			
			try {
				shortURL = createShortUrl(longUrl);
			}
			catch (Exception ex) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Problem Processing Twitter Promotion", ex);	
			}
		}
		for (ITopTenItem i: ttl.getList()) {

			//if (i.getPlayer().getTwitterHandle() != null && !i.getPlayer().getTwitterHandle().isEmpty()) {
			String player = i.getPlayer().getDisplayName();
			String tweet = buildTweet(i,ttl); //i.getPlayer().getTwitterHandle() + " you've made @TheRugbyNet " + ttl.getTitle() + "! ";
			i.setTweet(tweet);
			ttlf.put(i);
			String URL ="http://twitter.com/home?status="+ URLEncoder.encode(tweet + shortURL);
			retval += "<p><a href=\"" + URL +"\">"+ player +"</a></p>\n" + "\n";
			//}
		}

		return retval;

	}
	public String createShortUrl(String longUrl) {
		if (!longUrl.contains("codesvr")) {
			JSONObject request = new JSONObject();
			AppIdentityService.GetAccessTokenResult accessToken = null;
			HttpURLConnection connection = null;

			try {
				ArrayList<String> scopes = new ArrayList<String>();
				scopes.add("https://www.googleapis.com/auth/urlshortener");
				AppIdentityService appIdentity = AppIdentityServiceFactory.getAppIdentityService();
				accessToken = appIdentity.getAccessToken(scopes);
				// The token asserts the identity reported by appIdentity.getServiceAccountName()

				request.put("longUrl", longUrl);
			} catch (Exception e) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Problem in Twitter Promotion establishing identity: " + e.getLocalizedMessage(), e);
				return "short url";
			}
			try {
				URL url = new URL("https://www.googleapis.com/urlshortener/v1/url?pp=1");
				connection = (HttpURLConnection) url.openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.addRequestProperty("Content-Type", "application/json");
				connection.addRequestProperty("Authorization", "OAuth " + accessToken.getAccessToken());
				OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
				request.write(writer);
				writer.close();

			} catch (Exception e) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Problem in Twitter Promotion establishing goo.gl connection: " + e.getLocalizedMessage(), e);
				return "short url";
			}
			try {
				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					// Note: Should check the content-encoding.
					InputStream stream = connection.getInputStream();
					JSONTokener response_tokens = new org.json.JSONTokener(stream);
					JSONObject response = new JSONObject(response_tokens);
					return (String) response.get("id");
				} else {
					throw new Exception("Got bad response code " + connection.getResponseCode() + " and message " + connection.getResponseMessage() + " trying to use access token " + accessToken.getAccessToken());
				}
			} catch (Exception e) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Problem in Twitter Promotion reading response: " + e.getLocalizedMessage(), e);
				return "short url";
			}
		} else {
			// local dev
			return longUrl;
		}
	}

	String buildTweet(ITopTenItem i, ITopTenList l) {
		String tweet = "";
		// start with player twitter handle or display name
		if (i.getPlayer().getTwitterHandle() == null || i.getPlayer().getTwitterHandle().isEmpty()) {
			tweet += i.getPlayer().getDisplayName() + " ";
		} else {
			tweet += i.getPlayer().getTwitterHandle() + " ";
		}

		// add in team twitter handle if available
		ITeamGroup team = tgf.get(i.getTeamId());
		if (team != null) {
			if (team.getTwitter() != null && !team.getTwitter().isEmpty()) {
				tweet += "of " + team.getTwitter(); 
			} else {
				tweet += "of " + team.getShortName();
			}
		}

		tweet += " is on @TheRugbyNet " + l.getTwitterDescription();

		return tweet; 
	}
}
