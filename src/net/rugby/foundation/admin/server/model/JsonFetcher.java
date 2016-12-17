package net.rugby.foundation.admin.server.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonFetcher implements IJsonFetcher {
	protected URL url;
	protected String errorMessage;
	protected List<String> warningMessages = new ArrayList<String>();
	protected String errorCode;
	protected List<String> warningCodes = new ArrayList<String>();
	
	public JSONArray get() {
		try {
			
			String charset = java.nio.charset.StandardCharsets.UTF_8.name();

			HttpURLConnection conn= (HttpURLConnection) url.openConnection();           
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects( false );
			conn.setRequestMethod( "GET" );
			conn.setRequestProperty( "charset", charset);
			conn.setUseCaches( false );
			conn.setConnectTimeout(30000); // 30 second timeout
			
			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
			StringBuilder sb = new StringBuilder();
			for (int c; (c = in.read()) >= 0;)
				sb.append((char)c);
			String response = sb.toString();

			Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.INFO);
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,response);
			
			JSONObject json = new JSONObject(response);
			JSONArray retval = json.getJSONArray("Items");
			
			
			if (json.has("error") && !json.isNull("error")) {
				JSONObject errorObj = json.getJSONObject("error");
				errorMessage = errorObj.getString("message");
				errorCode = errorObj.getString("value");
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,errorCode + ":" + errorMessage);
			}
			
			
			if (json.has("warnings") && !json.isNull("warnings")) {
				JSONArray warningArray = json.getJSONArray("warnings");
				for (int i = 0; i < warningArray.length(); ++i) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, warningArray.getJSONObject(i).getString("value") + " : " + warningArray.getJSONObject(i).getString("message"));
					warningMessages.add(warningArray.getJSONObject(i).getString("message"));
					warningCodes.add(warningArray.getJSONObject(i).getString("value"));
				}
				
				
			}
			
			return retval;
			
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getMessage());
			return null;
		} 
	}
	
	public URL getURL() {
		return url;
	}

	public void setURL(URL url) {
		this.url = url;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IJsonFetcher#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IJsonFetcher#getWarningMessage()
	 */
	@Override
	public List<String> getWarningMessages() {
		return warningMessages;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IJsonFetcher#getErrorCode()
	 */
	@Override
	public String getErrorCode() {
		return errorCode;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.model.IJsonFetcher#getWarningCode()
	 */
	@Override
	public List<String> getWarningCodes() {
		return warningCodes;
	}
	
}
