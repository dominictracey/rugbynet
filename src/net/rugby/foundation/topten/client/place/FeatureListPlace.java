package net.rugby.foundation.topten.client.place;

import net.rugby.foundation.model.shared.IServerPlace;

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
public class FeatureListPlace extends Place {
  
	private String token;
	private String seps = "&|=";
	private Long compId = null;
	private Long listId = null;
	private Long itemId = null;
	
	
	public FeatureListPlace(String token) {
		this.token = token;
		
		String[] tok = token.split(seps);
		
		if (tok.length > 1) {
			if (tok[0].equals("l")) {
				String slistId = URL.decode(tok[1]);
				listId = Long.parseLong(slistId);
			}   else if (tok[0].equals("c")) {
				String str = URL.decode(tok[1]);
				compId = Long.parseLong(str);
			}   else if (tok[0].equals("i")) {
				String str = URL.decode(tok[1]);
				itemId = Long.parseLong(str);
			}
		}
		
		if (tok.length > 3) {
			if (tok[2].equals("l")) {
				String slistId = URL.decode(tok[3]);
				listId = Long.parseLong(slistId);
			}   else if (tok[2].equals("c")) {
				String str = URL.decode(tok[3]);
				compId = Long.parseLong(str);
			}   else if (tok[2].equals("p")) {
				String str = URL.decode(tok[3]);
				itemId = Long.parseLong(str);
			}
		}

		if (tok.length > 5) {
			if (tok[4].equals("l")) {
				String slistId = URL.decode(tok[5]);
				listId = Long.parseLong(slistId);
			}   else if (tok[4].equals("c")) {
				String str = URL.decode(tok[5]);
				compId = Long.parseLong(str);
			}   else if (tok[4].equals("p")) {
				String str = URL.decode(tok[5]);
				itemId = Long.parseLong(str);
			}
		}
		
		
	}

	public FeatureListPlace() {
		
	}


	public FeatureListPlace(Long compId, Long listId, Long itemId) {
		this.compId = compId;
		this.listId = listId;
		this.itemId = itemId;
	}
	
	public FeatureListPlace(IServerPlace sp) {
		compId = sp.getCompId();
		listId = sp.getListId();
		itemId = sp.getItemId();
	}

	public String getToken() {
		token = "";
		boolean started = false;
		
		if (!(compId == null)) {
			token += "c=";
			token += compId;
			started = true;
		}
		
		if (!(listId == null)) {
			if (started) {
				token += "&";
			} else {
				started = true;
			}
			token += "l=";
			token += listId;
		}
		
		if (!(itemId == null)) {
			if (started) {
				token += "&";
			} else {
				started = true;
			}
			token += "p=";
			token += itemId;
		}

		return token;

	}
	
	public Long getListId() { return listId; }
	public Long getItemId() { return itemId; }

	public Long getCompId() {
		return compId;
	}


	public void setCompId(Long compId) {
		this.compId = compId;
	}

	/**
	 * PlaceTokenizer knows how to serialize the Place's state to a URL token.
	 */
	@Prefix(value="Fx")
	public static class Tokenizer implements PlaceTokenizer<FeatureListPlace> {

		@Override
		public String getToken(FeatureListPlace place) {
			return place.getToken();
		}

		@Override
		public FeatureListPlace getPlace(String token) {
			return new FeatureListPlace(token);
		}

	}

	public void setListId(Long id) {
		this.listId = id;
		
	}
}
