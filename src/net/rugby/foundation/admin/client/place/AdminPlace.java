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
public class AdminPlace extends Place {
  
	private String token;

	public AdminPlace(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	/**
	 * PlaceTokenizer knows how to serialize the Place's state to a URL token.
	 */
	@Prefix(value="admin")
	public static class Tokenizer implements PlaceTokenizer<AdminPlace> {

		@Override
		public String getToken(AdminPlace place) {
			return place.getToken();
		}

		@Override
		public AdminPlace getPlace(String token) {
			return new AdminPlace(token);
		}

	}
}
