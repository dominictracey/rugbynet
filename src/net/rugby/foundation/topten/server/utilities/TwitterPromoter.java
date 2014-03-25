package net.rugby.foundation.topten.server.utilities;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONTokener;



import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;


import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;

public class TwitterPromoter implements IPromotionHandler {

	@Override
	public String process(ITopTenList ttl) {
		String retval = "******** TWITTER *********\n";
		String longUrl = "http://www.rugby.net/fb/topten.html?listId="+ttl.getId() + "\"";
		for (ITopTenItem i: ttl.getList()){
			String player = i.getPlayer().getDisplayName();
			String tweet = i.getPlayer().getTwitterHandle() + " You've made our Top Ten List! How about a RT?\"";
			if (tweet != null);{
				try {
					retval += "<a href=\"http://twitter.com/home?status="+ tweet + createShortUrl(longUrl) +">"+ player +"</a>\n" + "\n";
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return retval;
	}
	public String createShortUrl(String longUrl) throws Exception {
		try {
			ArrayList<String> scopes = new ArrayList<String>();
			scopes.add("https://www.googleapis.com/auth/urlshortener");
			AppIdentityService appIdentity = AppIdentityServiceFactory.getAppIdentityService();
			AppIdentityService.GetAccessTokenResult accessToken = appIdentity.getAccessToken(scopes);
			// The token asserts the identity reported by appIdentity.getServiceAccountName()
			JSONObject request = new JSONObject();
			request.put("longUrl", longUrl);

			URL url = new URL("https://www.googleapis.com/urlshortener/v1/url?pp=1");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.addRequestProperty("Content-Type", "application/json");
			connection.addRequestProperty("Authorization", "OAuth " + accessToken.getAccessToken());

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			request.write(writer);
			writer.close();

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// Note: Should check the content-encoding.
				InputStream stream = connection.getInputStream();
				JSONTokener response_tokens = new org.json.JSONTokener(stream);
				JSONObject response = new JSONObject(response_tokens);
				return (String) response.get("id");
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			// Error handling elided.
			throw e;
		}
	}
}
