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
public class PromotePlace extends Place {
  
	private String token;
	private String seps = "&|=";
	private Boolean active;
	private Map<String,String> map;
	
	public PromotePlace(Boolean active) {
		this.active = active;
	}
	
	public PromotePlace(String token) {
		this.token = token;
		
		String[] tok = token.split(seps);
		
		if (tok.length > 1) {
			assert (tok[0].equals("active"));
			active = URL.decode(tok[1]).equals("true") ? true : false ;				
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
		token += "active=";
		token += active;
		for (String key : map.keySet()) {
			token += "&" + key + "=" + map.get(key);
		}
		
		return token;
	}
	
	public Boolean getActive() { 
		return active;
	}

	/**
	 * PlaceTokenizer knows how to serialize the Place's state to a URL token.
	 */
	@Prefix(value="PromotePlace")
	public static class Tokenizer implements PlaceTokenizer<PromotePlace> {

		@Override
		public String getToken(PromotePlace place) {
			return place.getToken();
		}

		@Override
		public PromotePlace getPlace(String token) {
			return new PromotePlace(token);
		}

	}
}
