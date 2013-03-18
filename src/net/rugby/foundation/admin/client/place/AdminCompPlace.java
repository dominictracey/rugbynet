package net.rugby.foundation.admin.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * A place object representing a particular state of the UI. A Place can be converted to and from a
 * URL history token by defining a {@link PlaceTokenizer} for each {@link Place}, and the 
 * {@link PlaceHistoryHandler} automatically updates the browser URL corresponding to each 
 * {@link Place} in your app.
 */
public class AdminCompPlace extends Place {
  
	private String token;

	public AdminCompPlace(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	/**
	 * PlaceTokenizer knows how to serialize the Place's state to a URL token.
	 */
	@Prefix(value="AdminCompPlace")
	public static class Tokenizer implements PlaceTokenizer<AdminCompPlace> {

		@Override
		public String getToken(AdminCompPlace place) {
			return place.getToken();
		}

		@Override
		public AdminCompPlace getPlace(String token) {
			return new AdminCompPlace(token);
		}

	}
}
