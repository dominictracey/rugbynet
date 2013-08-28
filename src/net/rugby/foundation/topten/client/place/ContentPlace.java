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
public class ContentPlace extends Place {
  
	private String token;
	private String seps = "&|=";
	private Long contentId = null;

	
	
	public ContentPlace(String token) {
		this.token = token;
		
		String[] tok = token.split(seps);
		
		if (tok.length > 1) {
			if (tok[0].equals("contentId")) {
				String SlistId = URL.decode(tok[1]);
				contentId = Long.parseLong(SlistId);
			} 
		}
	}

	
	public ContentPlace(Long contentId) {
		this.contentId = contentId;
	}


	public ContentPlace() {
		
	}


	public String getToken() {
		token = "";
		
		if (!(contentId == null)) {
			token += "contentId=";
			token += contentId;
		}
		
		return token;
	}
	
	
	/**
	 * PlaceTokenizer knows how to serialize the Place's state to a URL token.
	 */
	@Prefix(value="Content")
	public static class Tokenizer implements PlaceTokenizer<ContentPlace> {

		@Override
		public String getToken(ContentPlace place) {
			return place.getToken();
		}

		@Override
		public ContentPlace getPlace(String token) {
			return new ContentPlace(token);
		}

	}

	public Long getContentId() { return contentId; }

	public void setContentId(Long id) {
		this.contentId = id;
		
	}
}
