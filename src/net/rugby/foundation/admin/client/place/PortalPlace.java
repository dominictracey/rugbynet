package net.rugby.foundation.admin.client.place;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * A place object representing a particular state of the UI. A Place can be converted to and from a
 * URL history token by defining a {@link PlaceTokenizer} for each {@link Place}, and the 
 * {@link PlaceHistoryHandler} automatically updates the browser URL corresponding to each 
 * {@link Place} in your app.
 */
public class PortalPlace extends Place {
  
	private String token;
	private String seps = "&|=";
	private String queryId;
	private Map<String,String> map;
	
	
	public PortalPlace(String token) {
		this.token = token;
		
		String[] tok = token.split(seps);
		
		if (tok.length > 1) {
			assert (tok[0].equals("queryId"));
			queryId = URL.decode(tok[1]);	
			
		}

		map = getParameterMap();
		
	}
	
	public Map<String,String> getParameterMap() {
	       String[] params = token.split("&");
	        map = new HashMap<String, String>();
	        if (!(params.length < 2)) {
		        for (String param : params)
		        {
		            String name = param.split("=")[0];
		            String value = param.split("=")[1];
		            map.put(name, value);
		        }
	        }
	        return map;
	}

	
	public String getToken() {
		token = "";
		token += "queryId=";
		token += queryId;
		for (String key : map.keySet()) {
			token += "&" + key + "=" + map.get(key);
		}
		
		return token;
	}
	
	public String getqueryId() { return queryId; }

	/**
	 * PlaceTokenizer knows how to serialize the Place's state to a URL token.
	 */
	@Prefix(value="PortalPlace")
	public static class Tokenizer implements PlaceTokenizer<PortalPlace> {

		@Override
		public String getToken(PortalPlace place) {
			return place.getToken();
		}

		@Override
		public PortalPlace getPlace(String token) {
			return new PortalPlace(token);
		}

	}
}
