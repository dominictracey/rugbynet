package net.rugby.foundation.topten.client.place;

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
public class TopTenListPlace extends Place {
  
	private String token;
	private String seps = "&|=";
	private Long compId = null;
	private Long listId = null;
	private Long playerId = null;
	
	
	public TopTenListPlace(String token) {
		this.token = token;
		
		String[] tok = token.split(seps);
		
		if (tok.length > 1) {
			if (tok[0].equals("listId")) {
				String SlistId = URL.decode(tok[1]);
				listId = Long.parseLong(SlistId);
			} else if (tok[0].equals("compId")) {
				String SlistId = URL.decode(tok[1]);
				compId = Long.parseLong(SlistId);
			} 
		}
		
		if (tok.length > 3) {
			if (tok[2].equals("playerId")) {
				String SplayerId = URL.decode(tok[3]);	
				playerId = Long.parseLong(SplayerId);
			} else if (tok[2].equals("compId")) {
				String SlistId = URL.decode(tok[3]);
				compId = Long.parseLong(SlistId);
			} 
		}
		
		
	}

	
	public TopTenListPlace(Long listId) {
		this.listId = listId;
	}


	public TopTenListPlace() {
		
	}


	public String getToken() {
		token = "";
		
		if (!(listId == null)) {
			token += "listId=";
			token += listId;
		}
		
		if (!(compId == null)) {
			token += "compId=";
			token += compId;
		}
		
		if (!(playerId == null)) {
			token += "&playerId=";
			token += playerId;
		}
		
		return token;
	}
	
	public Long getListId() { return listId; }
	public Long getPlayerId() { return playerId; }

	public Long getCompId() {
		return compId;
	}


	public void setCompId(Long compId) {
		this.compId = compId;
	}

	/**
	 * PlaceTokenizer knows how to serialize the Place's state to a URL token.
	 */
	@Prefix(value="List")
	public static class Tokenizer implements PlaceTokenizer<TopTenListPlace> {

		@Override
		public String getToken(TopTenListPlace place) {
			return place.getToken();
		}

		@Override
		public TopTenListPlace getPlace(String token) {
			return new TopTenListPlace(token);
		}

	}
}
