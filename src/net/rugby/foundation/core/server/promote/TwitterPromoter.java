package net.rugby.foundation.core.server.promote;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.CoreConfiguration.Environment;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.inject.Inject;

public class TwitterPromoter implements IPromoter {

	private final static String BUFFER_CREATE_URL = "https://api.bufferapp.com/1/updates/create.json";
	private int bufferCount;

	private ITopTenListFactory ttlf;
	private IPlayerFactory pf;
	private ITeamGroupFactory tgf;
	private IConfigurationFactory ccf;

	@Inject
	public TwitterPromoter(ITopTenListFactory ttlf, IPlayerFactory pf, ITeamGroupFactory tgf, IConfigurationFactory ccf) {
		this.ttlf = ttlf;
		this.pf = pf;
		this.tgf = tgf;
		this.ccf = ccf;
	}

	@Override
	public List<IPlayer> promoteList(Long ttlId) {
		try {	
			ICoreConfiguration cc = ccf.get();

			List<IPlayer> playerList = new ArrayList<IPlayer>();
			ITopTenList ttl = ttlf.get(ttlId);
			String retval = "<h3>Tweet results for " + ttl.getTitle() + "</h3>";

			if (ttl != null) {
				for (ITopTenItem tti : ttl.getList()) {
					if (cc.getEnvironment().equals(Environment.PROD) || cc.getEnvironment().equals(Environment.LOCAL)) {
						retval += promoteItem(tti, ttl);
					}
					playerList.add(tti.getPlayer());
				}
			}

			retval += "<hr>Buffer count: " + bufferCount;
			return playerList;

		}  catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getLocalizedMessage(),e);
			return null;
		}
	}

	@Override
	public String promoteItem(ITopTenItem tti, ITopTenList ttl) {
		try {

			// is this a recent save?
			if (tti.getPlayer().getTwitterHandle() == null) { 			//TODO - sometimes this line throws a null pointer exception
				IPlayer p = pf.get(tti.getPlayerId());
				ITeamGroup t = tgf.get(tti.getTeamId());
				if (p.getTwitterHandle() != null && !p.getTwitterHandle().isEmpty()) {
					tti.setTweet(p.getTwitterHandle() + " of " + t.getTwitter());
					tti.getPlayer().setTwitterHandle(p.getTwitterHandle());
				}
			}

			if (tti.getPlayer().getTwitterHandle() == null || tti.getPlayer().getTwitterHandle().isEmpty()) {
				return tti.getPlayer().getDisplayName() + ": no twitter handle<br>";
			}
			String charset = java.nio.charset.StandardCharsets.UTF_8.name();

			String listDesc = ttl.getTwitterDescription();
			if (listDesc == null || listDesc.isEmpty()) {
				listDesc = ttl.getTitle();
			}

			String guid = "";
			// this will always drive users to the feature, even if they share the series link (a good thing?)
			if (tti.getFeatureGuid() == null) {
				guid = tti.getPlaceGuid();
			} else {
				guid = tti.getFeatureGuid();
			}

			String tweet = "";

			// #1 gets a dot
			if (tti.getOrdinal() == 1) {
				tweet += ".";
			}

			tweet += tti.getTweet() + " is #" + tti.getOrdinal() + " on @TheRugbyNet " + listDesc;
			if (tti.getTwitterChannel() != null && !tti.getTwitterChannel().isEmpty() && tweet.length() < 115 - tti.getTwitterChannel().length()) {
				tweet += " " + tti.getTwitterChannel();
			}				

			StringBuilder params = new StringBuilder();
			params.append("access_token=1/4266c2423560ded98f0b532cac894c07");
			params.append("&profile_ids[]=53f4ab03c4320ad025b8ee70");
			params.append("&text=" + URLEncoder.encode(tweet,charset));
			params.append("&shorten=false");
			params.append("&profile_service=facebook");
			String link = ccf.get().getBaseToptenUrl() + "s/" + guid;
			params.append("&media[link]=" + URLEncoder.encode(link,charset));
			//params.append("media[title]=" + URLEncoder.encode(b.getLinkText(),charset));

			byte[] postData = params.toString().getBytes(charset);
			int    postDataLength = postData.length;

			URL url = new URL(BUFFER_CREATE_URL);
			HttpURLConnection conn= (HttpURLConnection) url.openConnection();           
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects( false );
			conn.setRequestMethod( "POST" );
			conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
			conn.setRequestProperty( "charset", charset);
			conn.setRequestProperty( "Content-Length", Integer.toString(postDataLength));
			conn.setUseCaches( false );

			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.write(postData);
			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));

			StringBuilder sb = new StringBuilder();
			for (int c; (c = in.read()) >= 0;)
				sb.append((char)c);
			String response = sb.toString();

			Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.INFO);
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,response);
			
			JSONObject json = new JSONObject(response);
			bufferCount = json.getInt("buffer_count");
			if (json.getBoolean("success")) {
				return tti.getPlayer().getDisplayName() + ": success<br>\n";
			} else {
				return tti.getPlayer().getDisplayName() + ": failed<br>\n";
			}

		} catch (IOException ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return ex.getMessage();
		}  catch (JSONException ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return ex.getMessage();
		}

		//	else {
		//			return "Not logged in as admin. Please contact info@rugby.net immediately.";
		//		}
	}
}
