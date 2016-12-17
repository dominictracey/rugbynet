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
	public enum Filter { ALL, UNDERWAY, CLIENT }
	private Filter filter;
	private String seps = "[=&]";
	
	public AdminCompPlace(String token) {
		this.token = token;
		String[] tok = token.split(seps);

		for (int i=0; i<tok.length; i+=2) {

			if (tok[i].toLowerCase().equals("filter")) {
				if (tok.length >= i+1)
					setFilter(Filter.valueOf(tok[i+1]));
			}
		}
	}

	public AdminCompPlace(Filter filter) {
		this.filter = filter;
	}

	public String getToken() {
		String retval = ""; 

		if (filter != null) {
			retval += "filter=" + filter.toString();	
		}
		
		return retval;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
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
