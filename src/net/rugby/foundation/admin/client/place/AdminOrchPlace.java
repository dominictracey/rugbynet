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
public class AdminOrchPlace extends Place {
  
	private String token;
	private String seps = "&|=";
	private String taskId;

	private Map<String,String> map;
	
	
	public AdminOrchPlace(String token) {
		this.token = token;
		
		String[] tok = token.split(seps);
		
		if (tok.length > 1) {
			assert (tok[0].equals("taskId"));
			taskId = URL.decode(tok[1]);	
			
		}
		
//		if (tok.length > 3) {
//			assert (tok[2].equals("name"));
//			name = URL.decode(tok[3]);
//		}
//		
//		if (tok.length > 5) {
//			assert (tok[4].equals("referringURL"));
//			referringURL = URL.decode(tok[5]);
//		}
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
		token += "taskId=";
		token += taskId;
		for (String key : map.keySet()) {
			token += "&" + key + "=" + map.get(key);
		}
		
//		token += "&name=";
//		token += name;
//		token += "&referringURL=";
//		token += referringURL;
		
		return token;
	}
	
	public String getTaskId() { return taskId; }
//	public String getName() { return name; }
//	public String getreferringURL() { return referringURL; }

	/**
	 * PlaceTokenizer knows how to serialize the Place's state to a URL token.
	 */
	@Prefix(value="AdminOrchPlace")
	public static class Tokenizer implements PlaceTokenizer<AdminOrchPlace> {

		@Override
		public String getToken(AdminOrchPlace place) {
			return place.getToken();
		}

		@Override
		public AdminOrchPlace getPlace(String token) {
			return new AdminOrchPlace(token);
		}

	}
}
