package net.rugby.foundation.admin.client.place;

import java.util.HashMap;
import java.util.Map;

import net.rugby.foundation.admin.shared.IAdminTask;

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
public class AdminTaskPlace extends Place {
  
	private String token;
	private String seps = "&|=";
	private String taskId = "";
	private String filter = "";
//	private String name;
//	private String referringURL;
//	private Map<String,String> map;
	
	
	public AdminTaskPlace(String token) {
		this.token = token;
		
		String[] tok = token.split(seps);
		
		if (tok.length > 1) {
			if (tok[0].equals("taskId")) {
				taskId = URL.decode(tok[1]);	
			} else if (tok[0].equals("filter")) {
				filter = URL.decode(tok[1]);	
			} 
		}
		
		if (tok.length > 3) {
			if (tok[2].equals("taskId")) {
				taskId = URL.decode(tok[3]);	
			} else if (tok[2].equals("filter")) {
				filter = URL.decode(tok[3]);	
			} 
		}
		
	//	map = getParameterMap();
		
	}
	
//	public Map<String,String> getParameterMap() {
//	       String[] params = token.split("&");
//	        map = new HashMap<String, String>();
//	        if (!(params.length < 2)) {
//		        for (String param : params)
//		        {
//		            String name = param.split("=")[0];
//		            String value = param.split("=")[1];
//		            map.put(name, value);
//		        }
//	        }
//	        return map;
//	}

	
	public String getToken() {
		token = "";
		
		if (!taskId.isEmpty()) {
			token += "taskId=";
			token += taskId;
		}
		
		if (!filter.isEmpty()) {
			token += "filter=";
			token += filter;
		}
		
//		for (String key : map.keySet()) {
//			token += "&" + key + "=" + map.get(key);
//		}
		
		return token;
	}
	
	public String getTaskId() { return taskId; }
	public String getFilter() { return filter; }

	/**
	 * PlaceTokenizer knows how to serialize the Place's state to a URL token.
	 */
	@Prefix(value="AdminTaskPlace")
	public static class Tokenizer implements PlaceTokenizer<AdminTaskPlace> {

		@Override
		public String getToken(AdminTaskPlace place) {
			return place.getToken();
		}

		@Override
		public AdminTaskPlace getPlace(String token) {
			return new AdminTaskPlace(token);
		}

	}
}
